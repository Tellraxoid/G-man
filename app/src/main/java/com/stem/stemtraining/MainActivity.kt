package com.stem.stemtraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.stem.stemtraining.data.*
import com.stem.stemtraining.ui.theme.STEMTrainingTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { STEMTrainingTheme { AppRoot() } }
    }
}

@Composable
private fun AppRoot() {
    var page by remember { mutableStateOf("today") }
    if (page == "history") HistoryScreen { page = "today" }
    else TrainingScreen { page = "history" }
}

data class CatalogExercise(val name: String, val muscle: String)
private val exerciseCatalog = listOf(
    CatalogExercise("Жим лёжа · штанга", "Грудь"), CatalogExercise("Жим на наклонной скамье · штанга", "Грудь"), CatalogExercise("Жим лёжа · гантели", "Грудь"), CatalogExercise("Разведение рук лёжа · гантели", "Грудь"), CatalogExercise("Отжимания", "Грудь"),
    CatalogExercise("Тяга в наклоне · штанга", "Спина"), CatalogExercise("Тяга в наклоне одной рукой · гантель", "Спина"), CatalogExercise("Тяга верхнего блока", "Спина"), CatalogExercise("Тяга горизонтального блока", "Спина"), CatalogExercise("Подтягивания", "Спина"),
    CatalogExercise("Приседания · штанга", "Ноги"), CatalogExercise("Выпады · гантели", "Ноги"), CatalogExercise("Жим ногами", "Ноги"), CatalogExercise("Сгибание ног", "Ноги"), CatalogExercise("Разгибание ног", "Ноги"), CatalogExercise("Подъём на носки стоя", "Икры"),
    CatalogExercise("Жим сидя · штанга", "Плечи"), CatalogExercise("Жим сидя · гантели", "Плечи"), CatalogExercise("Разведение рук в стороны · гантели", "Плечи"), CatalogExercise("Разведение рук в наклоне · гантели", "Плечи"), CatalogExercise("Шраги · гантели", "Трапеции"),
    CatalogExercise("Сгибание рук · штанга", "Бицепс"), CatalogExercise("Сгибание рук · гантели", "Бицепс"), CatalogExercise("Молотковые сгибания · гантели", "Бицепс"), CatalogExercise("Жим лёжа узким хватом · штанга", "Трицепс"), CatalogExercise("Разгибание рук на блоке", "Трицепс"), CatalogExercise("Французский жим · гантель", "Трицепс"),
    CatalogExercise("Скручивания", "Пресс"), CatalogExercise("Подъём ног", "Пресс"), CatalogExercise("Планка", "Пресс")
)

@Composable
fun TrainingScreen(onHistory: () -> Unit) {
    val dao = remember { TrainingDatabase.getInstance(LocalContext.current).trainingDao() }
    val activeWorkout by dao.observeActiveWorkout().collectAsState(initial = null)
    val completed by dao.observeCompletedWorkouts().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val workoutId = activeWorkout?.id ?: -1L
    val exercises by remember(workoutId) { dao.observeExercises(workoutId) }.collectAsState(initial = emptyList())
    val allSets by remember(workoutId) { dao.observeSets(workoutId) }.collectAsState(initial = emptyList())
    var showCatalog by remember { mutableStateOf(false) }; var addingSetFor by remember { mutableStateOf<ExerciseEntity?>(null) }
    var editingSet by remember { mutableStateOf<WorkoutSetEntity?>(null) }; var confirmFinish by remember { mutableStateOf(false) }
    val volume = allSets.sumOf { it.weight * it.reps }

    Scaffold { pad -> LazyColumn(Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(8.dp)); Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("S.T.E.M. TRAINING", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(if (activeWorkout == null) "Сегодня" else "Тренировка идёт", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
            TextButton(onClick = onHistory) { Text("История ${if (completed.isEmpty()) "" else "(${completed.size})"}") }
        }; Spacer(Modifier.height(8.dp)) }
        if (activeWorkout == null) item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text("Нет активной тренировки", fontWeight = FontWeight.Bold); Text("Начни новую тренировку. Завершённые сессии сохраняются в истории."); Button({ scope.launch { dao.insertWorkout(WorkoutEntity()) } }, Modifier.fillMaxWidth()) { Text("Начать тренировку") } } } }
        else {
            item { WorkoutSummary(exercises.size, allSets.size, volume) }
            items(exercises, key = { it.id }) { ex -> ExerciseCard(ex, allSets.filter { it.exerciseId == ex.id }, { addingSetFor = ex }, { editingSet = it }) }
            item { Button({ showCatalog = true }, Modifier.fillMaxWidth()) { Text("+  Добавить упражнение") }; TextButton({ confirmFinish = true }, Modifier.fillMaxWidth()) { Text("Завершить тренировку") }; Spacer(Modifier.height(24.dp)) }
        }
    } }

    if (showCatalog && activeWorkout != null) ExerciseCatalogDialog(exercises.map { it.name }.toSet(), { showCatalog = false }) { name -> scope.launch { dao.insertExercise(ExerciseEntity(workoutId = activeWorkout!!.id, name = name)) }; showCatalog = false }
    addingSetFor?.let { ex -> AddSetDialog(ex.name, allSets.lastOrNull { it.exerciseId == ex.id }, { addingSetFor = null }) { w,r -> scope.launch { dao.insertSet(WorkoutSetEntity(exerciseId=ex.id,weight=w,reps=r)) }; addingSetFor=null } }
    editingSet?.let { set -> EditSetDialog(set, { editingSet=null }, { w,r -> scope.launch { dao.updateSet(set.copy(weight=w,reps=r)) }; editingSet=null }, { scope.launch { dao.deleteSet(set.id) }; editingSet=null }) }
    if (confirmFinish && activeWorkout != null) AlertDialog(onDismissRequest={confirmFinish=false}, title={Text("Завершить тренировку?")}, text={Text("Упражнений: ${exercises.size}\nПодходов: ${allSets.size}\nОбъём: ${formatNumber(volume)} кг")}, confirmButton={TextButton({ val id=activeWorkout!!.id; scope.launch { dao.finishWorkout(id) }; confirmFinish=false }){Text("Завершить")}}, dismissButton={TextButton({confirmFinish=false}){Text("Отмена")}})
}

@Composable private fun ExerciseCard(exercise: ExerciseEntity, sets: List<WorkoutSetEntity>, onAdd:()->Unit, onEdit:(WorkoutSetEntity)->Unit) { Card(Modifier.fillMaxWidth(), shape=RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) { Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) { Text(exercise.name, Modifier.weight(1f), fontWeight=FontWeight.SemiBold); Text("＋", Modifier.clickable{onAdd()}.padding(8.dp), style=MaterialTheme.typography.headlineSmall, color=MaterialTheme.colorScheme.primary) }; Spacer(Modifier.height(10.dp)); if(sets.isEmpty()) Text("Подходов пока нет") else sets.forEachIndexed { i,s -> Row(Modifier.fillMaxWidth().clickable{onEdit(s)}.padding(vertical=7.dp), horizontalArrangement=Arrangement.SpaceBetween) { Text("${i+1} подход"); Text("${formatNumber(s.weight)} кг × ${s.reps}", fontWeight=FontWeight.Bold) } } } } }

@Composable private fun EditSetDialog(set: WorkoutSetEntity, dismiss:()->Unit, save:(Double,Int)->Unit, delete:()->Unit) { var w by remember(set){mutableStateOf(formatNumber(set.weight))}; var r by remember(set){mutableStateOf(set.reps.toString())}; val valid=w.replace(',','.').toDoubleOrNull()!=null&&r.toIntOrNull()!=null; AlertDialog(onDismissRequest=dismiss,title={Text("Редактировать подход")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(w,{w=it},label={Text("Вес, кг")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(r,{r=it.filter(Char::isDigit)},label={Text("Повторения")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number));TextButton(delete){Text("Удалить подход")}}},confirmButton={TextButton(enabled=valid,onClick={save(w.replace(',','.').toDouble(),r.toInt())}){Text("Сохранить")}},dismissButton={TextButton(dismiss){Text("Отмена")}}) }

@Composable private fun ExerciseCatalogDialog(existing:Set<String>, dismiss:()->Unit, select:(String)->Unit){var search by remember{mutableStateOf("")};var custom by remember{mutableStateOf("")};val filtered=exerciseCatalog.filter{(it.name.contains(search,true)||it.muscle.contains(search,true))&&it.name !in existing};Dialog(onDismissRequest=dismiss){Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(18.dp)){Text("Каталог упражнений",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);OutlinedTextField(search,{search=it},label={Text("Поиск или группа мышц")},modifier=Modifier.fillMaxWidth());LazyColumn(Modifier.height(360.dp)){items(filtered){e->Row(Modifier.fillMaxWidth().clickable{select(e.name)}.padding(vertical=10.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(e.name,Modifier.weight(1f));Text(e.muscle,color=MaterialTheme.colorScheme.primary)}}};OutlinedTextField(custom,{custom=it},label={Text("Своё упражнение")},modifier=Modifier.fillMaxWidth());Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End){TextButton(dismiss){Text("Отмена")};TextButton(enabled=custom.isNotBlank()&&custom.trim() !in existing,onClick={select(custom.trim())}){Text("Добавить своё")}}}}}}

@Composable private fun AddSetDialog(name:String,previous:WorkoutSetEntity?,dismiss:()->Unit,add:(Double,Int)->Unit){var w by remember(previous){mutableStateOf(previous?.let{formatNumber(it.weight)}?:"")};var r by remember(previous){mutableStateOf(previous?.reps?.toString()?:"")};val valid=w.replace(',','.').toDoubleOrNull()!=null&&r.toIntOrNull()!=null;AlertDialog(onDismissRequest=dismiss,title={Text("Новый подход")},text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){Text(name,fontWeight=FontWeight.SemiBold);OutlinedTextField(w,{w=it},label={Text("Вес, кг")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(r,{r=it.filter(Char::isDigit)},label={Text("Повторения")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))}},confirmButton={TextButton(enabled=valid,onClick={add(w.replace(',','.').toDouble(),r.toInt())}){Text("Добавить")}},dismissButton={TextButton(dismiss){Text("Отмена")}})}

@Composable private fun WorkoutSummary(ec:Int,sc:Int,v:Double){Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant)){Row(Modifier.fillMaxWidth().padding(18.dp),horizontalArrangement=Arrangement.SpaceBetween){SummaryValue(ec.toString(),"упр.");SummaryValue(sc.toString(),"подх.");SummaryValue(formatNumber(v),"кг");SummaryValue("идёт","статус")}}}
@Composable private fun SummaryValue(v:String,l:String){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(v,fontWeight=FontWeight.Bold);Text(l,style=MaterialTheme.typography.labelMedium)}}
private fun formatNumber(v:Double)=if(v%1.0==0.0)v.toLong().toString() else v.toString()
