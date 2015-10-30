package com.example.park.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 30..
 */
public class StudyAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Study";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt("state", 1);  // 공부 모드 1
        editor.commit();

        Log.d(TAG, "StudyAlarmReceiver !!");
        Toast.makeText(context, "휴식모드가 종료되었습니다 " + pref.getInt("studyTime", 1)+"분 동안 공부모드를 실행합니다", Toast.LENGTH_LONG).show();

        ScreenService mService = new ScreenService();
        mService.reservState = true;

        Intent i = new Intent(context, ScreenService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);

        int time = pref.getInt("studyTime", 1);
        Intent intentReceiver = new Intent(context, BreakAlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 1, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
    }
}
