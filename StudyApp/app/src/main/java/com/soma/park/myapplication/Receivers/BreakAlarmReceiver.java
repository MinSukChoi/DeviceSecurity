package com.soma.park.myapplication.Receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.Services.ScreenService;

import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 30..
 */
public class BreakAlarmReceiver extends BroadcastReceiver {
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private static final String TAG = "Break";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);

        if(pref.getBoolean("alarmstate", false)) {
            int position = intent.getIntExtra("position", 0);

            referenceMonitor.setBreaktimemode();
            Log.d(TAG, "BreakAlarmReceiver !!");
            Toast.makeText(context, pref.getInt("breakTime", 10) + "분 동안 휴식모드를 실행합니다", Toast.LENGTH_LONG).show();

            Intent intent1 = new Intent(context, ScreenService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.stopService(intent1);

            int time = pref.getInt("breakTime", 10);
            Intent i = new Intent(context, StudyAlarmReceiver.class);
            i.putExtra("position", position);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, position, i, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
            }
        }
    }
}
