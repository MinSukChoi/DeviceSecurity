package com.soma.park.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;

/**
 * Created by PARK on 15. 10. 22..
 */
public class AlertAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertAlarmReceiver";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        // 휴식모드면 무시
        if(referenceMonitor.getSTATE() == referenceMonitor.STUDYMODE |
                referenceMonitor.getSTATE() == referenceMonitor.ALERTMODE) {
            referenceMonitor.setStudymode();
            Toast.makeText(context, "긴급 모드가 종료되었습니다", Toast.LENGTH_LONG).show();

            Intent i = new Intent(context, ScreenService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
            AlertModePreventDelete alertModePreventDelete = AlertModePreventDelete.getInstance();
            alertModePreventDelete.setOffPreventMode();
        }
    }
}
