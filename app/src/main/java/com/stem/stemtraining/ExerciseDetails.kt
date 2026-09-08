package com.stem.stemtraining

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

data class ExerciseGuide(val muscle:String,val summary:String,val technique:String,val mistakes:String)

fun exerciseGuide(name:String):ExerciseGuide {
    val muscle=exerciseCatalog.firstOrNull{it.name.equals(name,true)}?.muscle ?: "Укажите группу мышц в библиотеке"
    fun guide(summary:String,technique:String,mistakes:String)=ExerciseGuide(muscle,summary,technique,mistakes)
    return when {
        name.contains("узким хватом",true)->guide("Базовый жим с акцентом на трицепс.","Лягте устойчиво, сведите лопатки и поставьте стопы на пол. Возьмите гриф немного уже плеч. Опускайте к нижней части груди, ведя локти близко к корпусу, затем выжмите без отрыва таза.","Слишком узкий хват, разведённые локти, отрыв таза и удар грифом о грудь.")
        name.contains("разгибание",true)&&name.contains("лёжа",true)&&name.contains("штанг",true)->guide("Изолированное разгибание локтя для трицепса.","Лягте на скамью и удерживайте гриф над плечами узким хватом. Зафиксируйте плечи почти вертикально. Сгибайте только локти, плавно опуская гриф ко лбу, затем разогните руки.","Движение плечами, слишком большой вес, разведение локтей и быстрое опускание к голове.")
        name.contains("шраг",true)->guide("Подъём плеч для верхней части трапеций.","Стойте ровно, держите руки прямыми. Поднимайте плечи вертикально к ушам, коротко фиксируйте верхнюю точку и подконтрольно опускайте.","Вращение плечами, сгибание локтей, раскачивание корпуса и запрокидывание головы.")
        name.contains("тяга",true)->guide("Тяговое движение для мышц спины.","Стабилизируйте корпус и сохраняйте нейтральную спину. Начните движение сведением лопаток, тяните снаряд локтями к корпусу и возвращайте его под контролем.","Круглая спина, рывок, чрезмерная амплитуда и движение только предплечьями.")
        name.contains("жим",true)->guide("Жимовое движение для груди, плеч или трицепса.","Создайте устойчивую опору, зафиксируйте лопатки и запястья. Опускайте снаряд подконтрольно и выжимайте по естественной траектории без удара в суставные ограничения.","Рывки, сломанные запястья, потеря опоры и вес, мешающий полной контролируемой амплитуде.")
        name.contains("разведен",true)||name.contains("разведён",true)->guide("Изолирующее движение с постоянным контролем плеча.","Слегка согните локти и сохраните этот угол. Двигайте руки симметрично, остановитесь до потери положения плеч и плавно вернитесь.","Раскачивание, превращение движения в жим, слишком прямые локти и чрезмерная амплитуда.")
        name.contains("сгибание",true)->guide("Сгибание локтя с акцентом на бицепс.","Зафиксируйте плечи и корпус. Согните локти без раскачивания, сократите бицепс и медленно опустите вес.","Увод локтей вперёд, помощь спиной, падение веса и неполная амплитуда.")
        name.contains("присед",true)||name.contains("выпад",true)||name.contains("ног",true)->guide("Упражнение для мышц ног с контролем коленей и таза.","Сохраняйте устойчивую стопу и нейтральную спину. Направляйте колени по линии носков, выполняйте доступную контролируемую амплитуду и поднимайтесь без рывка.","Завал стоп и коленей, потеря нейтральной спины, отрыв опоры и неконтролируемое опускание.")
        name.contains("скручив",true)||name.contains("планк",true)||name.contains("подъём ног",true)->guide("Упражнение для мышц корпуса.","Удерживайте рёбра и таз под контролем, двигайтесь за счёт мышц живота и дышите без задержки. Остановитесь при потере нейтрального положения поясницы.","Рывки, тяга шеей, провисание поясницы и выполнение за счёт инерции.")
        else->guide("Силовое упражнение из вашей библиотеки.","Настройте устойчивое исходное положение, выполняйте движение плавно и сохраняйте контролируемую амплитуду.","Рывки, потеря техники и нагрузка, которую невозможно контролировать.")
    }
}

@Composable fun ExerciseDetailsDialog(name:String,dismiss:()->Unit){
    val guide=exerciseGuide(name)
    AlertDialog(onDismissRequest=dismiss,title={Text(name)},text={Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
        Surface(color=androidx.compose.ui.graphics.Color.White,shape=MaterialTheme.shapes.medium){Image(painterResource(exerciseIcon(name)),name,Modifier.fillMaxWidth().height(230.dp),contentScale=ContentScale.Fit)}
        AssistChip(onClick={},label={Text(guide.muscle)})
        Text(guide.summary)
        Text("Как выполнять",style=MaterialTheme.typography.titleSmall);Text(guide.technique)
        Text("Частые ошибки",style=MaterialTheme.typography.titleSmall);Text(guide.mistakes)
        Text("График и расчётный 1ПМ находятся в разделе «Прогресс».",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }},confirmButton={TextButton(dismiss){Text("Закрыть")}})
}
