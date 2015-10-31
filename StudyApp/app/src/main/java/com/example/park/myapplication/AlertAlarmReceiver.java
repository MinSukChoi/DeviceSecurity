package com.example.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlertAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Alert_Receiver";
    ScreenService mService = new ScreenService();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        // 긴급모드 끝날 때 휴식모드가 실행되면 휴식모드 무시되고 락스크린 뜸
        // 긴급모드 끝날 때 공부모드면 종료. 휴식모드면 무시.
        if(pref.getInt("state", 1) == 1) {
            mService.reservState = true;
            Toast.makeText(context, "긴급 모드가 종료되었습니다", Toast.LENGTH_LONG).show();

            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }
    }
}
