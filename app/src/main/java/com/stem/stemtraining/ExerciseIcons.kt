package com.stem.stemtraining

import androidx.annotation.DrawableRes

@DrawableRes fun exerciseIcon(name:String):Int=when{
    name.contains("скамье Скотта",true)->R.drawable.exercise_preacher_curl
    name.contains("подъём ног в висе",true)->R.drawable.exercise_hanging_leg_raise
    name.contains("отжимания на брусьях",true)->R.drawable.exercise_dips
    name.contains("разведение рук на наклонной",true)->R.drawable.exercise_incline_fly
    name.contains("разведение рук лёжа",true)->R.drawable.exercise_fly
    name.contains("наклонной скамье",true)&&name.contains("гантел",true)->R.drawable.exercise_incline_dumbbell_press
    name.contains("наклонной скамье",true)&&name.contains("штанг",true)->R.drawable.exercise_incline_barbell_press
    name.contains("жим лёжа",true)&&name.contains("гантел",true)->R.drawable.exercise_dumbbell_bench
    name.contains("отжим",true)->R.drawable.exercise_pushup
    name.contains("тяга верхнего блока",true)->R.drawable.exercise_lat_pulldown
    name.contains("тяга горизонтального блока",true)->R.drawable.exercise_seated_row
    name.contains("выпад",true)->R.drawable.exercise_lunge
    name.contains("жим ногами",true)->R.drawable.exercise_leg_press
    name.contains("сгибание ног",true)->R.drawable.exercise_leg_curl
    name.contains("разгибание ног",true)->R.drawable.exercise_leg_extension
    name.contains("подъём на носки",true)->R.drawable.exercise_calf_raise
    name.contains("разведение рук в стороны",true)->R.drawable.exercise_lateral_raise
    name.contains("разгибание рук на блоке",true)->R.drawable.exercise_triceps_pushdown
    name.contains("планк",true)->R.drawable.exercise_plank
    name.contains("присед",true)->R.drawable.exercise_squat
    name.contains("станов",true)->R.drawable.exercise_deadlift
    name.contains("подтяг",true)->R.drawable.exercise_pullup
    name.contains("тяга",true)->R.drawable.exercise_row
    name.contains("молот",true)->R.drawable.exercise_hammer_curl
    name.contains("сгибание рук",true)&&name.contains("штанг",true)->R.drawable.exercise_barbell_curl
    name.contains("сгибание рук",true)&&name.contains("гантел",true)->R.drawable.exercise_dumbbell_curl
    name.contains("скручив",true)->R.drawable.exercise_crunch
    name.contains("подъём ног",true)->R.drawable.exercise_core
    name.contains("жим сидя",true)||name.contains("плеч",true)||name.contains("шраг",true)->R.drawable.exercise_press
    name.contains("жим лёжа",true)||name.contains("наклонной скамье",true)->R.drawable.exercise_bench
    else->R.drawable.exercise_generic
}
