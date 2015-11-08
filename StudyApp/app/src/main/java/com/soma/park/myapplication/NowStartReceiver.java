package com.soma.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;

import java.util.Calendar;

/**
 * Created by PARK on 15. 11. 8..
 */

public class NowStartReceiver extends BroadcastReceiver {
    private static final String TAG = "NowStartReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        editor = pref.edit();
        editor.putString("currentTitle", "바로 잠금");
        String currentStart = String.valueOf(hour + ":" + min);
        String currentEnd = String.valueOf((hour + pref.getInt("nowlockhour", 0)) + ":" + (min + pref.getInt("nowlockmin", 0)));
        editor.putString("currentStart", currentStart);
        editor.putString("currentEnd", currentEnd);
        editor.commit();

        referenceMonitor.setStudymode();
        Intent intent1 = new Intent(context, ScreenService.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent1);

        Toast.makeText(context, "한 시간의 공부 시간이 시작되었습니다 화이팅!", Toast.LENGTH_LONG).show();
    }
}