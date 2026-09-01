package com.stem.stemtraining.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null
)

@Entity(
    tableName = "exercises",
    indices = [Index("workoutId")]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val weight: Double,
    val reps: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface TrainingDao {
    @Query("SELECT * FROM workouts WHERE endedAt IS NULL ORDER BY startedAt DESC, id DESC LIMIT 1")
    fun observeActiveWorkout(): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts WHERE endedAt IS NOT NULL ORDER BY startedAt DESC, id DESC")
    fun observeCompletedWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY createdAt ASC, id ASC")
    fun observeExercises(workoutId: Long): Flow<List<ExerciseEntity>>

    @Query("SELECT workout_sets.* FROM workout_sets INNER JOIN exercises ON exercises.id = workout_sets.exerciseId WHERE exercises.workoutId = :workoutId ORDER BY workout_sets.createdAt ASC, workout_sets.id ASC")
    fun observeSets(workoutId: Long): Flow<List<WorkoutSetEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Query("UPDATE workouts SET endedAt = :endedAt WHERE id = :workoutId AND endedAt IS NULL")
    suspend fun finishWorkout(workoutId: Long, endedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM workout_sets WHERE id = :setId")
    suspend fun deleteSet(setId: Long)

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)
}

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class, WorkoutSetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var INSTANCE: TrainingDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val now = System.currentTimeMillis()
                db.execSQL("CREATE TABLE IF NOT EXISTS workouts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, startedAt INTEGER NOT NULL, endedAt INTEGER)")
                db.execSQL("INSERT INTO workouts (id, startedAt, endedAt) VALUES (1, $now, $now)")
                db.execSQL("ALTER TABLE exercises ADD COLUMN workoutId INTEGER NOT NULL DEFAULT 1")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_exercises_workoutId ON exercises(workoutId)")
            }
        }

        fun getInstance(context: Context): TrainingDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrainingDatabase::class.java,
                    "stem_training.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
