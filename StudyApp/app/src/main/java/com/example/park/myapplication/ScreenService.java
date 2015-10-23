package com.example.park.myapplication;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PARK on 15. 10. 1..
 */
public class ScreenService extends Service {
    private static final String TAG = "Service";
    private ScreenReceiver mReceiver = null;
    private static WindowManager mWindowManager;
    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler;
    public static View view;
    public boolean reservState = false;

//    NotificationManager notiManager;
//    Notification noti;
//    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//    boolean isScreenOn = pm.isScreenOn();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = (View) inflater.inflate(R.layout.lockscreen, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,  //항상 최 상위. 터치 이벤트 받을 수 있음.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                PixelFormat.TRANSLUCENT);   //투명
        try {
            // 이제 inflate 된 View 객체와 설정한 parameter 값들을, addView 메소드를 이용해서 window 에 배정합니다.
            // 이 부분이 try-catch 구문이 없을 때, 킷캣 이상의 폰에서 강제 종료되는 경우가 있습니다.
            // 제 폰이 롤리팝이 올라간 넥서스 5인데, try-catch 구문이 없으면 앱이 강제 종료됩니다;;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(view, params);   //윈도우에 뷰 넣기. permission 필요.
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Exception message : " + e.getLocalizedMessage());
        }

        //setAlarm(this, 60000);

        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);

        mHandler = new Handler();
        mTask = new TimerTask() {
            @Override
            public void run() {
                if(!(isServiceRunningCheck().contains("myapplication") |
                        isServiceRunningCheck().contains("contact") |
                        isServiceRunningCheck().contains("mms") |
                        isServiceRunningCheck().contains("browser") |
                        isServiceRunningCheck().contains("camera") |
                        isServiceRunningCheck().contains("gallery") |
                        isServiceRunningCheck().contains("memo") |
                        isServiceRunningCheck().contains("note") |
                        isServiceRunningCheck().contains("calculator") |
                        isServiceRunningCheck().contains("clock") |
                        isServiceRunningCheck().contains("calendar"))) {
                    Intent intent1 = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent1);
                    Intent intent2 = new Intent(getApplicationContext(), LockScreenActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
                }
            }
        };

        mTimer = new Timer(false);
        if(reservState) {
            mTimer.schedule(mTask, 1000, 3000);
        }
    }

    // 앱 리스트
    public void showApps(View v) {
        view.setVisibility(View.INVISIBLE); // View를 감춘다. (공간차지 O)
        Intent intent = new Intent(this, AppsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //activity에서 startActivity하는게 아니기 때문에 넣어줘야 함
        startActivity(intent);
    }

    // 스터디 모드 종료
    public void nomalModeLauncher(View v) {
        if(reservState == false) {
            mTimer.cancel();
            Intent intent = new Intent(this, ScreenService.class);
            stopService(intent);
        } else {
            Toast.makeText(this, "예약 잠금 시간입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 긴급 모드
    public void goAlertMode(View v) {
        view.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, AlertModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //activity에서 startActivity하는게 아니기 때문에 넣어줘야 함
        startActivity(intent);
    }

    // 설정
    public void showSettings(View v) {
        view.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 기본 앱 실행
    public void executeApp(View v) {
        PackageManager pm = getPackageManager();
        List appNames = new ArrayList();
        CharSequence name;

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            name = ri.activityInfo.packageName;
            appNames.add(name);
        }

        switch (v.getId()) {
            case R.id.call_app:
                for(Object object : appNames) {
                    if(object.toString().contains("contact")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.msg_app:
                for(Object object : appNames) {
                    if(object.toString().contains("mms")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.browser_app:
                for(Object object : appNames) {
                    if(object.toString().contains("browser")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.camera_app:
                for(Object object : appNames) {
                    if(object.toString().contains("camera")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.gallery_app:
                for(Object object : appNames) {
                    if(object.toString().contains("gallery")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.memo_app:
                for(Object object : appNames) {
                    if(object.toString().contains("note") | object.toString().contains("memo")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.calculator_app:
                for(Object object : appNames) {
                    if(object.toString().contains("calculator")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.alarm_app:
                for(Object object : appNames) {
                    if(object.toString().contains("clock")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.calendar_app:
                for(Object object : appNames) {
                    if(object.toString().contains("calendar")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
        }
    }

    private void startActivityForPackageName(String packageName) {
        view.setVisibility(View.INVISIBLE);

        PackageManager pm = getPackageManager();
        Intent app = pm.getLaunchIntentForPackage(packageName);
        this.startActivity(app);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        view.setVisibility(View.VISIBLE);

        if(intent != null){
            if(intent.getAction() == null){
                if(mReceiver == null){
                    mReceiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(mReceiver, filter);
                }
            }
        }

        //startForeground(int id, Notification notification);   //id: Noti~의 id
        //noti~: 서비스가 foreground로 실행되는 동안 나타날 Noti~

//        notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        Notification.Builder builder = new Notification.Builder(ScreenService.this);
//
//        builder.setTicker("서비스 실행됨");
//        builder.setContentTitle("Screen Service Notification");
//        builder.setContentText("Foreground로 실행됨");
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.build();
//
//        noti = builder.getNotification();
//        notiManager.notify(11, noti);


//        Notification notification = new Notification.Builder(getApplicationContext())
//                .setContentTitle("Screen Service")
//                .setContentText("Foreground로 실행됨")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();
//
//        startForeground(1, notification);

        return START_STICKY;
        // START_STICKY와
        // START_REDELIVER_INTENT
        // START_STICKY와 마찬가지로 Service가 종료되었을 경우 시스템이 다시 Service를 재시작
        // intent 값을 그대로 유지
        // 즉, startService() 호출 시 Intent value값을 사용한 경우라면 해당 Flag를 사용해서 리턴값 설정
        // 반드시 실행되어야 하는 service에 해당됨
    }

    public String isServiceRunningCheck() {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> Info = am.getRunningTasks(1);
        ComponentName topActivity = Info.get(0).topActivity;
        String topactivityname = topActivity.getPackageName();
        Log.d(TAG, topactivityname);
        return topactivityname;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();

        if(mWindowManager != null) {
            if(view != null) {
                mWindowManager.removeView(view);
                mWindowManager = null;
            }
        }
//        notiManager.cancel(11);
//        mReceiver.reenableKeyguard();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();

    }
}