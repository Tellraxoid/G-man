package com.stem.stemtraining
import com.stem.stemtraining.data.PreviousWorkoutSetRow
import org.junit.Assert.*
import org.junit.Test

class TrainingMathTest{
    @Test fun epleyEstimateIsCorrect(){assertEquals(120.0,estimatedOneRepMax(100.0,6),0.001)}
    @Test fun volumeSumsWorkingSets(){assertEquals(2200.0,workoutVolume(listOf(100.0 to 10,80.0 to 15)),0.001)}
    @Test fun plateCalculatorUsesBothSides(){assertEquals(listOf(25.0,10.0,2.5),platesPerSide(95.0,20.0))}
    @Test fun plateCalculatorHandlesEmptyBar(){assertTrue(platesPerSide(20.0,20.0).isEmpty())}
    @Test fun trueSingleRepIsNotOverestimated(){assertEquals(100.0,estimatedOneRepMax(100.0,1),0.001)}
    @Test fun nextWeightRespectsEffort(){
        fun sets(effort:String)=List(3){PreviousWorkoutSetRow(80.0,10,effort)}
        assertEquals(80.0,suggestedNextWeight(sets("Тяжело"),2.5)!!.weight,0.001)
        assertEquals(82.5,suggestedNextWeight(sets("Нормально"),2.5)!!.weight,0.001)
        assertEquals(85.0,suggestedNextWeight(sets("Легко"),2.5)!!.weight,0.001)
    }
    @Test fun incompleteEffortDoesNotIncreaseWeight(){
        val sets=listOf(PreviousWorkoutSetRow(60.0,10,"Легко"),PreviousWorkoutSetRow(60.0,9,null))
        assertEquals(60.0,suggestedNextWeight(sets,2.5)!!.weight,0.001)
    }
    @Test fun goalControlsSetsAndRepRange(){
        val r=workoutRecommendation(emptyList(),TrainingGoal.ENDURANCE,95.0,2.5)
        assertEquals(3,r.sets);assertEquals(15..20,r.reps);assertNull(r.weight)
    }
    @Test fun failureReducesRecommendedWeight(){
        val previous=List(3){PreviousWorkoutSetRow(80.0,8,"До отказа")}
        assertEquals(77.5,workoutRecommendation(previous,TrainingGoal.MUSCLE_GAIN,95.0,2.5).weight!!,0.001)
    }
    @Test fun bodyWeightAddsRelativeContext(){
        val previous=List(3){PreviousWorkoutSetRow(95.0,12,"Нормально")}
        assertEquals(103,workoutRecommendation(previous,TrainingGoal.RECOMPOSITION,95.0,2.5).relativeLoadPercent)
    }
    @Test fun normalEffortAddsOneRepBeforeIncreasingWeight(){
        val previous=List(3){PreviousWorkoutSetRow(80.0,10,"Нормально")}
        val result=workoutRecommendation(previous,TrainingGoal.MUSCLE_GAIN,95.0,2.5)
        assertEquals(80.0,result.weight!!,0.001);assertEquals(11,result.recommendedReps)
    }
    @Test fun weightIncreaseResetsRepsToBottomOfRange(){
        val previous=List(3){PreviousWorkoutSetRow(80.0,12,"Нормально")}
        val result=workoutRecommendation(previous,TrainingGoal.MUSCLE_GAIN,95.0,2.5)
        assertEquals(82.5,result.weight!!,0.001);assertEquals(8,result.recommendedReps)
    }
    @Test fun heavyEffortDoesNotAddReps(){
        val previous=List(3){PreviousWorkoutSetRow(80.0,9,"Тяжело")}
        assertEquals(9,workoutRecommendation(previous,TrainingGoal.MUSCLE_GAIN,95.0,2.5).recommendedReps)
    }
    @Test fun movesExerciseToRequestedPosition(){
        assertEquals(listOf("B","C","A"),moved(listOf("A","B","C"),0,2))
        assertEquals(listOf("A","B","C"),moved(listOf("A","B","C"),-1,2))
    }
}
