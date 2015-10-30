package com.example.park.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class LockScreenActivity extends Activity {
    private static final String TAG = "LockScreen";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ScreenService mScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, Splash.class));

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        mScreenService = new ScreenService();

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

        if(pref.getInt("Agree", 0) == 1) {
            setContentView(R.layout.activity_main);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //FLAG_SHOW_WHEN_LOCKED: 기본 잠금화면 보다 위에 activity를 띄워라
        //FLAG_DISMISS_KEYGUARD: 기본 잠금화면을 없애라 -> KeyguardManager와 KeyguardLock 사용할 것
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
        // service intent 를 만들고, startService 메소드를 사용합니다.
        // 이 메소드를 통해서 우리가 만든 서비스가 동작하게 됩니다.

        if(mScreenService.reservState) {
            Intent intent = new Intent(LockScreenActivity.this, ScreenService.class);
            startService(intent);
        }

        if(pref.getInt("Agree", 0) == 1) {
            setContentView(R.layout.activity_lockscreen);

            Button settingButton = (Button) findViewById(R.id.setting_button);
            settingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pref.getBoolean("alarmstate", false)) {
                        Toast.makeText(LockScreenActivity.this, "예약 시간에는 설정할 수 없습니다.", Toast.LENGTH_LONG);
                    } else {
                        Intent intent = new Intent(LockScreenActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }
                }
            });

            Button lockButton = (Button) findViewById(R.id.lock_btn);
            lockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LockScreenActivity.this, ScreenService.class);
                    startService(intent);
                }
            });
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
                        editor = pref.edit();
                        editor.putInt("Agree", 1);
                        editor.commit();
                    }
                }
                break;
        }
    }

//    @Override
//    public void onBackPressed() {
//        // Don't allow back to dismiss.
//        return;
//    }

//    protected void onUserLeaveHint() {
//        finish();
//        Intent intent = new Intent(this, ScreenService.class);
//        stopService(intent);
//        super.onUserLeaveHint();
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_HOME) {
//            return false;
//        }
//        return true;
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mHomeKeyLocker.unlock();
//        mHomeKeyLocker = null;
//    }

//    protected void onWindowVisibilityChanged (int visibility) {
//        mLauncher.onWindowVisibilityChanged(visibility);
//    }

//    public void onAttachedToWindow() {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onAttachedToWindow();
//    } // 옛날 버전에서만 가능. home key disable 어떻게 ?

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        super.onWindowFocusChanged(hasFocus);
//    }

}
