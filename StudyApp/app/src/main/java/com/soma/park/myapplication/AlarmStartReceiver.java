package com.soma.park.myapplication;

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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmStartReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmStartReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);

        boolean[] week = intent.getBooleanArrayExtra("weekday");
        boolean checkweek = intent.getBooleanExtra("checkweek", false); //매주 반복
        int position = intent.getIntExtra("position", 0);
        int oneDay = 0;
        Calendar cal = Calendar.getInstance();

        Log.i(TAG, "|긴급모드 횟수 : " + pref.getInt("alertNum", 3));
        Log.i(TAG, "|긴급모드 시간 : " + pref.getInt("alertTime", 15));
        Log.i(TAG, "|휴식모드 공부 : " + pref.getInt("studyTime", 50));
        Log.i(TAG, "|휴식모드 휴식 : " + pref.getInt("breakTime", 10));
        Log.i(TAG, "|일 : " + week[Calendar.SUNDAY]);
        Log.i(TAG, "|월 : " + week[Calendar.MONDAY]);
        Log.i(TAG, "|화 : " + week[Calendar.TUESDAY]);
        Log.i(TAG, "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i(TAG, "|목 : " + week[Calendar.THURSDAY]);
        Log.i(TAG, "|금 : " + week[Calendar.FRIDAY]);
        Log.i(TAG, "|토 : " + week[Calendar.SATURDAY]);
        Log.i(TAG, "|매주 반복 : " + checkweek);
        Log.i(TAG, "|오늘 요일 : " + cal.get(Calendar.DAY_OF_WEEK));

        // 요일 체크 안했을 경우 당일만 알람 실행
        for(int i = 0; i <= 7; i++) {
            if(!week[i]) {
                oneDay++;
            }
        }
        if(oneDay == 8) {
            week[cal.get(Calendar.DAY_OF_WEEK)] = true;
            oneDay = 0;
        }

        // 오늘 요일이 아닐 때
        if (!week[cal.get(Calendar.DAY_OF_WEEK)]) {
            return;
        } else {
            // 오늘 요일의 알람이 true이면 서비스 실행
            referenceMonitor.setStudymode();
            int time = pref.getInt("studyTime", 50);
            Intent i = new Intent(context, BreakAlarmReceiver.class);
            i.putExtra("position", position);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);
            }

            editor = pref.edit();
            editor.putBoolean("alarmstate", true); // 예약 잠금 true, 아니면 false
            editor.putString("currentTitle", pref.getString("title" + position, ""));
            String currentStart = String.valueOf(pref.getInt("startHour"+position, 0)+":"+pref.getInt("startMin"+position, 0));
            String currentEnd = String.valueOf(pref.getInt("endHour" + position, 0) + ":" + pref.getInt("endMin" + position, 0));
            editor.putString("currentStart", currentStart);
            editor.putString("currentEnd", currentEnd);
            editor.commit();

            Intent intent1 = new Intent(context, ScreenService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);

            Toast.makeText(context, "예약 시간이 시작되었습니다 화이팅!", Toast.LENGTH_LONG).show();
        }
    }
}
