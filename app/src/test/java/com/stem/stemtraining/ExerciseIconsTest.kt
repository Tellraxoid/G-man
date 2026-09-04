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

    @Test fun newAbExercisesUseDedicatedIllustrations() {
        assertEquals(R.drawable.exercise_decline_crunch, exerciseIcon("Скручивания на обратной скамье"))
        assertEquals(R.drawable.exercise_parallel_bar_leg_raise, exerciseIcon("Подъём ног в висе на брусьях"))
    }

    @Test fun rowsShrugsAndShoulderPressesUseExactEquipment() {
        assertEquals(R.drawable.exercise_bent_over_barbell_row, exerciseIcon("Тяга штанги в наклоне"))
        assertEquals(R.drawable.exercise_one_arm_dumbbell_row, exerciseIcon("Тяга в наклоне одной рукой · гантель"))
        assertEquals(R.drawable.exercise_dumbbell_shrug, exerciseIcon("Шраги · гантели"))
        assertEquals(R.drawable.exercise_barbell_shrug, exerciseIcon("Шраги · штанга"))
        assertEquals(R.drawable.exercise_seated_barbell_press, exerciseIcon("Жим сидя · штанга"))
        assertEquals(R.drawable.exercise_seated_dumbbell_press, exerciseIcon("Жим сидя · гантели"))
    }

    @Test fun standingAndBentOverRaisesStayDistinct() {
        assertEquals(R.drawable.exercise_lateral_raise, exerciseIcon("Разведение рук с гантелями стоя"))
        assertEquals(R.drawable.exercise_bent_over_reverse_fly, exerciseIcon("Разведение рук с гантелями в стороны в наклоне"))
        assertNotEquals(exerciseIcon("Разведение рук с гантелями стоя"), exerciseIcon("Разведение рук с гантелями в стороны в наклоне"))
    }
}
