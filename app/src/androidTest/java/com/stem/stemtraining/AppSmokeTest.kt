package com.stem.stemtraining
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class AppSmokeTest{
    @get:Rule val rule=createAndroidComposeRule<MainActivity>()
    private fun ready(){val buttons=rule.onAllNodesWithText("Начать").fetchSemanticsNodes();if(buttons.isNotEmpty())rule.onNodeWithText("Начать").performClick();rule.waitForIdle()}
    @Test fun bottomNavigationExposesAllDestinations(){ready();listOf("Тренировка","Календарь","Программы","Прогресс","Настройки").forEach{rule.onNode(hasText(it) and hasClickAction()).assertExists()}}
    @Test fun navigationIconsHaveAccessibleDescriptions(){ready();listOf("Тренировка","Календарь","Программы","Прогресс","Настройки").forEach{rule.onNodeWithContentDescription(it,useUnmergedTree=true).assertExists()}}
}
