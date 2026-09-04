package com.stem.integration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.Button;

/** Navigation contract v1 shared by the four independently installed S.T.E.M. modules. */
public final class StemHub {
    private static final String[] PACKAGES={"com.stem.companion","com.stem.stemtraining","com.stem.nutrition","com.stem.money"};
    private static final String[] LABELS={"Companion · помощник","Training · тренировки","Nutrition · питание","Money · финансы"};
    private StemHub() {}
    public static void show(Activity activity){
        String[] labels=new String[LABELS.length];
        for(int i=0;i<labels.length;i++) labels[i]=LABELS[i]+(activity.getPackageName().equals(PACKAGES[i])?" · текущий":activity.getPackageManager().getLaunchIntentForPackage(PACKAGES[i])==null?" · не установлен":"");
        new AlertDialog.Builder(activity).setTitle("S.T.E.M. · Все модули").setItems(labels,(dialog,index)->{
            if(activity.getPackageName().equals(PACKAGES[index]))return;
            Intent intent=activity.getPackageManager().getLaunchIntentForPackage(PACKAGES[index]);
            if(intent==null){new AlertDialog.Builder(activity).setTitle("Модуль не установлен").setMessage("Установите APK S.T.E.M. "+LABELS[index]+", затем откройте модуль здесь.").setPositiveButton("Понятно",null).show();return;}
            try{intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);activity.startActivity(intent);}
            catch(android.content.ActivityNotFoundException | SecurityException error){new AlertDialog.Builder(activity).setTitle("Не удалось открыть модуль").setMessage("Проверьте установку приложения и повторите попытку.").setPositiveButton("Понятно",null).show();}
        }).setNegativeButton("Закрыть",null).show();
    }
    public static Button button(Activity activity){
        Button button=new Button(activity);button.setText("Все модули S.T.E.M.");button.setAllCaps(false);button.setTextSize(16);
        button.setMinHeight(Math.round(48*activity.getResources().getDisplayMetrics().density));
        button.setTextColor(0xffffffff);button.setBackgroundTintList(ColorStateList.valueOf(0xff167750));
        button.setOnClickListener(view->show(activity));return button;
    }
}
