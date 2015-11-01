package com.example.park.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by PARK on 15. 10. 21..
 */
public class AlertModeActivity extends Activity {
    private static final String TAG = "Alert_Dialog";
    private AlarmManager alarmManager;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("긴급 모드 대화 상자")
                .setMessage("긴급 모드를 사용하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (pref.getInt("alertNum", 1) == 0){
                            Toast.makeText(getApplicationContext(), "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
                            editor = pref.edit();
                            editor.putInt("alert", 0);
                            editor.commit();
                            dialog.cancel();
                            Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                            startService(intent);
                        } else{
                            Toast.makeText(getApplicationContext(), pref.getInt("alertTime", 1)+"분 동안 긴급모드를 실행합니다", Toast.LENGTH_LONG).show();
                            onRegist();
                        }
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                        startService(intent);
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    private void onRegist() {
        ScreenService mService = new ScreenService();
        mService.reservState = false;
        mService.view.setVisibility(View.INVISIBLE);

        editor = pref.edit();
        editor.putInt("alertNum", pref.getInt("alertNum", 1) - 1);
        editor.commit();

        int time = pref.getInt("alertTime", 1);
        Log.d(TAG, String.valueOf(pref.getInt("alertNum", 1)));
        Log.d(TAG, String.valueOf(pref.getInt("alertTime", 1)));

        Intent intentReceiver = new Intent(this, AlertAlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(time), pIntent);

        Intent intent1 = new Intent(this, LockScreenActivity.class);
        startActivity(intent1);
    }
}