package com.stem.stemtraining

import com.stem.stemtraining.data.PreviousWorkoutSetRow

fun estimatedOneRepMax(weight:Double,reps:Int)=when{weight<=0||reps<=0->0.0;reps==1->weight;else->weight*(1.0+reps/30.0)}
data class LoadSuggestion(val weight:Double,val change:Double,val reason:String)
fun suggestedNextWeight(sets:List<PreviousWorkoutSetRow>,step:Double):LoadSuggestion?{
    val valid=sets.filter{it.weight>0&&it.reps>0};if(valid.isEmpty())return null
    val safeStep=step.coerceAtLeast(0.5)
    val base=valid.groupingBy{it.weight}.eachCount().entries.sortedWith(compareByDescending<Map.Entry<Double,Int>>{it.value}.thenByDescending{it.key}).first().key
    val efforts=valid.mapNotNull{it.effort}
    val multiplier=when{efforts.size<valid.size->0;efforts.any{it=="Тяжело"}->0;efforts.all{it=="Легко"}->2;else->1}
    val change=safeStep*multiplier
    val reason=when{efforts.size<valid.size->"Не все подходы оценены — повторите основной вес и отметьте усилие.";multiplier==0->"Было тяжело — вес пока не повышаем.";multiplier==2->"Все подходы были лёгкими — можно прибавить два шага.";else->"Нагрузка была нормальной — прибавьте один шаг."}
    return LoadSuggestion(base+change,change,reason)
}
fun platesPerSide(totalWeight:Double,barWeight:Double=20.0,available:List<Double> = listOf(25.0,20.0,15.0,10.0,5.0,2.5,1.25)):List<Double>{var left=((totalWeight-barWeight)/2).coerceAtLeast(0.0);val result=mutableListOf<Double>();available.forEach{plate->while(left+0.001>=plate){result+=plate;left-=plate}};return result}
fun workoutVolume(sets:List<Pair<Double,Int>>)=sets.sumOf{it.first*it.second}
