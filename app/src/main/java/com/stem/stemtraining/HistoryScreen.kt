package com.stem.stemtraining

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable fun HistoryScreen() {
    val context = LocalContext.current; val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }; val scope = rememberCoroutineScope()
    val workouts by dao.observeCompletedWorkouts().collectAsState(initial = emptyList()); val summaries by dao.observeCompletedSummaries().collectAsState(initial = emptyList())
    var selectedDay by remember { mutableLongStateOf(dayStart(System.currentTimeMillis())) }; var details by remember { mutableStateOf<WorkoutEntity?>(null) }; var editingSet by remember { mutableStateOf<WorkoutSetEntity?>(null) }; var addingSetFor by remember { mutableStateOf<ExerciseEntity?>(null) }; var addingExerciseTo by remember { mutableStateOf<WorkoutEntity?>(null) }
    val days = remember { (0..29).map { dayStart(System.currentTimeMillis() - it * 86_400_000L) }.reversed() }
    val filtered = workouts.filter { dayStart(it.startedAt) == selectedDay }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(18.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("ДНЕВНИК", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge); Text("Календарь", style = MaterialTheme.typography.headlineMedium) }; Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) { Icon(Icons.Rounded.CalendarMonth, null, Modifier.padding(12.dp)) } }; Spacer(Modifier.height(6.dp)); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { days.forEach { day -> val hasWorkout = workouts.any { dayStart(it.startedAt) == day }; FilterChip(selected = selectedDay == day, onClick = { selectedDay = day }, label = { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) { Text(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(day)).uppercase(), style = MaterialTheme.typography.labelSmall); Text(SimpleDateFormat("dd", Locale.getDefault()).format(Date(day)), style = MaterialTheme.typography.titleMedium); if (hasWorkout) Text("●", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall) } }) } } }
        item { Text(SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(selectedDay)), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item { Button({ scope.launch { val (start,end)=historicalWorkoutTimes(selectedDay);val id=dao.insertWorkout(WorkoutEntity(startedAt=start,endedAt=end));details=WorkoutEntity(id=id,startedAt=start,endedAt=end) } },Modifier.fillMaxWidth()){Icon(Icons.Rounded.Add,null);Text(if(filtered.isEmpty())" Добавить тренировку на эту дату" else " Добавить ещё тренировку") } }
        if (filtered.isEmpty()) item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(24.dp)) { Text("День отдыха", style = MaterialTheme.typography.titleLarge); Text("В этот день тренировок нет. Восстановление — часть прогресса.", color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
        items(filtered, key = { it.id }) { workout -> val summary = summaries.firstOrNull { it.workoutId == workout.id }; Card(Modifier.fillMaxWidth().clickable { details = workout }) { Column(Modifier.padding(18.dp)) { Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(workout.startedAt)), fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { HistoryMetric(summary?.exerciseCount ?: 0, "упр."); HistoryMetric(summary?.setCount ?: 0, "подх."); HistoryMetric(number(summary?.volume ?: 0.0), "кг"); HistoryMetric(((workout.endedAt!! - workout.startedAt) / 60000).coerceAtLeast(0), "мин") } } } }
    }
    details?.let { workout -> val exercises by dao.observeExercises(workout.id).collectAsState(initial = emptyList()); val sets by dao.observeSets(workout.id).collectAsState(initial = emptyList()); AlertDialog(onDismissRequest = { details = null }, title = { Text("Редактирование · ${SimpleDateFormat("dd.MM, HH:mm", Locale.getDefault()).format(Date(workout.startedAt))}") }, text = { LazyColumn { items(exercises,key={it.id}) { exercise -> Row(verticalAlignment=Alignment.CenterVertically){Image(painterResource(exerciseIcon(exercise.name)),null,Modifier.size(42.dp),contentScale=ContentScale.Crop);Spacer(Modifier.width(10.dp));Text(exercise.name,Modifier.weight(1f),fontWeight=FontWeight.Bold)}; sets.filter { it.exerciseId == exercise.id }.forEach { set->Text("  ${number(set.weight)} кг × ${set.reps}${set.rir?.let{" · RIR $it"}?:""}",Modifier.fillMaxWidth().clickable{editingSet=set}.padding(vertical=5.dp)) };TextButton({addingSetFor=exercise}){Icon(Icons.Rounded.Add,null);Text(" Добавить подход")};HorizontalDivider();Spacer(Modifier.height(6.dp)) }; item { OutlinedButton({addingExerciseTo=workout},Modifier.fillMaxWidth()){Icon(Icons.Rounded.Add,null);Text(" Добавить упражнение")};Text("Нажмите на подход, чтобы изменить или удалить",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant);TextButton({ scope.launch { dao.deleteWorkout(workout.id) }; details = null }) { Text("Удалить тренировку", color = MaterialTheme.colorScheme.error) } } } }, confirmButton = { TextButton({ details = null }) { Text("Готово") } }) }
    editingSet?.let{set->SetDialog(set,{editingSet=null},{weight,reps,rir,warmup->scope.launch{dao.updateSet(set.copy(weight=weight,reps=reps,rir=rir,isWarmup=warmup))};editingSet=null},{scope.launch{dao.deleteSet(set.id)};editingSet=null})}
    addingSetFor?.let{exercise->SetDialog(null,{addingSetFor=null},{weight,reps,rir,warmup->scope.launch{dao.insertSet(WorkoutSetEntity(exerciseId=exercise.id,weight=weight,reps=reps,rir=rir,isWarmup=warmup))};addingSetFor=null},isNew=true)}
    addingExerciseTo?.let{workout->val existing by dao.observeExercises(workout.id).collectAsState(initial=emptyList());ExerciseCatalogDialog(existing.map{it.name}.toSet(),{addingExerciseTo=null}){name->scope.launch{dao.insertExercise(ExerciseEntity(workoutId=workout.id,name=name))};addingExerciseTo=null}}
}
@Composable private fun HistoryMetric(value: Any, label: String) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(value.toString(), fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall) } }
private fun dayStart(time: Long): Long = Calendar.getInstance().apply { timeInMillis = time; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
private fun historicalWorkoutTimes(day:Long):Pair<Long,Long>{val now=System.currentTimeMillis();if(dayStart(now)==day)return (now-3_600_000L) to now;val end=Calendar.getInstance().apply{timeInMillis=day;set(Calendar.HOUR_OF_DAY,19);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)}.timeInMillis;return (end-3_600_000L) to end}
