package com.soma.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlertAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Alert_Receiver";
    ScreenService mService = new ScreenService();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt("alertstate", 0); // 긴급모드 1, 아니면 0
        editor.commit();

        // 긴급모드 끝날 때 휴식모드가 실행되면 휴식모드 무시되고 락스크린 뜸
        // 긴급모드 끝날 때 공부모드면 종료. 휴식모드면 무시.
        if(pref.getBoolean("alarmstate", false)) {   // 예약시간이고 공부모드면
            if(pref.getInt("state", 1) == 1) {
                mService.reservState = true;
                referenceMonitor.setStudymode();
                Toast.makeText(context, "긴급 모드가 종료되었습니다", Toast.LENGTH_LONG).show();

                Intent i = new Intent(context, ScreenService.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }
        } else {
            referenceMonitor.setNormalmode();
            Toast.makeText(context, "긴급 모드가 종료되었습니다", Toast.LENGTH_LONG).show();

            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }
    }
}
