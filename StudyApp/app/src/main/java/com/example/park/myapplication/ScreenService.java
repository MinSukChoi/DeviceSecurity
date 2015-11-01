package com.example.park.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.park.myapplication.Activities.PasswordActivity;
import com.example.park.myapplication.Elements.ReferenceMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PARK on 15. 10. 1..
 */
public class ScreenService extends Service {
    private static final String TAG = "Service";
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private BootReceiver mReceiver = null;
    public static View view;
    private static WindowManager mWindowManager;
    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler;
    private KeyguardManager km = null;
    private KeyguardManager.KeyguardLock keyLock = null;
    private TelephonyManager telephonyManager = null;
    private boolean isPhoneIdle = true;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LockScreenActivity lockScreenActivity;
    public static boolean reservState = false;

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

        lockScreenActivity = new LockScreenActivity();
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = (View) inflater.inflate(R.layout.lockscreen, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,  //항상 최 상위. 터치 이벤트 받을 수 있음
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                PixelFormat.TRANSLUCENT);   //투명
        try {
            // 이제 inflate 된 View 객체와 설정한 parameter 값들을, addView 메소드를 이용해서 window 에 배정합니다.
            // 이 부분이 try-catch 구문이 없을 때, 킷캣 이상의 폰에서 강제 종료되는 경우가 있습니다.
            // 제 폰이 롤리팝이 올라간 넥서스 5인데, try-catch 구문이 없으면 앱이 강제 종료됩니다;;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(view, params);   //윈도우에 뷰 넣기. permission 필요
        } catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Exception message : " + e.getLocalizedMessage());
        }

        if(reservState == true){
            mReceiver = new BootReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
            registerReceiver(mReceiver, filter);
        }

        if (km == null) {
            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        }

        if (keyLock == null) {
            keyLock = km.newKeyguardLock("IN");
        }

        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        // mHandler = new Handler();
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
//                    if(reservState) {
                        Log.d(TAG, isServiceRunningCheck());
                        Intent intent1 = new Intent(getApplicationContext(), ScreenService.class);
                        startService(intent1);
                        Intent intent2 = new Intent(getApplicationContext(), LockScreenActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
//                    }
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 1000, 2000);
    }

    private PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    isPhoneIdle = true; //정상상태
                    Log.d(TAG, "CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    isPhoneIdle = false;
                    view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_RINGING");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isPhoneIdle = false;
                    view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    break;
            }
        }
    };

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
            //view.setVisibility(View.INVISIBLE);
            stopSelf();
            Intent newintent = new Intent(getApplicationContext(), PasswordActivity.class);
            newintent.putExtra("state", 2);
            newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newintent);
            Log.v("TAG", "After");
        } else {
            Toast.makeText(this, "예약 잠금 시간입니다", Toast.LENGTH_SHORT).show();
        }
    }

    // 긴급 모드
    public void goAlertMode(View v) {
//        if(reservState == true) {
        if(pref.getInt("alert", 1) == 0) {
            Toast.makeText(v.getContext(), "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
        } else {
            //view.setVisibility(View.INVISIBLE);
            referenceMonitor.setAlertmode();
            stopSelf();
            Intent popupIntent = new Intent(getApplicationContext(), AlertModeActivity.class);
            PendingIntent pie = PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);
            try {
                pie.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
//        } else {
//            Toast.makeText(v.getContext(), "지금은 긴급 모드를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
//        }
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
                    if(object.toString().contains("contact") && object.toString().contains("android")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.msg_app:
                for(Object object : appNames) {
                    if(object.toString().contains("mms") && object.toString().contains("android")) {
                        startActivityForPackageName(object.toString());
                        break;
                    }
                }
                break;
            case R.id.browser_app:
                for(Object object : appNames) {
                    if(object.toString().contains("browser") && object.toString().contains("android")) {
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
                    if(object.toString().contains("gallery") && object.toString().contains("android")) {
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
                    if(object.toString().contains("calculator") && object.toString().contains("android")) {
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
                    if(object.toString().contains("calendar") && object.toString().contains("android")) {
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
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent, flags, startId);
        view.setVisibility(View.VISIBLE);

        String title = pref.getString("currentTitle", "");
        String currentStart = pref.getString("currentStart", "");
        String currentEnd = pref.getString("currentEnd", "");
        int alertCount = pref.getInt("alertNum", 1);
        int alertTime = pref.getInt("alertTime", 1);
        int studyTime = pref.getInt("studyTime", 1);
        int breakTime = pref.getInt("breakTime", 1);

        ((TextView)view.findViewById(R.id.current_title)).setText("제목 : " + title);
        ((TextView)view.findViewById(R.id.current_time)).setText("예약 잠금 시간 : " + currentStart + " ~ " + currentEnd);
        ((TextView)view.findViewById(R.id.current_alert)).setText("남은 긴급모드 횟수 : " + alertCount + "회");
        ((TextView)view.findViewById(R.id.current_break)).setText("공부 시간 : " + studyTime + "분 & 휴식 시간 : " + breakTime + "분");

        if(reservState) {
            if(intent != null){
                if(mReceiver == null){
                    mReceiver = new BootReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
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

        if(reservState) {
            return START_REDELIVER_INTENT;
        } else {
            return START_NOT_STICKY;
        }
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
        return topactivityname;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        mTimer.cancel();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        if(mWindowManager != null) {
            if(view != null) {
                mWindowManager.removeView(view);
                mWindowManager = null;
            }
        }
//      notiManager.cancel(11);
//      mReceiver.reenableKeyguard();
        super.onDestroy();
    }
}