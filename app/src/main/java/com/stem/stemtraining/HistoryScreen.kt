package com.stem.stemtraining

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.TrainingDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val dao = remember { TrainingDatabase.getInstance(context).trainingDao() }
    val workouts by dao.observeCompletedWorkouts().collectAsState(initial = emptyList())
    val summaries by dao.observeCompletedSummaries().collectAsState(initial = emptyList())
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("ИСТОРИЯ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Тренировки", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onBack) { Text("Сегодня") }
            }
        }

        if (workouts.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text("Завершённые тренировки появятся здесь.", modifier = Modifier.padding(20.dp))
                }
            }
        }

        items(workouts, key = { it.id }) { workout ->
            val summary = summaries.firstOrNull { it.workoutId == workout.id }
            val durationMinutes = workout.endedAt?.let { ((it - workout.startedAt) / 60000).coerceAtLeast(0) } ?: 0
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(formatter.format(Date(workout.startedAt)), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        HistoryValue(summary?.exerciseCount?.toString() ?: "0", "упр.")
                        HistoryValue(summary?.setCount?.toString() ?: "0", "подх.")
                        HistoryValue(formatNumberHistory(summary?.volume ?: 0.0), "кг")
                        HistoryValue("$durationMinutes", "мин")
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun HistoryValue(value: String, label: String) {
    Column {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

private fun formatNumberHistory(value: Double): String = if (value % 1.0 == 0.0) value.toLong().toString() else String.format(Locale.US, "%.1f", value)
