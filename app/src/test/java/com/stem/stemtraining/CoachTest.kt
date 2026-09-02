package com.stem.stemtraining
import com.stem.stemtraining.data.WorkoutSetEntity
import org.junit.Assert.*
import org.junit.Test
class CoachTest {
    private fun set(effort:String?,reps:Int=10,warmup:Boolean=false)=WorkoutSetEntity(exerciseId=1,weight=40.0,reps=reps,effort=effort,isWarmup=warmup)
    @Test fun noAdviceFromWarmups(){assertTrue(coachAdvice(listOf(set("Легко",warmup=true)),10).contains("нет рабочих"))}
    @Test fun heavyPreventsProgression(){assertTrue(coachAdvice(listOf(set("Легко"),set("Тяжело")),10).contains("Не спешите"))}
    @Test fun allEasyAndTargetsMet(){assertTrue(coachAdvice(listOf(set("Легко"),set("Легко")),10).contains("одно повторение"))}
    @Test fun missingEffortIsNotEasy(){assertFalse(coachAdvice(listOf(set(null)),10).contains("одно повторение"))}
    @Test fun missedTargetDoesNotProgress(){assertFalse(coachAdvice(listOf(set("Легко",8)),10).contains("одно повторение"))}
}
