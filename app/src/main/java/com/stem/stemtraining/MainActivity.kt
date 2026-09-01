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
import androidx.compose.ui.window.Dialog
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

data class CatalogExercise(val name: String, val muscle: String)

private val exerciseCatalog = listOf(
    CatalogExercise("Жим лёжа · штанга", "Грудь"),
    CatalogExercise("Жим на наклонной скамье · штанга", "Грудь"),
    CatalogExercise("Жим лёжа · гантели", "Грудь"),
    CatalogExercise("Разведение рук лёжа · гантели", "Грудь"),
    CatalogExercise("Отжимания", "Грудь"),
    CatalogExercise("Тяга в наклоне · штанга", "Спина"),
    CatalogExercise("Тяга в наклоне одной рукой · гантель", "Спина"),
    CatalogExercise("Тяга верхнего блока", "Спина"),
    CatalogExercise("Тяга горизонтального блока", "Спина"),
    CatalogExercise("Подтягивания", "Спина"),
    CatalogExercise("Приседания · штанга", "Ноги"),
    CatalogExercise("Выпады · гантели", "Ноги"),
    CatalogExercise("Жим ногами", "Ноги"),
    CatalogExercise("Сгибание ног", "Ноги"),
    CatalogExercise("Разгибание ног", "Ноги"),
    CatalogExercise("Подъём на носки стоя", "Икры"),
    CatalogExercise("Жим сидя · штанга", "Плечи"),
    CatalogExercise("Жим сидя · гантели", "Плечи"),
    CatalogExercise("Разведение рук в стороны · гантели", "Плечи"),
    CatalogExercise("Разведение рук в наклоне · гантели", "Плечи"),
    CatalogExercise("Шраги · гантели", "Трапеции"),
    CatalogExercise("Сгибание рук · штанга", "Бицепс"),
    CatalogExercise("Сгибание рук · гантели", "Бицепс"),
    CatalogExercise("Молотковые сгибания · гантели", "Бицепс"),
    CatalogExercise("Жим лёжа узким хватом · штанга", "Трицепс"),
    CatalogExercise("Разгибание рук на блоке", "Трицепс"),
    CatalogExercise("Французский жим · гантель", "Трицепс"),
    CatalogExercise("Скручивания", "Пресс"),
    CatalogExercise("Подъём ног", "Пресс"),
    CatalogExercise("Планка", "Пресс")
)

@Composable
fun TrainingScreen() {
    val context = LocalContext.current
    val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }
    val exercises by dao.observeExercises().collectAsState(initial = emptyList())
    val allSets by dao.observeSets().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showCatalog by remember { mutableStateOf(false) }
    var addingSetFor by remember { mutableStateOf<ExerciseEntity?>(null) }

    val setCount = allSets.size
    val volume = allSets.sumOf { it.weight * it.reps }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
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
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Тренировка пока пустая", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Выбери упражнение из каталога и начни записывать подходы.")
                        }
                    }
                }
            }

            items(exercises, key = { it.id }) { exercise ->
                ExerciseCard(exercise, allSets.filter { it.exerciseId == exercise.id }) { addingSetFor = exercise }
            }

            item {
                Button(onClick = { showCatalog = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text("+  Добавить упражнение")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showCatalog) {
        ExerciseCatalogDialog(
            existingNames = exercises.map { it.name }.toSet(),
            onDismiss = { showCatalog = false },
            onSelect = { name ->
                scope.launch { dao.insertExercise(ExerciseEntity(name = name)) }
                showCatalog = false
            }
        )
    }

    addingSetFor?.let { exercise ->
        val previousSet = allSets.lastOrNull { it.exerciseId == exercise.id }
        AddSetDialog(exercise.name, previousSet, { addingSetFor = null }) { weight, reps ->
            scope.launch { dao.insertSet(WorkoutSetEntity(exerciseId = exercise.id, weight = weight, reps = reps)) }
            addingSetFor = null
        }
    }
}

@Composable
private fun ExerciseCatalogDialog(existingNames: Set<String>, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    var customName by remember { mutableStateOf("") }
    val filtered = exerciseCatalog.filter {
        (it.name.contains(search, ignoreCase = true) || it.muscle.contains(search, ignoreCase = true)) && it.name !in existingNames
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Каталог упражнений", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = search, onValueChange = { search = it }, label = { Text("Поиск или группа мышц") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                LazyColumn(modifier = Modifier.height(360.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(filtered) { exercise ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onSelect(exercise.name) }.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(exercise.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            Text(exercise.muscle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Нет в каталоге?", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = customName, onValueChange = { customName = it }, label = { Text("Своё упражнение") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    TextButton(enabled = customName.isNotBlank() && customName.trim() !in existingNames, onClick = { onSelect(customName.trim()) }) { Text("Добавить своё") }
                }
            }
        }
    }
}

@Composable
private fun WorkoutSummary(exerciseCount: Int, setCount: Int, volume: Double) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
private fun ExerciseCard(exercise: ExerciseEntity, sets: List<WorkoutSetEntity>, onAddSet: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(exercise.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("＋", modifier = Modifier.clickable { onAddSet() }.padding(6.dp), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Text("⋮", modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.height(14.dp))
            if (sets.isEmpty()) Text("Подходов пока нет", style = MaterialTheme.typography.bodyMedium)
            else Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
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

@Composable
private fun AddSetDialog(exerciseName: String, previousSet: WorkoutSetEntity?, onDismiss: () -> Unit, onAdd: (Double, Int) -> Unit) {
    var weight by remember(previousSet) { mutableStateOf(previousSet?.let { formatNumber(it.weight) } ?: "") }
    var reps by remember(previousSet) { mutableStateOf(previousSet?.reps?.toString() ?: "") }
    val valid = weight.replace(',', '.').toDoubleOrNull() != null && reps.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый подход") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(exerciseName, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Вес, кг") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = reps, onValueChange = { reps = it.filter(Char::isDigit) }, label = { Text("Повторения") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onAdd(weight.replace(',', '.').toDouble(), reps.toInt()) }) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun formatNumber(value: Double): String = if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
