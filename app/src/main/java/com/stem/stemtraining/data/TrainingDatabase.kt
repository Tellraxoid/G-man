package com.stem.stemtraining.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workouts") data class WorkoutEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val startedAt: Long = System.currentTimeMillis(), val endedAt: Long? = null)
@Entity(tableName = "exercises", indices = [Index("workoutId")]) data class ExerciseEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val workoutId: Long, val name: String, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "workout_sets", foreignKeys = [ForeignKey(entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"], onDelete = ForeignKey.CASCADE)], indices = [Index("exerciseId")]) data class WorkoutSetEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val exerciseId: Long, val weight: Double, val reps: Int, val createdAt: Long = System.currentTimeMillis())
data class WorkoutSummaryRow(val workoutId: Long, val exerciseCount: Int, val setCount: Int, val volume: Double)
data class ExerciseProgressRow(val name: String, val sessions: Int, val sets: Int, val bestWeight: Double, val bestEstimated1Rm: Double, val totalVolume: Double)

@Dao interface TrainingDao {
    @Query("SELECT * FROM workouts WHERE endedAt IS NULL ORDER BY startedAt DESC, id DESC LIMIT 1") fun observeActiveWorkout(): Flow<WorkoutEntity?>
    @Query("SELECT * FROM workouts WHERE endedAt IS NOT NULL ORDER BY startedAt DESC, id DESC") fun observeCompletedWorkouts(): Flow<List<WorkoutEntity>>
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY createdAt ASC, id ASC") fun observeExercises(workoutId: Long): Flow<List<ExerciseEntity>>
    @Query("SELECT workout_sets.* FROM workout_sets INNER JOIN exercises ON exercises.id = workout_sets.exerciseId WHERE exercises.workoutId = :workoutId ORDER BY workout_sets.createdAt ASC, workout_sets.id ASC") fun observeSets(workoutId: Long): Flow<List<WorkoutSetEntity>>
    @Query("SELECT workouts.id AS workoutId, COUNT(DISTINCT exercises.id) AS exerciseCount, COUNT(workout_sets.id) AS setCount, COALESCE(SUM(workout_sets.weight * workout_sets.reps), 0.0) AS volume FROM workouts LEFT JOIN exercises ON exercises.workoutId = workouts.id LEFT JOIN workout_sets ON workout_sets.exerciseId = exercises.id WHERE workouts.endedAt IS NOT NULL GROUP BY workouts.id") fun observeCompletedSummaries(): Flow<List<WorkoutSummaryRow>>
    @Query("SELECT exercises.name AS name, COUNT(DISTINCT exercises.workoutId) AS sessions, COUNT(workout_sets.id) AS sets, COALESCE(MAX(workout_sets.weight),0.0) AS bestWeight, COALESCE(MAX(workout_sets.weight * (1.0 + workout_sets.reps / 30.0)),0.0) AS bestEstimated1Rm, COALESCE(SUM(workout_sets.weight * workout_sets.reps),0.0) AS totalVolume FROM exercises LEFT JOIN workout_sets ON workout_sets.exerciseId = exercises.id INNER JOIN workouts ON workouts.id = exercises.workoutId WHERE workouts.endedAt IS NOT NULL GROUP BY exercises.name ORDER BY bestEstimated1Rm DESC, exercises.name ASC") fun observeExerciseProgress(): Flow<List<ExerciseProgressRow>>
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertWorkout(workout: WorkoutEntity): Long
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertExercise(exercise: ExerciseEntity): Long
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertSet(set: WorkoutSetEntity): Long
    @Update suspend fun updateSet(set: WorkoutSetEntity)
    @Query("UPDATE workouts SET endedAt = :endedAt WHERE id = :workoutId AND endedAt IS NULL") suspend fun finishWorkout(workoutId: Long, endedAt: Long = System.currentTimeMillis())
    @Query("DELETE FROM workout_sets WHERE id = :setId") suspend fun deleteSet(setId: Long)
    @Query("DELETE FROM exercises WHERE id = :exerciseId") suspend fun deleteExercise(exerciseId: Long)
}

@Database(entities = [WorkoutEntity::class, ExerciseEntity::class, WorkoutSetEntity::class], version = 2, exportSchema = false)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
    companion object {
        @Volatile private var INSTANCE: TrainingDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) { val now=System.currentTimeMillis(); db.execSQL("CREATE TABLE IF NOT EXISTS workouts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, startedAt INTEGER NOT NULL, endedAt INTEGER)"); db.execSQL("INSERT INTO workouts (id, startedAt, endedAt) VALUES (1, $now, $now)"); db.execSQL("ALTER TABLE exercises ADD COLUMN workoutId INTEGER NOT NULL DEFAULT 1"); db.execSQL("CREATE INDEX IF NOT EXISTS index_exercises_workoutId ON exercises(workoutId)") } }
        fun getInstance(context: Context): TrainingDatabase = INSTANCE ?: synchronized(this) { INSTANCE ?: Room.databaseBuilder(context.applicationContext, TrainingDatabase::class.java, "stem_training.db").addMigrations(MIGRATION_1_2).build().also { INSTANCE=it } }
    }
}
