package com.stem.stemtraining
import org.junit.Assert.*
import org.junit.Test

class TrainingMathTest{
    @Test fun epleyEstimateIsCorrect(){assertEquals(120.0,estimatedOneRepMax(100.0,6),0.001)}
    @Test fun volumeSumsWorkingSets(){assertEquals(2200.0,workoutVolume(listOf(100.0 to 10,80.0 to 15)),0.001)}
    @Test fun plateCalculatorUsesBothSides(){assertEquals(listOf(25.0,10.0,2.5),platesPerSide(95.0,20.0))}
    @Test fun plateCalculatorHandlesEmptyBar(){assertTrue(platesPerSide(20.0,20.0).isEmpty())}
}
