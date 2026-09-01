package com.stem.stemtraining

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.*
import kotlinx.coroutines.launch

data class WorkoutProgram(val name:String,val exercises:List<String>)
val starterPrograms=listOf(WorkoutProgram("Грудь + Бицепс",listOf("Жим лёжа на наклонной скамье · штанга","Жим лёжа · гантели","Разведение рук лёжа · гантели","Сгибание рук · штанга","Молотковые сгибания · гантели","Скручивания")),WorkoutProgram("Спина + Плечи",listOf("Тяга в наклоне · штанга","Тяга верхнего блока","Тяга горизонтального блока","Жим сидя · штанга","Разведение рук в стороны · гантели","Скручивания")),WorkoutProgram("Ноги + Трицепс",listOf("Приседания · штанга","Выпады · гантели","Подъём на носки стоя","Жим лёжа узким хватом · штанга","Разгибание рук на блоке","Скручивания")))

@Composable fun ProgramsScreen(onStart:(ProgramWithExercises)->Unit){val context=LocalContext.current;val dao=remember{TrainingDatabase.getInstance(context).trainingDao()};val scope=rememberCoroutineScope();val programs by dao.observePrograms().collectAsState(initial=emptyList());var editing by remember{mutableStateOf<ProgramWithExercises?>(null)};var creating by remember{mutableStateOf(false)};var selected by remember{mutableStateOf<ProgramWithExercises?>(null)}
    LazyColumn(Modifier.fillMaxSize().padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Spacer(Modifier.height(18.dp));Text("ПЛАН ТРЕНИРОВОК",color=MaterialTheme.colorScheme.secondary,style=MaterialTheme.typography.labelLarge);Text("Программы",style=MaterialTheme.typography.headlineMedium);Text("Задайте порядок, подходы и повторения.",color=MaterialTheme.colorScheme.onSurfaceVariant);Spacer(Modifier.height(6.dp));Button({creating=true},Modifier.fillMaxWidth().height(52.dp)){Icon(Icons.Rounded.Add,null);Spacer(Modifier.width(8.dp));Text("Новая программа")}}
        items(programs,key={it.program.id}){p->Card(Modifier.fillMaxWidth().clickable{selected=p},colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(20.dp)){Row(verticalAlignment=Alignment.CenterVertically){Surface(shape=MaterialTheme.shapes.small,color=MaterialTheme.colorScheme.primaryContainer){Icon(Icons.Rounded.ViewList,null,Modifier.padding(10.dp))};Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(p.program.name,style=MaterialTheme.typography.titleMedium);Text("${p.exercises.size} упражнений",color=MaterialTheme.colorScheme.onSurfaceVariant)};IconButton({editing=p}){Icon(Icons.Rounded.Edit,"Изменить")}};Spacer(Modifier.height(10.dp));p.exercises.sortedBy{it.position}.take(4).forEach{Text("${it.targetSets} × ${it.targetReps}  ${it.name}",style=MaterialTheme.typography.bodyMedium)}}}}
    }
    selected?.let{p->AlertDialog(onDismissRequest={selected=null},title={Text(p.program.name)},text={Column{p.exercises.sortedBy{it.position}.forEach{exercise->Row(Modifier.fillMaxWidth().padding(vertical=4.dp),verticalAlignment=Alignment.CenterVertically){Image(painterResource(exerciseIcon(exercise.name)),null,Modifier.size(40.dp),contentScale=ContentScale.Crop);Spacer(Modifier.width(10.dp));Text("${exercise.targetSets} × ${exercise.targetReps}  ${exercise.name}")}}}},confirmButton={TextButton({onStart(p);selected=null}){Text("Начать")}},dismissButton={TextButton({selected=null}){Text("Закрыть")}})}
    if(creating)ProgramEditor(null,{creating=false},save={name,items->scope.launch{dao.saveProgram(ProgramEntity(name=name),items)};creating=false})
    editing?.let{p->ProgramEditor(p,{editing=null},{name,items->scope.launch{dao.saveProgram(p.program.copy(name=name),items)};editing=null},{scope.launch{dao.deleteProgram(p.program.id)};editing=null})}
}

@Composable private fun ProgramEditor(current:ProgramWithExercises?,dismiss:()->Unit,save:(String,List<ProgramExerciseEntity>)->Unit,delete:(()->Unit)?=null){var name by remember(current){mutableStateOf(current?.program?.name?:"")};var rows by remember(current){mutableStateOf(current?.exercises?.sortedBy{it.position}?:emptyList())};var picker by remember{mutableStateOf(false)}
    AlertDialog(onDismissRequest=dismiss,title={Text(if(current==null)"Новая программа" else "Редактировать")},text={LazyColumn(Modifier.heightIn(max=520.dp)){item{OutlinedTextField(name,{name=it},label={Text("Название")},modifier=Modifier.fillMaxWidth());Spacer(Modifier.height(10.dp))};items(rows,key={it.name}){row->Card(Modifier.fillMaxWidth().padding(vertical=4.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant)){Column(Modifier.padding(10.dp)){Row(verticalAlignment=Alignment.CenterVertically){Image(painterResource(exerciseIcon(row.name)),null,Modifier.size(36.dp),contentScale=ContentScale.Crop);Spacer(Modifier.width(8.dp));Text(row.name,Modifier.weight(1f),style=MaterialTheme.typography.titleSmall);IconButton({val i=rows.indexOf(row);if(i>0){val m=rows.toMutableList();m[i]=m[i-1];m[i-1]=row;rows=m}}){Icon(Icons.Rounded.KeyboardArrowUp,null)};IconButton({val i=rows.indexOf(row);if(i<rows.lastIndex){val m=rows.toMutableList();m[i]=m[i+1];m[i+1]=row;rows=m}}){Icon(Icons.Rounded.KeyboardArrowDown,null)};IconButton({rows=rows-row}){Icon(Icons.Rounded.Close,null)}};Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){TargetStepper("Подходы",row.targetSets,{v->rows=rows.map{if(it==row)it.copy(targetSets=v) else it}},Modifier.weight(1f));TargetStepper("Повторы",row.targetReps,{v->rows=rows.map{if(it==row)it.copy(targetReps=v) else it}},Modifier.weight(1f))}}}};item{OutlinedButton({picker=true},Modifier.fillMaxWidth()){Icon(Icons.Rounded.Add,null);Text(" Упражнение")};delete?.let{TextButton(it){Text("Удалить программу",color=MaterialTheme.colorScheme.error)}}}}},confirmButton={TextButton({if(name.isNotBlank()&&rows.isNotEmpty())save(name.trim(),rows)}){Text("Сохранить")}},dismissButton={TextButton(dismiss){Text("Отмена")}})
    if(picker)ExerciseCatalogDialog(rows.map{it.name}.toSet(),{picker=false}){rows=rows+ProgramExerciseEntity(programId=current?.program?.id?:0,name=it,position=rows.size);picker=false}
}
@Composable private fun TargetStepper(label:String,value:Int,change:(Int)->Unit,modifier:Modifier=Modifier){Column(modifier){Text(label,style=MaterialTheme.typography.labelSmall);Row(verticalAlignment=Alignment.CenterVertically){IconButton({change((value-1).coerceAtLeast(1))}){Icon(Icons.Rounded.Remove,null)};Text(value.toString(),style=MaterialTheme.typography.titleMedium);IconButton({change((value+1).coerceAtMost(30))}){Icon(Icons.Rounded.Add,null)}}}}
