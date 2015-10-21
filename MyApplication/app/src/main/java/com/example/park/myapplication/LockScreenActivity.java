package com.example.park.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class LockScreenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 보통은 setContentView 를 사용해서, layout 을 보여줍니다만...
        // 우리는 서비스에서 뷰를 보여주려고 하니, 이 부분을 과감히 주석처리 합니다.

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //FLAG_SHOW_WHEN_LOCKED: 기본 잠금화면 보다 위에 activity를 띄워라
        //FLAG_DISMISS_KEYGUARD: 기본 잠금화면을 없애라 -> KeyguardManager와 KeyguardLock 사용할 것
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
        // service intent 를 만들고, startService 메소드를 사용합니다.
        // 이 메소드를 통해서 우리가 만든 서비스가 동작하게 됩니다.

        Intent intent = new Intent(this, ScreenService.class);
//        if(reservstate) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//        }
        startService(intent);
    }

//    protected void onUserLeaveHint() {
//        finish();
//        Intent intent = new Intent(this, ScreenService.class);
//        stopService(intent);
//        super.onUserLeaveHint();
//    }
//    public void showApps(View v){
//        Intent i = new Intent(this, AppsListActivity.class);
//        startActivity(i);
//    }
//
//    public void nomalModeLauncher(View v){
//        finish();
//    }
//
    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }

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
