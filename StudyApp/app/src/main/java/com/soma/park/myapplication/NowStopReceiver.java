package com.soma.park.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;

/**
 * Created by PARK on 15. 11. 8..
 */

public class NowStopReceiver extends BroadcastReceiver {
    private static final String TAG = "NowStopReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    ScreenService mService;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);

        if(pref.getBoolean("alarmstate", false) == false) {
            if(pref.getBoolean("nowlock", false)) {
                editor = pref.edit();
                editor.putBoolean("nowlock", false);
                editor.commit();

                referenceMonitor.setNormalmode();
                mService = new ScreenService();
                mService.reservState = false;
                Intent intent1 = new Intent(context, ScreenService.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.stopService(intent1);
//            Intent intent2 = new Intent(context, LockScreenActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);

                Toast.makeText(context, "한 시간의 공부 시간이 종료되었습니다^^", Toast.LENGTH_LONG).show();
            }
        }
    }
}