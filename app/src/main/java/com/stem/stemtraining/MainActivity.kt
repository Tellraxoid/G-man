package com.stem.stemtraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.ui.theme.STEMTrainingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { STEMTrainingTheme { TrainingScreen() } }
    }
}

data class WorkoutSet(val weight: Double, val reps: Int)
data class ExerciseUi(val name: String, val sets: List<WorkoutSet>)

private val initialExercises = listOf(
    ExerciseUi("Тяга в наклоне · штанга", listOf(WorkoutSet(100.0, 12), WorkoutSet(100.0, 12), WorkoutSet(100.0, 12))),
    ExerciseUi("Тяга в наклоне одной рукой · гантель", listOf(WorkoutSet(35.0, 12), WorkoutSet(42.5, 8), WorkoutSet(42.5, 8))),
    ExerciseUi("Шраги · гантели", listOf(WorkoutSet(42.5, 15), WorkoutSet(42.5, 15), WorkoutSet(42.5, 15))),
    ExerciseUi("Разведение рук в наклоне · гантели", listOf(WorkoutSet(15.0, 12), WorkoutSet(15.0, 12), WorkoutSet(15.0, 12)))
)

@Composable
fun TrainingScreen() {
    val exercises = remember { mutableStateListOf<ExerciseUi>().apply { addAll(initialExercises) } }
    var editingExerciseIndex by remember { mutableStateOf<Int?>(null) }

    val setCount = exercises.sumOf { it.sets.size }
    val volume = exercises.sumOf { exercise -> exercise.sets.sumOf { it.weight * it.reps } }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
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
                WorkoutSummary(exercises.size, setCount, volume)
                Spacer(Modifier.height(8.dp))
                Text("Спина   Плечи", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            itemsIndexed(exercises) { index, exercise ->
                ExerciseCard(exercise, onAddSet = { editingExerciseIndex = index })
            }

            item {
                Button(onClick = { }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text("+  Добавить упражнение")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    editingExerciseIndex?.let { index ->
        AddSetDialog(
            exerciseName = exercises[index].name,
            previousSet = exercises[index].sets.lastOrNull(),
            onDismiss = { editingExerciseIndex = null },
            onAdd = { weight, reps ->
                val exercise = exercises[index]
                exercises[index] = exercise.copy(sets = exercise.sets + WorkoutSet(weight, reps))
                editingExerciseIndex = null
            }
        )
    }
}

@Composable
private fun WorkoutSummary(exerciseCount: Int, setCount: Int, volume: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryValue(exerciseCount.toString(), "упр.")
            SummaryValue(setCount.toString(), "подх.")
            SummaryValue(formatNumber(volume), "кг")
            SummaryValue("—", "мин")
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
private fun ExerciseCard(exercise: ExerciseUi, onAddSet: () -> Unit) {
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
                Text(
                    "＋",
                    modifier = Modifier.clickable { onAddSet() }.padding(6.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("⋮", modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                exercise.sets.takeLast(4).forEach { set ->
                    Column {
                        Text("${formatNumber(set.weight)} кг", fontWeight = FontWeight.Bold)
                        Text("${set.reps} повт", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSetDialog(
    exerciseName: String,
    previousSet: WorkoutSet?,
    onDismiss: () -> Unit,
    onAdd: (Double, Int) -> Unit
) {
    var weight by remember(previousSet) { mutableStateOf(previousSet?.let { formatNumber(it.weight) } ?: "") }
    var reps by remember(previousSet) { mutableStateOf(previousSet?.reps?.toString() ?: "") }
    val valid = weight.replace(',', '.').toDoubleOrNull() != null && reps.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый подход") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(exerciseName, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Вес, кг") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter(Char::isDigit) },
                    label = { Text("Повторения") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = {
                    onAdd(weight.replace(',', '.').toDouble(), reps.toInt())
                }
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun formatNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

@Preview(showBackground = true)
@Composable
private fun TrainingScreenPreview() {
    STEMTrainingTheme { TrainingScreen() }
}
