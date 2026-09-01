package com.stem.stemtraining

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("stem_settings", 0) }
    var athlete by remember { mutableStateOf(prefs.getString("athlete", "") ?: "") }
    var rest by remember { mutableStateOf(prefs.getInt("rest", 90).toString()) }
    var vibration by remember { mutableStateOf(prefs.getBoolean("vibration", true)) }
    var saved by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(4.dp)); Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("ПРИЛОЖЕНИЕ", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge); Text("Настройки", style = MaterialTheme.typography.headlineMedium) }; Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) { Icon(Icons.Rounded.Settings, null, Modifier.padding(12.dp)) } }
        Text("Профиль", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(athlete, { athlete = it }, label = { Text("Имя спортсмена") }, leadingIcon = { Icon(Icons.Rounded.Person, null) }, modifier = Modifier.fillMaxWidth())
        Text("Тренировка", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(rest, { rest = it.filter(Char::isDigit) }, label = { Text("Отдых между подходами, сек") }, leadingIcon = { Icon(Icons.Rounded.Timer, null) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text("Вибрация таймера", fontWeight = FontWeight.Bold); Text("Сигнал по окончании отдыха", style = MaterialTheme.typography.bodySmall) }; Switch(vibration, { vibration = it }) } }
        Button({ prefs.edit().putString("athlete", athlete.trim()).putInt("rest", rest.toIntOrNull()?.coerceIn(15, 600) ?: 90).putBoolean("vibration", vibration).apply(); saved = true }, Modifier.fillMaxWidth().height(52.dp)) { Text("Сохранить настройки") }
        if (saved) Text("Настройки сохранены", color = MaterialTheme.colorScheme.primary)
        HorizontalDivider(); Text("S.T.E.M. Training 1.0", style = MaterialTheme.typography.bodySmall); Text("Данные тренировок хранятся только на устройстве.", style = MaterialTheme.typography.bodySmall)
    }
}
