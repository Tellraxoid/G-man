package com.stem.stemtraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.ui.theme.STEMTrainingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STEMTrainingTheme {
                TrainingScreen()
            }
        }
    }
}

data class WorkoutSet(val weight: String, val reps: Int)
data class ExerciseUi(val name: String, val sets: List<WorkoutSet>)

private val sampleExercises = listOf(
    ExerciseUi("Тяга в наклоне · штанга", listOf(WorkoutSet("100 кг", 12), WorkoutSet("100 кг", 12), WorkoutSet("100 кг", 12))),
    ExerciseUi("Тяга в наклоне одной рукой · гантель", listOf(WorkoutSet("35 кг", 12), WorkoutSet("42.5 кг", 8), WorkoutSet("42.5 кг", 8))),
    ExerciseUi("Шраги · гантели", listOf(WorkoutSet("42.5 кг", 15), WorkoutSet("42.5 кг", 15), WorkoutSet("42.5 кг", 15))),
    ExerciseUi("Разведение рук в наклоне · гантели", listOf(WorkoutSet("15 кг", 12), WorkoutSet("15 кг", 12), WorkoutSet("15 кг", 12)))
)

@Composable
fun TrainingScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("S.T.E.M. TRAINING", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Сегодня", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Text("⋮", style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(Modifier.height(16.dp))
                WorkoutSummary()
                Spacer(Modifier.height(8.dp))
                Text("Спина   Плечи", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            items(sampleExercises) { exercise ->
                ExerciseCard(exercise)
            }

            item {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text("+  Добавить упражнение")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun WorkoutSummary() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryValue("4", "упр.")
            SummaryValue("12", "подх.")
            SummaryValue("8 640", "кг")
            SummaryValue("42", "мин")
        }
    }
}

@Composable
private fun SummaryValue(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ExerciseCard(exercise: ExerciseUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    exercise.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text("＋", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Text("  ⋮", style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                exercise.sets.forEach { set ->
                    Column {
                        Text(set.weight, fontWeight = FontWeight.Bold)
                        Text("${set.reps} повт", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainingScreenPreview() {
    STEMTrainingTheme {
        TrainingScreen()
    }
}
