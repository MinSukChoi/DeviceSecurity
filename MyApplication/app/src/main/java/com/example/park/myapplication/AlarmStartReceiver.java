package com.example.park.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlarmStartReceiver extends BroadcastReceiver {
    private static final String TAG = "Reservation_Start_Receiver";
    ScreenService mService;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean[] week = intent.getBooleanArrayExtra("weekday");
        boolean checkweek = intent.getBooleanExtra("checkweek", false);

        Log.i("AlarmStartReceiver", "|일 : " + week[Calendar.SUNDAY]);
        Log.i("AlarmStartReceiver", "|월 : " + week[Calendar.MONDAY]);
        Log.i("AlarmStartReceiver", "|화 : " + week[Calendar.TUESDAY]);
        Log.i("AlarmStartReceiver", "|수 : " + week[Calendar.WEDNESDAY]);
        Log.i("AlarmStartReceiver", "|목 : " + week[Calendar.THURSDAY]);
        Log.i("AlarmStartReceiver", "|금 : " + week[Calendar.FRIDAY]);
        Log.i("AlarmStartReceiver", "|토 : " + week[Calendar.SATURDAY]);
        Log.i("AlarmStartReceiver", "|반복 : " + checkweek);

        mService = new ScreenService();
        Calendar cal = Calendar.getInstance();
        Log.i("AlarmStartReceiver", "|" + cal.get(Calendar.DAY_OF_WEEK) + "|");

        // 오늘 요일이 아닐 때
        if (!week[cal.get(Calendar.DAY_OF_WEEK)]) {
            return;
        } else {
            // 오늘 요일의 알람이 true이면 서비스 실행
            Toast.makeText(context, "예약 시간이 시작되었습니다^^ 화이팅!", Toast.LENGTH_LONG).show();
            mService.reservState = true;
            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }
    }
}
