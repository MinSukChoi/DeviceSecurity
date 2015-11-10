package com.soma.park.myapplication.Services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soma.park.myapplication.Activities.AppsListActivity;
import com.soma.park.myapplication.Activities.LockScreenActivity;
import com.soma.park.myapplication.Activities.PasswordActivity;
import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.R;
import com.soma.park.myapplication.Receivers.BootReceiver;
import com.soma.park.myapplication.Receivers.DeviceEventReceiver;
import com.soma.park.myapplication.Activities.StudyBrowser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by PARK on 15. 10. 1..
 */
public class ScreenService extends Service {
    private static final String TAG = "Service";
    private BootReceiver mReceiver1 = null;
    private DeviceEventReceiver mReceiver2 = null;
    public static View view;
    private static WindowManager mWindowManager;
    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler= new Handler();
    private KeyguardManager km = null;
    private KeyguardManager.KeyguardLock keyLock = null;
    private TelephonyManager telephonyManager = null;
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private SeekBar seekBar;
    private TextView textView_leftTime;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

        mReceiver1 = new BootReceiver();
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(mReceiver1, filter1);
        mReceiver2 = new DeviceEventReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mReceiver2, filter2);

        // mHandler = new Handler();
        mTask = new TimerTask() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String availList = pref.getString("appList", "");

                if(!(isServiceRunningCheck().contains("myapplication") |
                        isServiceRunningCheck().contains("call") |
                        isServiceRunningCheck().contains("contact") |
                        isServiceRunningCheck().contains("mms") |
                        isServiceRunningCheck().contains("browser") |
                        isServiceRunningCheck().contains("camera") |
                        isServiceRunningCheck().contains("gallery") |
                        isServiceRunningCheck().contains("memo") |
                        isServiceRunningCheck().contains("note") |
                        isServiceRunningCheck().contains("calculator") |
                        isServiceRunningCheck().contains("clock") |
                        isServiceRunningCheck().contains("calendar") |
                        availList.contains(isServiceRunningCheck()))) {
                            Log.d(TAG, isServiceRunningCheck());
                            Intent intent1 = new Intent(getApplicationContext(), ScreenService.class);
                            startService(intent1);
                            Intent intent2 = new Intent(getApplicationContext(), LockScreenActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent2);
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
                    Log.d(TAG, "CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_RINGING");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    break;
            }
        }
    };

    // 앱 리스트
    public void showApps(View v) {
        view.setVisibility(View.INVISIBLE); // View를 감춘다. (공간차지 O)
        Intent intent = new Intent(getApplicationContext(), AppsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //activity에서 startActivity하는게 아니기 때문에 넣어줘야 함
        startActivity(intent);
        Log.v("TAG", "After");
    }

    // 스터디 모드 종료(나가기 버튼)
//    public void nomalModeLauncher(View v) {
//        if(reservState == false) {
//            stopSelf();
//            Intent newintent = new Intent(getApplicationContext(), PasswordActivity.class);
//            newintent.putExtra("state", 2);
//            newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(newintent);
//        } else {
//            Toast.makeText(this, "공부 시간입니다", Toast.LENGTH_SHORT).show();
//        }
//    }

    // 긴급 모드
    public void goAlertMode(View v) {
        if(pref.getInt("alert", 1) == 0) {
            Toast.makeText(v.getContext(), "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
        } else {
            referenceMonitor.setSTATE(referenceMonitor.TEMPMODE);
            stopSelf();
            Intent newintent = new Intent(getApplicationContext(), PasswordActivity.class);
            newintent.putExtra("state", 3);
            newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newintent);
        }
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
                if(pref.getBoolean("studybrowser",true)) {
                    view.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(getApplicationContext(),StudyBrowser.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    for (Object object : appNames) {
                        if (object.toString().contains("browser") && object.toString().contains("android")) {
                            startActivityForPackageName(object.toString());
                            break;
                        }
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

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Calendar calendar = Calendar.getInstance();
            String currentEnd = pref.getString("currentEnd", "");
            String currentStart = pref.getString("currentStart", "");
            String[] endTimes = currentEnd.split(":");
            String[] startTimes = currentStart.split(":");
            int endMin = (60*Integer.parseInt(endTimes[0])+Integer.parseInt(endTimes[1]));
            int startMin = (60*Integer.parseInt(startTimes[0])+Integer.parseInt(startTimes[1]));
            int nowMin = 60*calendar.get(Calendar.HOUR_OF_DAY)+calendar.get(Calendar.MINUTE);
            int whole = (endMin+1440-startMin)%1440;
            int passed = (nowMin+1440-startMin)%1440;
            seekBar.setProgress(100*passed/whole);
            textView_leftTime.setText((whole-passed)+"분");
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 2000);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        view.setVisibility(View.VISIBLE);

        String title = pref.getString("currentTitle", "");
        String currentStart = pref.getString("currentStart", "");
        String currentEnd = pref.getString("currentEnd", "");
        int alertCount = pref.getInt("alertNum", 3);
        int alertTime = pref.getInt("alertTime", 15);
        int studyTime = pref.getInt("studyTime", 50);
        int breakTime = pref.getInt("breakTime", 10);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        textView_leftTime = (TextView) view.findViewById(R.id.textView_leftTime);

        updateProgressBar();

        /*
        ((TextView)view.findViewById(R.id.current_title)).setText("제목 : " + title);
        ((TextView)view.findViewById(R.id.current_time)).setText("시간 : " + currentStart + " ~ " + currentEnd);
        ((TextView)view.findViewById(R.id.current_alert)).setText("긴급모드 횟수 : " + alertCount + "회,  긴급모드 시간 : " + alertTime + "분");
        if(pref.getBoolean("alarmstate", false)) {
            ((TextView)view.findViewById(R.id.current_break)).setText("공부 시간 : " + studyTime + "분,  휴식 시간 : " + breakTime + "분");
        }
        */

        if(intent != null){
            if(mReceiver1 == null){
                mReceiver1 = new BootReceiver();
                IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
                registerReceiver(mReceiver1, filter);
            }
            if(mReceiver2 == null){
                mReceiver2 = new DeviceEventReceiver();
                IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
                registerReceiver(mReceiver2, filter);
            }
        }

        return START_NOT_STICKY;
        // START_STICKY
        // START_REDELIVER_INTENT
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
        mTimer.cancel();
        mHandler.removeCallbacks(mUpdateTimeTask);

        if (mReceiver1 != null) {
            unregisterReceiver(mReceiver1);
        }
        if (mReceiver2 != null) {
            unregisterReceiver(mReceiver2);
        }

        if(mWindowManager != null) {
            if(view != null) {
                mWindowManager.removeView(view);
                mWindowManager = null;
            }
        }

        super.onDestroy();
    }
}