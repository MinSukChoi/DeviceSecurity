package com.example.park.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 30..
 */
public class BreakAlarmReceiver extends BroadcastReceiver {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Toast.makeText(context, pref.getInt("breakTime", 1)+"분 동안 휴식모드를 실행합니다", Toast.LENGTH_LONG).show();

        ScreenService mService = new ScreenService();
        mService.reservState = false;

        Intent i = new Intent(context, ScreenService.class);
        context.stopService(i);

        int time = pref.getInt("breakTime", 1);
        Intent intentReceiver = new Intent(context, AlarmStartReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentReceiver, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
    }
}
