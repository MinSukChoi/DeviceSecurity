package com.example.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmStopReceiver extends BroadcastReceiver {
    private static final String TAG = "Reservation_Stop_Receiver";
    ScreenService mService;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean[] week = intent.getBooleanArrayExtra("weekday");
        boolean checkweek = intent.getBooleanExtra("checkweek", false);

        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean("alarmstate", false);
        editor.commit();

        Log.i("AlarmStopReceiver", "|일 : " + week[Calendar.SUNDAY]);
        Log.i("AlarmStopReceiver", "|월 : " + week[Calendar.MONDAY]);
        Log.i("AlarmStopReceiver", "|화 : " + week[Calendar.TUESDAY]);
        Log.i("AlarmStopReceiver", "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i("AlarmStopReceiver", "|목 : " + week[Calendar.THURSDAY]);
        Log.i("AlarmStopReceiver", "|금 : " + week[Calendar.FRIDAY]);
        Log.i("AlarmStopReceiver", "|토 : " + week[Calendar.SATURDAY]);
        Log.i("AlarmStopReceiver", "|반복 : " + checkweek);

        mService = new ScreenService();
        Calendar cal = Calendar.getInstance();
        Log.i("AlarmStopReceiver", "|" + cal.get(Calendar.DAY_OF_WEEK) + "|");

        // 오늘 요일이 아닐 때
        if (!week[cal.get(Calendar.DAY_OF_WEEK)]) {
            return;
        } else {
            // 오늘 요일의 알람 재생이 true이면 서비스 정지
            Toast.makeText(context, "공부 시간이 종료되었습니다^^", Toast.LENGTH_LONG).show();
            mService.reservState = false;
            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.stopService(i);
        }
    }
}
