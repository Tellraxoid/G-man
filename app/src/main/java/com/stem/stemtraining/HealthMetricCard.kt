package com.stem.stemtraining

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

internal fun friendlyHealthDetail(detail: String): String {
    val names = mapOf(
        "com.hevy" to "Hevy",
        "com.google.android.apps.fitness" to "Google Fit",
        "com.mi.health" to "Mi Health",
        "com.sec.android.app.shealth" to "Samsung Health",
        "com.fitbit.FitbitMobile" to "Fitbit"
    )
    // Exact package tokens only; unknown sources remain identifiable.
    return Regex("[A-Za-z][A-Za-z0-9_]*(?:\\.[A-Za-z][A-Za-z0-9_]*)+")
        .replace(detail) { names[it.value] ?: it.value }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360)
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360, fontScale = 1.5f)
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HealthMetricsPreview() {
    com.stem.stemtraining.ui.theme.STEMTrainingTheme {
        Surface {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HealthMetricCard(HealthMetric("Вес", "80 кг", "02.09.2026 08:00 · com.hevy"))
                HealthMetricCard(HealthMetric("Жир", "20 %", "01.09.2026 08:00 · com.google.android.apps.fitness"))
                HealthMetricCard(HealthMetric("Сон за последние 24 часа", "7 ч 30 мин", "01.09.2026 08:00 — 02.09.2026 08:00 · com.mi.health"))
                HealthMetricCard(HealthMetric("Последняя запись питания", "Нет записей за 7 дней"))
            }
        }
    }
}

@Composable
internal fun HealthMetricCard(metric: HealthMetric) {
    val (label, icon) = when (metric.title) {
        "Вес" -> "Вес" to Icons.Outlined.MonitorWeight
        "Жир" -> "Процент жира" to Icons.Outlined.Percent
        "Сон за последние 24 часа" -> "Сон" to Icons.Outlined.Bedtime
        else -> "Питание" to Icons.Outlined.Restaurant
    }
    val hasReading = metric.detail.isNotBlank()
    val subtitle = when (metric.title) {
        "Сон за последние 24 часа" -> "За последние 24 часа"
        "Последняя запись питания" -> "Последняя запись · не итог за день"
        else -> null
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(metric.value,
                    style = if (hasReading && metric.title != "Последняя запись питания") MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
                    fontWeight = if (hasReading) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (hasReading) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (hasReading) {
                    val detail = friendlyHealthDetail(metric.detail)
                    // Full sleep interval remains available in the details disclosure.
                    val caption = if (metric.title == "Сон за последние 24 часа") detail.substringAfter(" · ", "") else detail.substringBefore(". Это запись")
                    if (caption.isNotBlank()) Text(caption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
