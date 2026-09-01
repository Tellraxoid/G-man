package com.stem.stemtraining

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.*
import kotlinx.coroutines.launch

data class WorkoutProgram(val name: String, val exercises: List<String>)
val starterPrograms = listOf(
    WorkoutProgram("Грудь + Бицепс", listOf("Жим на наклонной скамье · штанга", "Жим лёжа · гантели", "Разведение рук лёжа · гантели", "Сгибание рук · штанга", "Молотковые сгибания · гантели", "Скручивания")),
    WorkoutProgram("Спина + Плечи", listOf("Тяга в наклоне · штанга", "Тяга верхнего блока", "Тяга горизонтального блока", "Жим сидя · штанга", "Разведение рук в стороны · гантели", "Скручивания")),
    WorkoutProgram("Ноги + Трицепс", listOf("Приседания · штанга", "Выпады · гантели", "Подъём на носки стоя", "Жим лёжа узким хватом · штанга", "Разгибание рук на блоке", "Скручивания"))
)

@Composable fun ProgramsScreen(onStart: (ProgramWithExercises) -> Unit) {
    val context = LocalContext.current; val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }; val scope = rememberCoroutineScope()
    val programs by dao.observePrograms().collectAsState(initial = emptyList()); var editing by remember { mutableStateOf<ProgramWithExercises?>(null) }; var creating by remember { mutableStateOf(false) }; var selected by remember { mutableStateOf<ProgramWithExercises?>(null) }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(12.dp)); Text("ПЛАН", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge); Text("Программы", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Button({ creating = true }, Modifier.fillMaxWidth()) { Text("+ Новая программа") } }
        items(programs, key = { it.program.id }) { program -> Card(Modifier.fillMaxWidth().clickable { selected = program }) { Column(Modifier.padding(18.dp)) { Row { Text(program.program.name, Modifier.weight(1f), fontWeight = FontWeight.Bold); TextButton({ editing = program }) { Text("Изменить") } }; Text("${program.exercises.size} упражнений"); program.exercises.sortedBy { it.position }.take(3).forEach { Text("• ${it.name}", style = MaterialTheme.typography.bodySmall) } } } }
        item { Spacer(Modifier.height(20.dp)) }
    }
    selected?.let { program -> AlertDialog(onDismissRequest = { selected = null }, title = { Text(program.program.name) }, text = { Column { program.exercises.sortedBy { it.position }.forEachIndexed { index, item -> Text("${index + 1}. ${item.name}") } } }, confirmButton = { TextButton({ onStart(program); selected = null }) { Text("Начать") } }, dismissButton = { TextButton({ selected = null }) { Text("Закрыть") } }) }
    if (creating) ProgramEditor(null, { creating = false }, { name, exercises -> scope.launch { dao.saveProgram(ProgramEntity(name = name), exercises) }; creating = false })
    editing?.let { program -> ProgramEditor(program, { editing = null }, { name, exercises -> scope.launch { dao.saveProgram(program.program.copy(name = name), exercises) }; editing = null }, { scope.launch { dao.deleteProgram(program.program.id) }; editing = null }) }
}

@Composable private fun ProgramEditor(current: ProgramWithExercises?, dismiss: () -> Unit, save: (String, List<String>) -> Unit, delete: (() -> Unit)? = null) {
    var name by remember(current) { mutableStateOf(current?.program?.name ?: "") }; var names by remember(current) { mutableStateOf<List<String>>(current?.exercises?.sortedBy { it.position }?.map { it.name } ?: emptyList()) }; var picker by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = dismiss, title = { Text(if (current == null) "Новая программа" else "Редактировать") }, text = { Column { OutlinedTextField(name, { name = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp)); names.forEach { exercise -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(exercise, Modifier.weight(1f)); TextButton({ names = names - exercise }) { Text("×") } } }; OutlinedButton({ picker = true }, Modifier.fillMaxWidth()) { Text("Добавить упражнение") }; delete?.let { TextButton(it) { Text("Удалить программу", color = MaterialTheme.colorScheme.error) } } } }, confirmButton = { TextButton({ if (name.isNotBlank() && names.isNotEmpty()) save(name.trim(), names) }) { Text("Сохранить") } }, dismissButton = { TextButton(dismiss) { Text("Отмена") } })
    if (picker) ExerciseCatalogDialog(names.toSet(), { picker = false }) { names = names + it; picker = false }
}
