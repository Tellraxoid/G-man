package com.stem.stemtraining

import androidx.compose.foundation.layout.*
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
import com.stem.stemtraining.data.TrainingDatabase
import java.util.Locale

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }
    val progress by dao.observeExerciseProgress().collectAsState(initial = emptyList())
    val workouts by dao.observeCompletedWorkouts().collectAsState(initial = emptyList())
    val summaries by dao.observeCompletedSummaries().collectAsState(initial = emptyList())
    val totalVolume = summaries.sumOf { it.volume }
    val totalSets = summaries.sumOf { it.setCount }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(18.dp)); Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("S.T.E.M. ANALYTICS", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge); Text("Прогресс", style = MaterialTheme.typography.headlineMedium) }; Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) { Icon(Icons.Rounded.EmojiEvents, null, Modifier.padding(12.dp)) } } }
        item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) { Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) { StatValue(workouts.size.toString(), "ТРЕНИРОВОК"); StatValue(totalSets.toString(), "ПОДХОДОВ"); StatValue(formatStat(totalVolume), "КГ ОБЪЁМА") } } }
        item { Text("Личные результаты", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text("Расчётный 1ПМ — формула Epley по сохранённым рабочим подходам.", style = MaterialTheme.typography.bodySmall) }
        if (progress.isEmpty()) item { Text("Заверши несколько тренировок — здесь появятся результаты.") }
        items(progress, key = { it.name }) { p -> Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(p.name, fontWeight = FontWeight.Bold); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { StatValue(formatStat(p.bestWeight), "макс. вес"); StatValue(formatStat(p.bestEstimated1Rm), "расч. 1ПМ"); StatValue(p.sessions.toString(), "сессий") }; Text("Всего: ${p.sets} подходов · ${formatStat(p.totalVolume)} кг", style = MaterialTheme.typography.bodySmall) } } }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable private fun StatValue(value:String,label:String){Column{Text(value,fontWeight=FontWeight.Bold);Text(label,style=MaterialTheme.typography.labelSmall)}}
private fun formatStat(v:Double)=if(v%1.0==0.0)v.toLong().toString() else String.format(Locale.US,"%.1f",v)
