package com.stem.stemtraining

import org.junit.Assert.assertEquals
import org.junit.Test

class HealthPresentationTest {
    @Test fun knownSourcesHaveReadableNames() {
        assertEquals("01.09.2026 17:06 · Hevy", friendlyHealthDetail("01.09.2026 17:06 · com.hevy"))
        assertEquals("Google Fit, Mi Health", friendlyHealthDetail("com.google.android.apps.fitness, com.mi.health"))
    }
    @Test fun unknownSourcesAndDatesArePreserved() {
        val detail = "01.09.2026 18:12 — 02.09.2026 18:12 · org.example.tracker"
        assertEquals(detail, friendlyHealthDetail(detail))
    }
    @Test fun similarPackageIsNotMislabelled() {
        assertEquals("com.hevy.other", friendlyHealthDetail("com.hevy.other"))
    }
    @Test fun nutritionQualifierSurvivesFormatting() {
        assertEquals("Hevy. Это запись, не суточная сумма.", friendlyHealthDetail("com.hevy. Это запись, не суточная сумма."))
    }
}
