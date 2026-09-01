package com.stem.stemtraining

fun estimatedOneRepMax(weight:Double,reps:Int)=weight*(1.0+reps/30.0)
fun platesPerSide(totalWeight:Double,barWeight:Double=20.0,available:List<Double> = listOf(25.0,20.0,15.0,10.0,5.0,2.5,1.25)):List<Double>{var left=((totalWeight-barWeight)/2).coerceAtLeast(0.0);val result=mutableListOf<Double>();available.forEach{plate->while(left+0.001>=plate){result+=plate;left-=plate}};return result}
fun workoutVolume(sets:List<Pair<Double,Int>>)=sets.sumOf{it.first*it.second}
