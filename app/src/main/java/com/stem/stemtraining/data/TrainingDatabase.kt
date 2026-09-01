package com.stem.stemtraining.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workouts") data class WorkoutEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val startedAt: Long = System.currentTimeMillis(), val endedAt: Long? = null)
@Entity(tableName = "exercises", indices = [Index("workoutId")], foreignKeys = [ForeignKey(entity = WorkoutEntity::class, parentColumns = ["id"], childColumns = ["workoutId"], onDelete = ForeignKey.CASCADE)]) data class ExerciseEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val workoutId: Long, val name: String, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "workout_sets", indices = [Index("exerciseId")], foreignKeys = [ForeignKey(entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"], onDelete = ForeignKey.CASCADE)]) data class WorkoutSetEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val exerciseId: Long, val weight: Double, val reps: Int, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "programs") data class ProgramEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "program_exercises", indices = [Index("programId")], foreignKeys = [ForeignKey(entity = ProgramEntity::class, parentColumns = ["id"], childColumns = ["programId"], onDelete = ForeignKey.CASCADE)]) data class ProgramExerciseEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val programId: Long, val name: String, val position: Int)

data class WorkoutSummaryRow(val workoutId: Long, val exerciseCount: Int, val setCount: Int, val volume: Double)
data class ExerciseProgressRow(val name: String, val sessions: Int, val sets: Int, val bestWeight: Double, val bestEstimated1Rm: Double, val totalVolume: Double)
data class ProgramWithExercises(@Embedded val program: ProgramEntity, @Relation(parentColumn = "id", entityColumn = "programId") val exercises: List<ProgramExerciseEntity>)

@Dao interface TrainingDao {
    @Query("SELECT * FROM workouts WHERE endedAt IS NULL ORDER BY startedAt DESC, id DESC LIMIT 1") fun observeActiveWorkout(): Flow<WorkoutEntity?>
    @Query("SELECT * FROM workouts WHERE endedAt IS NOT NULL ORDER BY startedAt DESC, id DESC") fun observeCompletedWorkouts(): Flow<List<WorkoutEntity>>
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY createdAt, id") fun observeExercises(workoutId: Long): Flow<List<ExerciseEntity>>
    @Query("SELECT workout_sets.* FROM workout_sets INNER JOIN exercises ON exercises.id = workout_sets.exerciseId WHERE exercises.workoutId = :workoutId ORDER BY workout_sets.createdAt, workout_sets.id") fun observeSets(workoutId: Long): Flow<List<WorkoutSetEntity>>
    @Query("SELECT workouts.id AS workoutId, COUNT(DISTINCT exercises.id) AS exerciseCount, COUNT(workout_sets.id) AS setCount, COALESCE(SUM(workout_sets.weight * workout_sets.reps), 0.0) AS volume FROM workouts LEFT JOIN exercises ON exercises.workoutId = workouts.id LEFT JOIN workout_sets ON workout_sets.exerciseId = exercises.id WHERE workouts.endedAt IS NOT NULL GROUP BY workouts.id") fun observeCompletedSummaries(): Flow<List<WorkoutSummaryRow>>
    @Query("SELECT exercises.name AS name, COUNT(DISTINCT exercises.workoutId) AS sessions, COUNT(workout_sets.id) AS sets, COALESCE(MAX(workout_sets.weight),0.0) AS bestWeight, COALESCE(MAX(workout_sets.weight * (1.0 + workout_sets.reps / 30.0)),0.0) AS bestEstimated1Rm, COALESCE(SUM(workout_sets.weight * workout_sets.reps),0.0) AS totalVolume FROM exercises LEFT JOIN workout_sets ON workout_sets.exerciseId = exercises.id INNER JOIN workouts ON workouts.id = exercises.workoutId WHERE workouts.endedAt IS NOT NULL GROUP BY exercises.name ORDER BY bestEstimated1Rm DESC, exercises.name") fun observeExerciseProgress(): Flow<List<ExerciseProgressRow>>
    @Transaction @Query("SELECT * FROM programs ORDER BY createdAt, id") fun observePrograms(): Flow<List<ProgramWithExercises>>
    @Insert suspend fun insertWorkout(workout: WorkoutEntity): Long
    @Insert suspend fun insertExercise(exercise: ExerciseEntity): Long
    @Insert suspend fun insertSet(set: WorkoutSetEntity): Long
    @Insert suspend fun insertProgram(program: ProgramEntity): Long
    @Insert suspend fun insertProgramExercises(exercises: List<ProgramExerciseEntity>)
    @Update suspend fun updateSet(set: WorkoutSetEntity)
    @Update suspend fun updateExercise(exercise: ExerciseEntity)
    @Update suspend fun updateProgram(program: ProgramEntity)
    @Query("UPDATE workouts SET endedAt = :endedAt WHERE id = :workoutId") suspend fun finishWorkout(workoutId: Long, endedAt: Long = System.currentTimeMillis())
    @Query("DELETE FROM workout_sets WHERE id = :id") suspend fun deleteSet(id: Long)
    @Query("DELETE FROM exercises WHERE id = :id") suspend fun deleteExercise(id: Long)
    @Query("DELETE FROM workouts WHERE id = :id") suspend fun deleteWorkout(id: Long)
    @Query("DELETE FROM program_exercises WHERE programId = :programId") suspend fun clearProgramExercises(programId: Long)
    @Query("DELETE FROM programs WHERE id = :id") suspend fun deleteProgram(id: Long)
    @Query("SELECT COUNT(*) FROM programs") suspend fun programCount(): Int
    @Transaction suspend fun saveProgram(program: ProgramEntity, names: List<String>): Long { val id = if (program.id == 0L) insertProgram(program) else { updateProgram(program); clearProgramExercises(program.id); program.id }; insertProgramExercises(names.mapIndexed { index, name -> ProgramExerciseEntity(programId = id, name = name, position = index) }); return id }
}

@Database(entities = [WorkoutEntity::class, ExerciseEntity::class, WorkoutSetEntity::class, ProgramEntity::class, ProgramExerciseEntity::class], version = 4, exportSchema = false)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
    companion object {
        @Volatile private var instance: TrainingDatabase? = null
        private val migration1To2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) { val now = System.currentTimeMillis(); db.execSQL("CREATE TABLE IF NOT EXISTS workouts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, startedAt INTEGER NOT NULL, endedAt INTEGER)"); db.execSQL("INSERT INTO workouts (id, startedAt, endedAt) VALUES (1, $now, $now)"); db.execSQL("ALTER TABLE exercises ADD COLUMN workoutId INTEGER NOT NULL DEFAULT 1"); db.execSQL("CREATE INDEX IF NOT EXISTS index_exercises_workoutId ON exercises(workoutId)") } }
        private val migration2To3 = object : Migration(2, 3) { override fun migrate(db: SupportSQLiteDatabase) { db.execSQL("CREATE TABLE IF NOT EXISTS programs (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, createdAt INTEGER NOT NULL)"); db.execSQL("CREATE TABLE IF NOT EXISTS program_exercises (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, programId INTEGER NOT NULL, name TEXT NOT NULL, position INTEGER NOT NULL, FOREIGN KEY(programId) REFERENCES programs(id) ON UPDATE NO ACTION ON DELETE CASCADE)"); db.execSQL("CREATE INDEX IF NOT EXISTS index_program_exercises_programId ON program_exercises(programId)") } }
        private val migration3To4 = object : Migration(3, 4) { override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE exercises_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, workoutId INTEGER NOT NULL, name TEXT NOT NULL, createdAt INTEGER NOT NULL, FOREIGN KEY(workoutId) REFERENCES workouts(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
            db.execSQL("INSERT INTO exercises_new (id, workoutId, name, createdAt) SELECT id, workoutId, name, createdAt FROM exercises")
            db.execSQL("CREATE TABLE workout_sets_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, exerciseId INTEGER NOT NULL, weight REAL NOT NULL, reps INTEGER NOT NULL, createdAt INTEGER NOT NULL, FOREIGN KEY(exerciseId) REFERENCES exercises(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
            db.execSQL("INSERT INTO workout_sets_new (id, exerciseId, weight, reps, createdAt) SELECT id, exerciseId, weight, reps, createdAt FROM workout_sets")
            db.execSQL("DROP TABLE workout_sets")
            db.execSQL("DROP TABLE exercises")
            db.execSQL("ALTER TABLE exercises_new RENAME TO exercises")
            db.execSQL("ALTER TABLE workout_sets_new RENAME TO workout_sets")
            db.execSQL("CREATE INDEX index_exercises_workoutId ON exercises(workoutId)")
            db.execSQL("CREATE INDEX index_workout_sets_exerciseId ON workout_sets(exerciseId)")
        } }
        fun getInstance(context: Context): TrainingDatabase = instance ?: synchronized(this) { instance ?: Room.databaseBuilder(context.applicationContext, TrainingDatabase::class.java, "stem_training.db").addMigrations(migration1To2, migration2To3, migration3To4).build().also { instance = it } }
    }
}
