package com.stem.stemtraining

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.stem.stemtraining.data.TrainingDatabase
import java.util.Locale
import java.util.Calendar

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }
    val progress by dao.observeExerciseProgress().collectAsState(initial = emptyList())
    val workouts by dao.observeCompletedWorkouts().collectAsState(initial = emptyList())
    val summaries by dao.observeCompletedSummaries().collectAsState(initial = emptyList())
    val totalVolume = summaries.sumOf { it.volume }
    val totalSets = summaries.sumOf { it.setCount }
    val weekStart=remember{Calendar.getInstance().apply{set(Calendar.DAY_OF_WEEK,firstDayOfWeek);set(Calendar.HOUR_OF_DAY,0);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0)}.timeInMillis};val weekly=workouts.count{it.startedAt>=weekStart};val goal=LocalContext.current.getSharedPreferences("stem_settings",0).getInt("weekly_goal",3);val streak=remember(workouts){var count=0;var cursor=Calendar.getInstance();val days=workouts.map{Calendar.getInstance().apply{timeInMillis=it.startedAt}.get(Calendar.DAY_OF_YEAR) to Calendar.getInstance().apply{timeInMillis=it.startedAt}.get(Calendar.YEAR)}.toSet();while((cursor.get(Calendar.DAY_OF_YEAR) to cursor.get(Calendar.YEAR)) in days){count++;cursor.add(Calendar.DAY_OF_YEAR,-1)};count};var plates by remember{mutableStateOf(false)}
    var selectedName by remember { mutableStateOf<String?>(null) }
    val points by remember(selectedName) { dao.observeProgressPoints(selectedName ?: "") }.collectAsState(initial = emptyList())

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(18.dp)); Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("S.T.E.M. ANALYTICS", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge); Text("Прогресс", style = MaterialTheme.typography.headlineMedium) }; Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) { Icon(Icons.Rounded.EmojiEvents, null, Modifier.padding(12.dp)) } } }
        item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) { Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) { StatValue(workouts.size.toString(), "ТРЕНИРОВОК"); StatValue(totalSets.toString(), "ПОДХОДОВ"); StatValue(formatStat(totalVolume), "КГ ОБЪЁМА") } } }
        item{Card(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Цель недели",style=MaterialTheme.typography.titleMedium);Text("$weekly / $goal",fontWeight=FontWeight.Bold)};LinearProgressIndicator(progress={(weekly.toFloat()/goal).coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth().padding(vertical=10.dp));Text("Серия: $streak дн. подряд",color=MaterialTheme.colorScheme.secondary);OutlinedButton({plates=true},Modifier.fillMaxWidth().padding(top=8.dp)){Text("Калькулятор блинов")}}}}
        item{val muscleVolumes=progress.groupBy{p->exerciseCatalog.firstOrNull{it.name==p.name}?.muscle?:"Другое"}.mapValues{it.value.sumOf{p->p.totalVolume}}.entries.sortedByDescending{it.value}.take(5);if(muscleVolumes.isNotEmpty())Card(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("Объём по мышцам",style=MaterialTheme.typography.titleMedium);val max=muscleVolumes.maxOf{it.value};muscleVolumes.forEach{(muscle,volume)->Text("$muscle · ${formatStat(volume)} кг",style=MaterialTheme.typography.bodySmall,modifier=Modifier.padding(top=8.dp));LinearProgressIndicator(progress={(volume/max).toFloat()},modifier=Modifier.fillMaxWidth())}}}}
        item { Text("Личные результаты", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Расчётный 1ПМ — формула Epley по сохранённым рабочим подходам.", style = MaterialTheme.typography.bodySmall) }
        if(selectedName!=null)item{Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)){Column(Modifier.padding(18.dp)){Text(selectedName!!,style=MaterialTheme.typography.titleMedium);Text("Динамика расчётного 1ПМ",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant);ProgressChart(points.map{it.estimated1Rm})}}}
        if (progress.isEmpty()) item { Text("Заверши несколько тренировок — здесь появятся результаты.") }
        items(progress, key = { it.name }) { p -> Card(Modifier.fillMaxWidth().clickable{selectedName=p.name}, shape = RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(selectedName==p.name)MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(p.name, fontWeight = FontWeight.Bold); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { StatValue(formatStat(p.bestWeight), "макс. вес"); StatValue(formatStat(p.bestEstimated1Rm), "расч. 1ПМ"); StatValue(p.sessions.toString(), "сессий") }; Text("Всего: ${p.sets} подходов · ${formatStat(p.totalVolume)} кг · нажмите для графика", style = MaterialTheme.typography.bodySmall) } } }
        item { Spacer(Modifier.height(24.dp)) }
    }
    if(plates)PlateCalculator{plates=false}
}

@Composable private fun StatValue(value:String,label:String){Column{Text(value,fontWeight=FontWeight.Bold);Text(label,style=MaterialTheme.typography.labelSmall)}}
private fun formatStat(v:Double)=if(v%1.0==0.0)v.toLong().toString() else String.format(Locale.US,"%.1f",v)
@Composable private fun ProgressChart(values:List<Double>){val color=MaterialTheme.colorScheme.secondary;val grid=MaterialTheme.colorScheme.outline;Box(Modifier.fillMaxWidth().height(160.dp).padding(top=12.dp)){if(values.size<2)Text("Нужно минимум две тренировки",color=MaterialTheme.colorScheme.onSurfaceVariant);else Canvas(Modifier.fillMaxSize()){val min=values.min();val max=values.max();val range=(max-min).coerceAtLeast(1.0);repeat(4){i->val y=size.height*i/3f;drawLine(grid.copy(alpha=.35f),start=androidx.compose.ui.geometry.Offset(0f,y),end=androidx.compose.ui.geometry.Offset(size.width,y))};val path=Path();values.forEachIndexed{i,v->val x=size.width*i/(values.lastIndex);val y=size.height-((v-min)/range*size.height).toFloat();if(i==0)path.moveTo(x,y)else path.lineTo(x,y);drawCircle(color,6f,androidx.compose.ui.geometry.Offset(x,y))};drawPath(path,color,style=Stroke(width=7f,cap=StrokeCap.Round))}}}
@Composable private fun PlateCalculator(dismiss:()->Unit){var total by remember{mutableStateOf("100")};var bar by remember{mutableStateOf("20")};val side=((total.replace(',','.').toDoubleOrNull()?:0.0)-(bar.replace(',','.').toDoubleOrNull()?:20.0)).coerceAtLeast(0.0)/2;val plates=remember(side){var left=side;val out=mutableListOf<Double>();listOf(25.0,20.0,15.0,10.0,5.0,2.5,1.25).forEach{p->while(left+0.001>=p){out+=p;left-=p}};out};AlertDialog(onDismissRequest=dismiss,title={Text("Калькулятор блинов")},text={Column{OutlinedTextField(total,{total=it},label={Text("Общий вес, кг")});OutlinedTextField(bar,{bar=it},label={Text("Гриф, кг")});Spacer(Modifier.height(12.dp));Text("На каждую сторону",style=MaterialTheme.typography.labelLarge);Text(if(plates.isEmpty())"Без блинов" else plates.joinToString(" + "){formatStat(it)},style=MaterialTheme.typography.titleLarge);Text("Остаток: ${formatStat((side-plates.sum()).coerceAtLeast(0.0))} кг",style=MaterialTheme.typography.bodySmall)}},confirmButton={TextButton(dismiss){Text("Готово")}})}
