package com.stem.stemtraining
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stem.stemtraining.data.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class AssistDatabaseTest {
    @Test fun effortsAndSupersetsSurviveReadsAndDeleteSafely()=runBlocking {
        val db=Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext,TrainingDatabase::class.java).build()
        try {
            val dao=db.trainingDao()
            val workout=dao.insertWorkout(WorkoutEntity())
            val first=dao.insertExercise(ExerciseEntity(workoutId=workout,name="A",supersetNext=true))
            val second=dao.insertExercise(ExerciseEntity(workoutId=workout,name="B"))
            dao.insertSet(WorkoutSetEntity(exerciseId=first,weight=40.0,reps=10,effort="Легко"))
            assertTrue(dao.allExercises().first().supersetNext)
            assertEquals("Легко",dao.allSets().first().effort)
            dao.deleteExercise(second)
            assertFalse(dao.allExercises().first().supersetNext)
            assertEquals(1,dao.allSets().size)
        } finally {db.close()}
    }
}
