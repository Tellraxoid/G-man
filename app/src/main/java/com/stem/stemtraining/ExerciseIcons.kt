package com.stem.stemtraining

import androidx.annotation.DrawableRes

@DrawableRes fun exerciseIcon(name:String):Int=when{
    name.contains("присед",true)||name.contains("выпад",true)||name.contains("ног",true)||name.contains("нос",true)->R.drawable.exercise_squat
    name.contains("станов",true)->R.drawable.exercise_deadlift
    name.contains("подтяг",true)||name.contains("верхнего блока",true)->R.drawable.exercise_pullup
    name.contains("тяга",true)->R.drawable.exercise_row
    name.contains("сгибание рук",true)||name.contains("молот",true)->R.drawable.exercise_curl
    name.contains("скручив",true)||name.contains("подъём ног",true)||name.contains("планк",true)->R.drawable.exercise_core
    name.contains("сидя",true)||name.contains("стороны",true)||name.contains("плеч",true)||name.contains("шраг",true)->R.drawable.exercise_press
    else->R.drawable.exercise_bench
}
