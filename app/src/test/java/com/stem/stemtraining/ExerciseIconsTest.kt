package com.stem.stemtraining

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ExerciseIconsTest {
    @Test fun chestPressVariantsUseTheirOwnIllustrations() {
        assertEquals(R.drawable.exercise_bench, exerciseIcon("Жим лёжа · штанга"))
        assertEquals(R.drawable.exercise_dumbbell_bench, exerciseIcon("Жим лёжа · гантели"))
        assertEquals(R.drawable.exercise_incline_barbell_press, exerciseIcon("Жим лёжа на наклонной скамье · штанга"))
        assertEquals(R.drawable.exercise_incline_dumbbell_press, exerciseIcon("Жим лёжа на наклонной скамье · гантели"))
        assertEquals(R.drawable.exercise_incline_fly, exerciseIcon("Разведение рук на наклонной скамье · гантели"))
    }

    @Test fun curlAndCoreVariantsAreNotReused() {
        val barbell = exerciseIcon("Сгибание рук · штанга")
        val dumbbells = exerciseIcon("Сгибание рук · гантели")
        val hammer = exerciseIcon("Молотковые сгибания · гантели")
        assertNotEquals(barbell, dumbbells)
        assertNotEquals(dumbbells, hammer)
        assertEquals(R.drawable.exercise_crunch, exerciseIcon("Скручивания"))
        assertNotEquals(exerciseIcon("Скручивания"), exerciseIcon("Планка"))
    }

    @Test fun newBenchHangingAndDipExercisesUseDedicatedIllustrations() {
        assertEquals(R.drawable.exercise_preacher_curl, exerciseIcon("Сгибание рук на скамье Скотта"))
        assertEquals(R.drawable.exercise_hanging_leg_raise, exerciseIcon("Подъём ног в висе"))
        assertEquals(R.drawable.exercise_dips, exerciseIcon("Отжимания на брусьях"))
    }
}
