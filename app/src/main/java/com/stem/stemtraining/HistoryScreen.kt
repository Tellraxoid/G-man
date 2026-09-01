package com.stem.stemtraining

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable fun HistoryScreen() {
    val context = LocalContext.current; val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }; val scope = rememberCoroutineScope()
    val workouts by dao.observeCompletedWorkouts().collectAsState(initial = emptyList()); val summaries by dao.observeCompletedSummaries().collectAsState(initial = emptyList())
    var selectedDay by remember { mutableLongStateOf(dayStart(System.currentTimeMillis())) }; var details by remember { mutableStateOf<WorkoutEntity?>(null) }; var editingSet by remember { mutableStateOf<WorkoutSetEntity?>(null) }
    val days = remember { (0..13).map { dayStart(System.currentTimeMillis() - it * 86_400_000L) }.reversed() }
    val filtered = workouts.filter { dayStart(it.startedAt) == selectedDay }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(18.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("ДНЕВНИК", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge); Text("Календарь", style = MaterialTheme.typography.headlineMedium) }; Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) { Icon(Icons.Rounded.CalendarMonth, null, Modifier.padding(12.dp)) } }; Spacer(Modifier.height(6.dp)); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { days.forEach { day -> val hasWorkout = workouts.any { dayStart(it.startedAt) == day }; FilterChip(selected = selectedDay == day, onClick = { selectedDay = day }, label = { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) { Text(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(day)).uppercase(), style = MaterialTheme.typography.labelSmall); Text(SimpleDateFormat("dd", Locale.getDefault()).format(Date(day)), style = MaterialTheme.typography.titleMedium); if (hasWorkout) Text("●", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall) } }) } } }
        item { Text(SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(selectedDay)), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (filtered.isEmpty()) item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(24.dp)) { Text("День отдыха", style = MaterialTheme.typography.titleLarge); Text("В этот день тренировок нет. Восстановление — часть прогресса.", color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
        items(filtered, key = { it.id }) { workout -> val summary = summaries.firstOrNull { it.workoutId == workout.id }; Card(Modifier.fillMaxWidth().clickable { details = workout }) { Column(Modifier.padding(18.dp)) { Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(workout.startedAt)), fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { HistoryMetric(summary?.exerciseCount ?: 0, "упр."); HistoryMetric(summary?.setCount ?: 0, "подх."); HistoryMetric(number(summary?.volume ?: 0.0), "кг"); HistoryMetric(((workout.endedAt!! - workout.startedAt) / 60000).coerceAtLeast(0), "мин") } } } }
    }
    details?.let { workout -> val exercises by dao.observeExercises(workout.id).collectAsState(initial = emptyList()); val sets by dao.observeSets(workout.id).collectAsState(initial = emptyList()); AlertDialog(onDismissRequest = { details = null }, title = { Text("Тренировка ${SimpleDateFormat("dd.MM, HH:mm", Locale.getDefault()).format(Date(workout.startedAt))}") }, text = { LazyColumn { items(exercises) { exercise -> Text(exercise.name, fontWeight = FontWeight.Bold); sets.filter { it.exerciseId == exercise.id }.forEach { set->Text("  ${number(set.weight)} кг × ${set.reps}",Modifier.fillMaxWidth().clickable{editingSet=set}.padding(vertical=5.dp)) }; Spacer(Modifier.height(8.dp)) }; item { Text("Нажмите на подход, чтобы изменить",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant);TextButton({ scope.launch { dao.deleteWorkout(workout.id) }; details = null }) { Text("Удалить тренировку", color = MaterialTheme.colorScheme.error) } } } }, confirmButton = { TextButton({ details = null }) { Text("Готово") } }) }
    editingSet?.let{set->HistorySetEditor(set,{editingSet=null},{w,r->scope.launch{dao.updateSet(set.copy(weight=w,reps=r))};editingSet=null},{scope.launch{dao.deleteSet(set.id)};editingSet=null})}
}
@Composable private fun HistoryMetric(value: Any, label: String) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(value.toString(), fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall) } }
private fun dayStart(time: Long): Long = Calendar.getInstance().apply { timeInMillis = time; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
@Composable private fun HistorySetEditor(set:WorkoutSetEntity,dismiss:()->Unit,save:(Double,Int)->Unit,delete:()->Unit){var weight by remember(set){mutableStateOf(number(set.weight))};var reps by remember(set){mutableStateOf(set.reps.toString())};AlertDialog(onDismissRequest=dismiss,title={Text("Редактировать подход")},text={Column{OutlinedTextField(weight,{weight=it},label={Text("Вес, кг")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(reps,{reps=it.filter(Char::isDigit)},label={Text("Повторения")});TextButton(delete){Text("Удалить",color=MaterialTheme.colorScheme.error)}}},confirmButton={TextButton({val w=weight.replace(',','.').toDoubleOrNull();val r=reps.toIntOrNull();if(w!=null&&r!=null)save(w,r)}){Text("Сохранить")}},dismissButton={TextButton(dismiss){Text("Отмена")}})}
