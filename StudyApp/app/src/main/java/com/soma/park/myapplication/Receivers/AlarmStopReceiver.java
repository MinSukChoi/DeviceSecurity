package com.soma.park.myapplication.Receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.soma.park.myapplication.AlertModePreventDelete;
import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.Services.ScreenService;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmStopReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmStopReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;
    private int dayNum = 0;
    private int count = 0;
    int oneDay = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);

        boolean[] week = intent.getBooleanArrayExtra("weekday");
        boolean checkweek = intent.getBooleanExtra("checkweek", false);
        int position = intent.getIntExtra("position", 0);
        Calendar cal = Calendar.getInstance();

        Log.i(TAG, "|일 : " + week[Calendar.SUNDAY]);
        Log.i(TAG, "|월 : " + week[Calendar.MONDAY]);
        Log.i(TAG, "|화 : " + week[Calendar.TUESDAY]);
        Log.i(TAG, "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i(TAG, "|목 : " + week[Calendar.THURSDAY]);
        Log.i(TAG, "|금 : " + week[Calendar.FRIDAY]);
        Log.i(TAG, "|토 : " + week[Calendar.SATURDAY]);
        Log.i(TAG, "|오늘 요일 : " + cal.get(Calendar.DAY_OF_WEEK) + "|");

        for (int i = 0; i <= 7; i++) {
            if (!week[i]) {
                oneDay++;
            } else {
                dayNum++;
            }
        }
        // 요일 체크 안했을 경우 당일만 알람 실행
        if (oneDay == 8) {
            week[cal.get(Calendar.DAY_OF_WEEK)] = true;
            dayNum = 1;
            oneDay = 0;
        }

        Log.i(TAG, "|매주 반복 : " + checkweek);
        Log.i(TAG, "|position : " + position);
        Log.i(TAG, "|선택된 요일 수 : " + String.valueOf(dayNum));

        // 오늘 요일이 아닐 때
        if (!week[cal.get(Calendar.DAY_OF_WEEK)]) {
            return;
        } else {
            // 오늘 요일의 알람 재생이 true이면 서비스 정지
            if(referenceMonitor.getSTATE() == referenceMonitor.ALERTMODE){
                AlertModePreventDelete alertModePreventDelete = AlertModePreventDelete.getInstance();
                alertModePreventDelete.setOffPreventMode();

                Intent intentReceiver = new Intent(context, AlertAlarmReceiver.class);
                PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pIntent);
            }

            referenceMonitor.setNormalmode();

            int tree = pref.getInt("tree",0);
            editor = pref.edit();
            editor.putBoolean("alarmstate", false);
            editor.putInt("tree",tree+1);
            editor.commit();

            Intent intent1 = new Intent(context, ScreenService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.stopService(intent1);

            Toast.makeText(context, "예약 시간이 종료되었습니다^^", Toast.LENGTH_LONG).show();

            if (!checkweek) { // 매주 반복이 아니면
                count++;
                Log.d(TAG, "count: " + String.valueOf(count));
                if (count == dayNum) {
                    onUnregist(position);
                    count = 0;
                    dayNum = 0;
                }
            }
        }
    }

    private void onUnregist(int pos)
    {
        Log.d(TAG, String.valueOf(pos)+"번째 예약 끝");
        editor = pref.edit();
        editor.putBoolean("checkValue"+pos, false);
        editor.commit();
    }
}
