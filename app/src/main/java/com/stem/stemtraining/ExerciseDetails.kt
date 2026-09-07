package com.stem.stemtraining

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stem.stemtraining.data.TrainingDatabase
import com.stem.stemtraining.data.WorkoutSetEntity
import kotlin.math.round

fun estimatedOneRepMax(weight:Double,reps:Int):Double = if(weight<=0 || reps<=0) 0.0 else weight*(1.0+reps/30.0)

fun suggestedNextWeight(previous:List<WorkoutSetEntity>, step:Double):Pair<Double,String>? {
    val work=previous.filterNot{it.isWarmup}.filter{it.weight>0}
    if(work.isEmpty())return null
    val base=work.maxOf{it.weight}
    val labels=work.mapNotNull{it.effort}
    if(labels.isEmpty())return base to "Отметьте «Легко / Нормально / Тяжело» после рабочих подходов — тогда совет станет точнее."
    val heavy=labels.count{it=="Тяжело"}
    val normal=labels.count{it=="Нормально"}
    val easy=labels.count{it=="Легко"}
    val increment=when {
        heavy==labels.size -> 0.0
        heavy>easy && heavy>=normal -> step*0.5
        easy==labels.size -> step*2.0
        easy>normal && easy>heavy -> step*1.5
        else -> step
    }
    val safeStep=step.coerceAtLeast(0.5)
    val target=round((base+increment)/safeStep)*safeStep
    val reason=when {
        heavy==labels.size -> "Все рабочие подходы были тяжёлыми: вес лучше оставить прежним."
        heavy>easy && heavy>=normal -> "Тяжёлых подходов было много: повышение минимальное."
        easy==labels.size -> "Все рабочие подходы были лёгкими: можно прибавить заметнее."
        easy>normal && easy>heavy -> "Преобладала оценка «Легко»: можно увеличить нагрузку сильнее обычного."
        else -> "Нагрузка была нормальной: рекомендуется стандартный шаг прогрессии."
    }
    return target to reason
}

fun exerciseDescription(name:String):String = when {
    name.contains("тяга штанги в наклоне",true) -> "Базовое упражнение для широчайших, ромбовидных, задней дельты и разгибателей спины. Наклоните корпус, удерживайте нейтральную спину и тяните штангу к нижней части живота. Не превращайте движение в рывок корпусом."
    name.contains("жим лёжа узким хватом",true) -> "Вариант жима лёжа с повышенной нагрузкой на трицепс. Хват уже обычного, но не чрезмерно узкий. Лопатки сведены, локти идут ближе к корпусу, штанга опускается контролируемо к нижней части груди."
    name.contains("разгибание",true) && name.contains("штанг",true) && name.contains("лёжа",true) -> "Изолирующее упражнение на трицепс, часто называемое французским жимом лёжа или skull crusher. Плечи держите относительно неподвижно, сгибание происходит преимущественно в локтях. Опускайте штангу контролируемо за линию лба или к верхней части головы."
    name.contains("жим лёжа",true) -> "Базовое жимовое движение для грудных мышц, трицепсов и передней дельты. Сведите лопатки, держите стопы устойчиво, опускайте вес под контролем и сохраняйте стабильную траекторию."
    name.contains("присед",true) -> "Базовое движение для квадрицепсов, ягодичных и мышц корпуса. Сохраняйте жёсткий корпус, контролируйте колени по направлению носков и выбирайте глубину, в которой сохраняется стабильная техника."
    name.contains("станов",true) -> "Базовая тяга для задней цепи: ягодичных, бицепса бедра, разгибателей спины и трапеций. Держите гриф близко к ногам и не теряйте нейтральное положение позвоночника."
    name.contains("тяга",true) -> "Тяговое упражнение для мышц спины. Начинайте движение сведением лопаток, затем ведите локти назад. Избегайте чрезмерного раскачивания и сохраняйте контролируемую негативную фазу."
    name.contains("сгибание рук",true) || name.contains("молот",true) -> "Упражнение на сгибатели локтя. Сохраняйте плечо стабильным, не раскачивайте корпус и выполняйте полную контролируемую амплитуду."
    name.contains("разгибание рук на блоке",true) -> "Изолирующее упражнение на трицепс. Локти держите близко к корпусу, плечи неподвижны, внизу полностью сокращайте трицепс без раскачивания."
    name.contains("жим сидя",true) -> "Жимовое упражнение для плеч с акцентом на переднюю и среднюю дельту. Стабилизируйте корпус, не переразгибайте поясницу и контролируйте движение веса."
    name.contains("выпад",true) -> "Одностороннее упражнение для квадрицепсов и ягодичных. Держите корпус устойчиво, колено направляйте по линии стопы и сохраняйте равномерный контроль на каждом повторении."
    name.contains("подтяг",true) -> "Базовое упражнение для широчайших, бицепсов и мышц верхней части спины. Начинайте движение плечевым поясом и избегайте раскачивания."
    name.contains("скручив",true) || name.contains("подъём ног",true) || name.contains("планк",true) -> "Упражнение для мышц корпуса. Сохраняйте контролируемое движение и не компенсируйте амплитуду поясницей."
    else -> "Выполняйте упражнение в контролируемой амплитуде, сохраняйте стабильную технику и повышайте нагрузку только при уверенном выполнении всех рабочих подходов."
}

@Composable
fun ExerciseDetailsDialog(name:String,onDismiss:()->Unit){
    val context=LocalContext.current
    val dao=remember{TrainingDatabase.getInstance(context).trainingDao()}
    val previous by remember(name){dao.observePreviousExerciseSets(name)}.collectAsState(initial=emptyList())
    val points by remember(name){dao.observeProgressPoints(name)}.collectAsState(initial=emptyList())
    val step=context.getSharedPreferences("stem_settings",0).getFloat("weight_step",2.5f).toDouble()
    val suggestion=suggestedNextWeight(previous,step)
    val best1Rm=points.maxOfOrNull{it.estimated1Rm}?:previous.filterNot{it.isWarmup}.maxOfOrNull{estimatedOneRepMax(it.weight,it.reps)}?:0.0
    AlertDialog(
        onDismissRequest=onDismiss,
        title={Text(name)},
        text={Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(12.dp)){
            Image(painterResource(exerciseIcon(name)),null,Modifier.fillMaxWidth().height(170.dp),contentScale=ContentScale.Fit)
            val muscle=exerciseCatalog.firstOrNull{it.name==name}?.muscle
            muscle?.let{Text(it,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.secondary)}
            Text(exerciseDescription(name))
            HorizontalDivider()
            Text("Расчётный 1ПМ: ${number(best1Rm)} кг",fontWeight=FontWeight.Bold)
            suggestion?.let{(weight,reason)->
                Text("Совет на следующую тренировку: ${number(weight)} кг",fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)
                Text(reason,style=MaterialTheme.typography.bodySmall)
            } ?: Text("После первой завершённой тренировки появится совет по следующему весу.",style=MaterialTheme.typography.bodySmall)
            Text("График прогресса находится во вкладке «Прогресс», а не в карточке упражнения.",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }},
        confirmButton={TextButton(onDismiss){Text("Закрыть")}}
    )
}
