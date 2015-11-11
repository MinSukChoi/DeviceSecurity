package com.soma.park.myapplication.Activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.R;
import com.soma.park.myapplication.Receivers.DailyReceiver;
import com.soma.park.myapplication.Receivers.NowStartReceiver;
import com.soma.park.myapplication.Receivers.NowStopReceiver;
import com.soma.park.myapplication.Services.ScreenService;

import java.util.Calendar;

public class LockScreenActivity extends Activity {
    private static final String TAG = "LockScreen";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //FLAG_SHOW_WHEN_LOCKED: 기본 잠금화면 보다 위에 activity를 띄워라
        //FLAG_DISMISS_KEYGUARD: 기본 잠금화면을 없애라 -> KeyguardManager와 KeyguardLock 사용할 것

        startActivity(new Intent(this, Splash.class));

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

//        alarmManager.cancel(PendingIntent.getBroadcast(LockScreenActivity.this, 0,
//                new Intent(LockScreenActivity.this, DailyReceiver.class), 0));
//        alarmManager.cancel(PendingIntent.getBroadcast(LockScreenActivity.this, 0,
//                new Intent(LockScreenActivity.this, DailyReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent = new Intent(LockScreenActivity.this, DailyReceiver.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        Log.d(TAG, String.valueOf(calendar.getTime()));
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(LockScreenActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmSender);

        if(pref.getInt("First", 0) != 1){
            Handler hd = new Handler();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LockScreenActivity.this, AccessTerms.class);
                    startActivityForResult(intent, 1);
                }
            }, 3000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if(referenceMonitor.getSTATE()!=referenceMonitor.TEMPMODE && referenceMonitor.getSTATE()!=referenceMonitor.ALERTMODE) {
//            Intent intent = new Intent(LockScreenActivity.this, ScreenService.class);
//            startService(intent);
//        }
        setContentView(R.layout.activity_lockscreen);

        Button settingButton = (Button) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newintent = new Intent(LockScreenActivity.this, PasswordActivity.class);
                newintent.putExtra("state", 2);
                startActivityForResult(newintent, 2);
            }
        });

        // 한시간 바로 잠금
        TextView lockText = (TextView) findViewById(R.id.lock_textview);
        lockText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(referenceMonitor.getSTATE() == referenceMonitor.BREAKTIMEMODE) {
                    Toast.makeText(v.getContext(), "지금은 예약 잠금 시간입니다", Toast.LENGTH_SHORT).show();
                } else if(referenceMonitor.getSTATE() == referenceMonitor.ALERTMODE) {
                    Toast.makeText(v.getContext(), "지금은 긴급모드 사용 중입니다", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LockScreenActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor = pref.edit();
                            editor.putInt("nowlockhour", 1);
                            editor.putInt("nowlockmin", 0);
                            editor.commit();
                            onRegist(1, 0); // 바로잠금 2분
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("한 시간 잠금모드를 실행합니다");
                    alert.show();
                }
            }
        });
    }

    private void onRegist(int hour, int min) {
        Intent intent1 = new Intent(LockScreenActivity.this, NowStartReceiver.class);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.SECOND, 0);
        Log.d(TAG, String.valueOf(calendar1.getTime()));
        PendingIntent pIntent1 = PendingIntent.getBroadcast(LockScreenActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {      //api 19 이상
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pIntent1);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pIntent1);
        }

        Intent intent2 = new Intent(LockScreenActivity.this, NowStopReceiver.class);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, (calendar2.get(Calendar.HOUR_OF_DAY)+hour));
        calendar2.set(Calendar.MINUTE, (calendar2.get(Calendar.MINUTE)+min));
        calendar2.set(Calendar.SECOND, 0);
        Log.d(TAG, String.valueOf(calendar2.getTime()));
        PendingIntent pIntent2 = PendingIntent.getBroadcast(LockScreenActivity.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pIntent2);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pIntent2);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult");
        //super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    if(intent.getExtras().getInt("agree") == 0) {
                        finish();
                    }
                    if(intent.getExtras().getInt("agree") == 1) {
                        Intent newintent = new Intent(LockScreenActivity.this, PasswordActivity.class);
                        newintent.putExtra("state",0);
                        startActivity(newintent);
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK) {
                    if(intent.getExtras().getInt("validation") == 0) {
                        Toast.makeText(this,"암호가 올바르지 않습니다",Toast.LENGTH_SHORT).show();
                    }
                    if(intent.getExtras().getInt("validation") == 1) {
                        Intent newintent = new Intent(LockScreenActivity.this, Setting.class);
                        startActivity(newintent);
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume " + referenceMonitor.getSTATE());
        super.onResume();
        if(referenceMonitor.getSTATE()==referenceMonitor.STUDYMODE || referenceMonitor.getSTATE()==referenceMonitor.INVALIDMODE) {
            referenceMonitor.setStudymode();
            Intent intent = new Intent(LockScreenActivity.this, ScreenService.class);
            startService(intent);
        }else if(referenceMonitor.getSTATE() == referenceMonitor.TEMPMODE) {
            Log.v(TAG, "onAlert("+pref.getInt("alert", 1)+")");
//            if(pref.getInt("alert", 1) == 0) {
//                Toast.makeText(this, "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
//            } else {
                Intent popupIntent = new Intent(this, AlertModeActivity.class);
                PendingIntent pie = PendingIntent.getActivity(this, 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
                try {
                    pie.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
//            }
        }
        Log.v(TAG, "onResume end"+referenceMonitor.getSTATE());
    }
}
