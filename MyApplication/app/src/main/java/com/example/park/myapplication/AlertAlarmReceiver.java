package com.example.park.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlertAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Alert_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ScreenService mService = new ScreenService();
        mService.reservState = true;
        Toast.makeText(context, "긴급 모드가 종료되었습니다", Toast.LENGTH_LONG).show();

        Intent i = new Intent(context, ScreenService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}
