package com.stem.stemtraining

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class WorkoutProgram(val name:String,val exercises:List<String>)

val starterPrograms=listOf(
    WorkoutProgram("Грудь + Бицепс",listOf("Жим на наклонной скамье · штанга","Жим лёжа · гантели","Разведение рук лёжа · гантели","Сгибание рук · штанга","Молотковые сгибания · гантели","Скручивания")),
    WorkoutProgram("Спина + Плечи",listOf("Тяга в наклоне · штанга","Тяга верхнего блока","Тяга горизонтального блока","Жим сидя · штанга","Разведение рук в стороны · гантели","Разведение рук в наклоне · гантели","Скручивания")),
    WorkoutProgram("Ноги + Трицепс",listOf("Приседания · штанга","Выпады · гантели","Подъём на носки стоя","Жим лёжа узким хватом · штанга","Разгибание рук на блоке","Скручивания"))
)

@Composable fun ProgramsScreen(onBack:()->Unit,onStart:(WorkoutProgram)->Unit){var selected by remember{mutableStateOf<WorkoutProgram?>(null)};LazyColumn(Modifier.padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Spacer(Modifier.height(12.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column{Text("ПРОГРАММЫ",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary);Text("Мои тренировки",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold)};TextButton(onBack){Text("Сегодня")}}};items(starterPrograms){p->Card(Modifier.fillMaxWidth().clickable{selected=p},shape=RoundedCornerShape(20.dp)){Column(Modifier.padding(18.dp)){Text(p.name,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Text("${p.exercises.size} упражнений");p.exercises.take(3).forEach{Text("• $it",style=MaterialTheme.typography.bodySmall)};if(p.exercises.size>3)Text("+ ещё ${p.exercises.size-3}",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.primary)}}};item{Spacer(Modifier.height(24.dp))}}
selected?.let{p->AlertDialog(onDismissRequest={selected=null},title={Text(p.name)},text={Column{p.exercises.forEachIndexed{i,e->Text("${i+1}. $e",modifier=Modifier.padding(vertical=3.dp))}}},confirmButton={TextButton({onStart(p);selected=null}){Text("Начать")}},dismissButton={TextButton({selected=null}){Text("Отмена")}})}}
