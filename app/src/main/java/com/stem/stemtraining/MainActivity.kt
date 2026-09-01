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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.ExerciseEntity
import com.stem.stemtraining.data.TrainingDatabase
import com.stem.stemtraining.data.WorkoutSetEntity
import com.stem.stemtraining.ui.theme.STEMTrainingTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { STEMTrainingTheme { TrainingScreen() } }
    }
}

@Composable
fun TrainingScreen() {
    val context = LocalContext.current
    val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }
    val exercises by dao.observeExercises().collectAsState(initial = emptyList())
    val allSets by dao.observeSets().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var addingExercise by remember { mutableStateOf(false) }
    var addingSetFor by remember { mutableStateOf<ExerciseEntity?>(null) }

    val setCount = allSets.size
    val volume = allSets.sumOf { it.weight * it.reps }

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
                WorkoutSummary(exercises.size, setCount, volume)
            }

            if (exercises.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Тренировка пока пустая", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Добавь первое упражнение и начни записывать рабочие подходы.")
                        }
                    }
                }
            }

            items(exercises, key = { it.id }) { exercise ->
                val sets = allSets.filter { it.exerciseId == exercise.id }
                ExerciseCard(
                    exercise = exercise,
                    sets = sets,
                    onAddSet = { addingSetFor = exercise }
                )
            }

            item {
                Button(
                    onClick = { addingExercise = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text("+  Добавить упражнение")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (addingExercise) {
        AddExerciseDialog(
            onDismiss = { addingExercise = false },
            onAdd = { name ->
                scope.launch { dao.insertExercise(ExerciseEntity(name = name.trim())) }
                addingExercise = false
            }
        )
    }

    addingSetFor?.let { exercise ->
        val previousSet = allSets.lastOrNull { it.exerciseId == exercise.id }
        AddSetDialog(
            exerciseName = exercise.name,
            previousSet = previousSet,
            onDismiss = { addingSetFor = null },
            onAdd = { weight, reps ->
                scope.launch {
                    dao.insertSet(
                        WorkoutSetEntity(
                            exerciseId = exercise.id,
                            weight = weight,
                            reps = reps
                        )
                    )
                }
                addingSetFor = null
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
private fun ExerciseCard(
    exercise: ExerciseEntity,
    sets: List<WorkoutSetEntity>,
    onAddSet: () -> Unit
) {
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

            if (sets.isEmpty()) {
                Text("Подходов пока нет", style = MaterialTheme.typography.bodyMedium)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    sets.takeLast(4).forEach { set ->
                        Column {
                            Text("${formatNumber(set.weight)} кг", fontWeight = FontWeight.Bold)
                            Text("${set.reps} повт", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddExerciseDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новое упражнение") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = { onAdd(name) }) {
                Text("Добавить")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun AddSetDialog(
    exerciseName: String,
    previousSet: WorkoutSetEntity?,
    onDismiss: () -> Unit,
    onAdd: (Double, Int) -> Unit
) {
    var weight by remember(previousSet) {
        mutableStateOf(previousSet?.let { formatNumber(it.weight) } ?: "")
    }
    var reps by remember(previousSet) {
        mutableStateOf(previousSet?.reps?.toString() ?: "")
    }
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
