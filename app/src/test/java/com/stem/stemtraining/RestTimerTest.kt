package com.stem.stemtraining

import org.junit.Assert.assertEquals
import org.junit.Test

class RestTimerTest {
    @Test fun remainingRoundsUp() {
        assertEquals(1L, restSecondsLeft(1001, 1000))
        assertEquals(90L, restSecondsLeft(91000, 1000))
    }
    @Test fun expiredNeverGoesNegative() {
        assertEquals(0L, restSecondsLeft(1000, 1000))
        assertEquals(0L, restSecondsLeft(0, 1000))
    }
    @Test fun durationSurvivesTimeAwayFromScreen() {
        assertEquals(25L, restSecondsLeft(91000, 66000))
        assertEquals(0L, restSecondsLeft(91000, 92000))
    }
    @Test fun clockUsesPaddedSeconds() {
        assertEquals("1:30", restClock(90))
        assertEquals("0:05", restClock(5))
        assertEquals("0:00", restClock(0))
        assertEquals("10:00", restClock(600))
    }
}
