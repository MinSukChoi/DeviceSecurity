package com.example.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by PARK on 15. 11. 1..
 */
public class DeviceEventReceiver extends BroadcastReceiver {
    private static final String TAG = "Break";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ReservActivity reservActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        reservActivity = new ReservActivity();
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt("alert", 1);
        editor.putInt("alertNum", pref.getInt("alertInitialNum", 1));
        editor.putInt("alertTime", pref.getInt("alertInitialTime", 1));
        editor.commit();

        int size = pref.getInt("size", 0);
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            // 날짜가 변경된 경우 해야 될 작업을 한다.
            for(int i = 1; i <= size; i++) {
                reservActivity.onRegist(i);
            }
        }
    }
}