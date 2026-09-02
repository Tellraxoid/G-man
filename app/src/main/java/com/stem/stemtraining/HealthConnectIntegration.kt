package com.stem.stemtraining

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.stem.stemtraining.ui.theme.STEMTrainingTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val HEALTH_PRIVACY = "Health Connect: только чтение веса, процента жира, сна и питания после вашего разрешения. Показатели отображаются как контекст к тренировкам, не служат диагнозом и не меняют программу автоматически. Записи загружаются только при открытом экране и хранятся в памяти до его закрытия. Внешнему ИИ, серверам, рекламе и аналитике они не передаются; в резервные копии и JSON-экспорт приложения не включаются. Доступ можно отозвать в Health Connect или кнопкой «Отключить». Исходные записи при отключении не удаляются. Приложения часов, весов и питания должны самостоятельно записывать данные в Health Connect."

class HealthPrivacyActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{STEMTrainingTheme{Surface(Modifier.fillMaxSize()){Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())){Text("Данные здоровья",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(16.dp));Text(HEALTH_PRIVACY);TextButton({finish()}){Text("Закрыть")}}}}}}
}

data class HealthMetric(val title:String,val value:String,val detail:String="")
internal suspend fun permittedHealthMetric(title:String,permission:String,granted:Set<String>,read:suspend ()->HealthMetric):HealthMetric {
    if(permission !in granted)return HealthMetric(title,"Доступ не разрешён")
    return try{read()}catch(e:CancellationException){throw e}catch(e:SecurityException){HealthMetric(title,"Разрешение отозвано")}catch(e:Exception){HealthMetric(title,"Не удалось прочитать; попробуйте обновить")}
}
internal fun maskRevokedHealth(metrics:List<HealthMetric>,permissions:List<String>,granted:Set<String>):List<HealthMetric> =
    metrics.zip(permissions).map{(metric,permission)->if(permission in granted)metric else HealthMetric(metric.title,"Доступ не разрешён")}
val healthReadPermissions=setOf(
    HealthPermission.getReadPermission(WeightRecord::class),
    HealthPermission.getReadPermission(BodyFatRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(NutritionRecord::class)
)
fun healthTime(time:Instant):String=DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault()).format(time)

class HealthReader(private val client:HealthConnectClient){
    suspend fun read(now:Instant=Instant.now()):List<HealthMetric>{
        val granted=client.permissionController.getGrantedPermissions()
        val month=TimeRangeFilter.between(now.minus(Duration.ofDays(29)),now)
        suspend fun metric(title:String,permission:String,block:suspend ()->HealthMetric):HealthMetric{
            return permittedHealthMetric(title,permission,granted,block)
        }
        val weight=metric("Вес",HealthPermission.getReadPermission(WeightRecord::class)){
            val r=client.readRecords(ReadRecordsRequest(WeightRecord::class,month,ascendingOrder=false,pageSize=1)).records.firstOrNull()
            if(r==null)HealthMetric("Вес","Нет записей за 29 дней") else HealthMetric("Вес","${number(r.weight.inKilograms)} кг","${healthTime(r.time)} · ${r.metadata.dataOrigin.packageName}")
        }
        val fat=metric("Жир",HealthPermission.getReadPermission(BodyFatRecord::class)){
            val r=client.readRecords(ReadRecordsRequest(BodyFatRecord::class,month,ascendingOrder=false,pageSize=1)).records.firstOrNull()
            if(r==null)HealthMetric("Жир","Нет записей за 29 дней") else HealthMetric("Жир","${number(r.percentage.value)} %","${healthTime(r.time)} · ${r.metadata.dataOrigin.packageName}")
        }
        val sleep=metric("Сон за последние 24 часа",HealthPermission.getReadPermission(SleepSessionRecord::class)){
            val from=now.minus(Duration.ofHours(24))
            val r=client.aggregate(AggregateRequest(setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),TimeRangeFilter.between(from,now)))
            val duration=r[SleepSessionRecord.SLEEP_DURATION_TOTAL]
            if(duration==null)HealthMetric("Сон за последние 24 часа","Нет данных") else HealthMetric("Сон за последние 24 часа","${duration.toMinutes()/60} ч ${duration.toMinutes()%60} мин","${healthTime(from)} — ${healthTime(now)} · ${r.dataOrigins.joinToString{it.packageName}}")
        }
        val nutrition=metric("Последняя запись питания",HealthPermission.getReadPermission(NutritionRecord::class)){
            val r=client.readRecords(ReadRecordsRequest(NutritionRecord::class,TimeRangeFilter.between(now.minus(Duration.ofDays(7)),now),ascendingOrder=false,pageSize=1)).records.firstOrNull()
            if(r==null)HealthMetric("Последняя запись питания","Нет записей за 7 дней") else {
                val fields=listOfNotNull(r.energy?.let{"${number(it.inKilocalories)} ккал"},r.protein?.let{"белок ${number(it.inGrams)} г"},r.totalCarbohydrate?.let{"углеводы ${number(it.inGrams)} г"},r.totalFat?.let{"жиры ${number(it.inGrams)} г"})
                HealthMetric("Последняя запись питания",fields.joinToString(" · ").ifEmpty{"Калории и БЖУ не заполнены"},"${healthTime(r.endTime)} · ${r.metadata.dataOrigin.packageName}. Это запись, не суточная сумма.")
            }
        }
        // Recheck after reads: never retain data for permissions revoked during the request.
        val finalGranted=client.permissionController.getGrantedPermissions()
        return maskRevokedHealth(listOf(weight,fat,sleep,nutrition),listOf(
            HealthPermission.getReadPermission(WeightRecord::class),HealthPermission.getReadPermission(BodyFatRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),HealthPermission.getReadPermission(NutritionRecord::class)
        ),finalGranted)
    }
}

@Composable fun HealthConnectPanel(compact:Boolean=false){
    val context=LocalContext.current;val lifecycle=LocalLifecycleOwner.current.lifecycle
    val prefs=remember{context.getSharedPreferences("stem_settings",0)}
    var enabled by remember{mutableStateOf(prefs.getBoolean("health_enabled",false))}
    var revision by remember{mutableIntStateOf(0)}
    var foreground by remember{mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))}
    var availability by remember{mutableIntStateOf(HealthConnectClient.SDK_UNAVAILABLE)}
    var metrics by remember{mutableStateOf(emptyList<HealthMetric>())}
    var status by remember{mutableStateOf("")};var busy by remember{mutableStateOf(false)}
    var privacy by remember{mutableStateOf(false)}
    val scope=rememberCoroutineScope()
    val launcher=rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()){granted->
        enabled=granted.any{it in healthReadPermissions};prefs.edit().putBoolean("health_enabled",enabled).apply();revision++
    }
    DisposableEffect(lifecycle){val observer=LifecycleEventObserver{_,event->if(event==Lifecycle.Event.ON_RESUME){foreground=true;revision++}else if(event==Lifecycle.Event.ON_PAUSE){foreground=false;metrics=emptyList()}};lifecycle.addObserver(observer);onDispose{lifecycle.removeObserver(observer)}}
    LaunchedEffect(foreground,enabled,revision){
        metrics=emptyList();status="";busy=false
        if(!foreground)return@LaunchedEffect
        availability=HealthConnectClient.getSdkStatus(context)
        if(enabled && availability==HealthConnectClient.SDK_AVAILABLE){
            busy=true
            try{metrics=HealthReader(HealthConnectClient.getOrCreate(context)).read();status="Обновлено: ${healthTime(Instant.now())}"}
            catch(e:CancellationException){throw e}
            catch(e:Exception){status="Health Connect недоступен или доступ отозван. Проверьте разрешения."}
            finally{busy=false}
        }
    }
    if(compact && !enabled)return
    Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
        Text("Health Connect",style=MaterialTheme.typography.titleMedium)
        Text("Текущие данные · только чтение",style=MaterialTheme.typography.labelSmall)
        if(availability==HealthConnectClient.SDK_UNAVAILABLE)Text("Health Connect недоступен на этом устройстве.")
        else if(availability==HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED)TextButton({runCatching{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")))}}){Text("Установить / обновить Health Connect")}
        else {
            if(!compact)TextButton({privacy=true}){Text(if(enabled)"Разрешения и использование данных" else "Подключить Health Connect")}
            if(enabled){
                if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
                metrics.forEach{m->Text("${m.title}: ${m.value}");if(m.detail.isNotBlank())Text(m.detail,style=MaterialTheme.typography.labelSmall)}
                Text(status,style=MaterialTheme.typography.labelSmall)
                TextButton({revision++},enabled=!busy){Text("Обновить")}
                if(!compact)TextButton({
                    enabled=false;metrics=emptyList();prefs.edit().putBoolean("health_enabled",false).apply()
                    scope.launch{try{HealthConnectClient.getOrCreate(context).permissionController.revokeAllPermissions();status="Отключено"}catch(e:CancellationException){throw e}catch(e:Exception){status="Чтение отключено. Отзовите разрешения в Health Connect."}}
                }){Text("Отключить")}
            }
            if(!compact)TextButton({runCatching{context.startActivity(Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS))}.onFailure{status="Откройте Health Connect в настройках Android"}}){Text("Открыть Health Connect")}
        }
        Text("Возраст и процент мышц не импортируются. Нет записи ≠ нулевое значение. Показатели не изменяют программу автоматически.",style=MaterialTheme.typography.labelSmall)
    }}
    if(privacy)AlertDialog(onDismissRequest={privacy=false},title={Text("Доступ к данным здоровья")},text={Text(HEALTH_PRIVACY,Modifier.verticalScroll(rememberScrollState()))},confirmButton={TextButton({privacy=false;runCatching{launcher.launch(healthReadPermissions)}.onFailure{status="Не удалось открыть запрос разрешений"}}){Text("Выбрать разрешения")}},dismissButton={TextButton({privacy=false}){Text("Отмена")}})
}
