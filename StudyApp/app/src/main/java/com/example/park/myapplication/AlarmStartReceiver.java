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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmStartReceiver extends BroadcastReceiver {
    private static final String TAG = "Reservation_Start_Receiver";
    ScreenService mService;
    int dayNum = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean[] week = intent.getBooleanArrayExtra("weekday");
        boolean checkweek = intent.getBooleanExtra("checkweek", false); //매주 반복

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        editor = pref.edit();
//        editor.putBoolean("alarmstate", true);
//        editor.putInt("alert", 1);
//        editor.putInt("alertNum", pref.getInt("alertInitialNum", 1));
//        editor.putInt("alertTime", pref.getInt("alertInitialTime", 1));
//        editor.commit();

        Log.i("AlarmStartReceiver", "|긴급모드 횟수 : " + pref.getInt("alertInitialNum", 1));
        Log.i("AlarmStartReceiver", "|긴급모드 시간 : " + pref.getInt("alertInitialTime", 1));

        Log.i("AlarmStartReceiver", "|일 : " + week[Calendar.SUNDAY]);
        Log.i("AlarmStartReceiver", "|월 : " + week[Calendar.MONDAY]);
        Log.i("AlarmStartReceiver", "|화 : " + week[Calendar.TUESDAY]);
        Log.i("AlarmStartReceiver", "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i("AlarmStartReceiver", "|목 : " + week[Calendar.THURSDAY]);
        Log.i("AlarmStartReceiver", "|금 : " + week[Calendar.FRIDAY]);
        Log.i("AlarmStartReceiver", "|토 : " + week[Calendar.SATURDAY]);
        Log.i("AlarmStartReceiver", "|반복 : " + checkweek);

        // 매주 반복이 아닌 경우
        for(int i=0; i<=7; i++) {
            if(week[i]) {
                dayNum++;
            }
        }
        Log.d("선택된 요일 수 : ", String.valueOf(dayNum));

        mService = new ScreenService();
        Calendar cal = Calendar.getInstance();
        Log.i("AlarmStartReceiver", "|" + cal.get(Calendar.DAY_OF_WEEK) + "|");

        // 오늘 요일이 아닐 때
        if (!week[cal.get(Calendar.DAY_OF_WEEK)]) {
            return;
        } else {
            // 오늘 요일의 알람이 true이면 서비스 실행
            Toast.makeText(context, "공부 시간이 시작되었습니다^^ 화이팅!", Toast.LENGTH_LONG).show();

            onRegist(context);

            mService.reservState = true;
            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }
    }

    private void onRegist(Context context) {
        int time = pref.getInt("studyTime", 60);
        Intent intent = new Intent(context, BreakAlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
    }
}
