package com.soma.park.myapplication.Receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.soma.park.myapplication.AlertModePreventDelete;
import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.Services.ScreenService;

/**
 * Created by PARK on 15. 11. 8..
 */

public class NowStopReceiver extends BroadcastReceiver {
    private static final String TAG = "NowStopReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(pref.getBoolean("alarmstate", false) == false) {
            if(referenceMonitor.getSTATE() == referenceMonitor.ALERTMODE){
                AlertModePreventDelete alertModePreventDelete = AlertModePreventDelete.getInstance();
                alertModePreventDelete.setOffPreventMode();

                Intent intentReceiver = new Intent(context, AlertAlarmReceiver.class);
                PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pIntent);
            }

            referenceMonitor.setNormalmode();
            Intent intent1 = new Intent(context, ScreenService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.stopService(intent1);

            editor = pref.edit();
            editor.putBoolean("lockstate", false);
            editor.commit();

            Toast.makeText(context, "한 시간의 공부 시간이 종료되었습니다^^", Toast.LENGTH_LONG).show();
        }
    }
}