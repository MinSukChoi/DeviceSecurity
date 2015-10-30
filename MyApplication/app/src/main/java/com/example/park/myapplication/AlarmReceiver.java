package com.example.park.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean[] week = intent.getBooleanArrayExtra("weekday");
        Log.i("AlarmReceiver", "|일 : " + week[Calendar.SUNDAY]);
        Log.i("AlarmReceiver", "|월 : " + week[Calendar.MONDAY]);
        Log.i("AlarmReceiver", "|화 : " + week[Calendar.TUESDAY]);
        Log.i("AlarmReceiver", "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i("AlarmReceiver", "|목 : " + week[Calendar.THURSDAY]);
        Log.i("AlarmReceiver", "|금 : " + week[Calendar.FRIDAY]);
        Log.i("AlarmReceiver", "|토 : " + week[Calendar.SATURDAY]);

        Calendar cal = Calendar.getInstance();
        Log.i("AlarmReceiver", "|" + cal.get(Calendar.DAY_OF_WEEK) + "|");
        // 오늘 요일의 알람 재생이 true이면 사운드 재생

        if (!week[cal.get(Calendar.DAY_OF_WEEK)])
            return;

//        Intent i = new Intent(context, ScreenService.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(i);

    }
}
