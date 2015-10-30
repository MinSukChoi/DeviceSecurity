package com.example.park.myapplication;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

/**
 * Created by PARK on 15. 10. 1..
 */
public class ScreenReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenReceiver";
    private KeyguardManager km = null;
    private KeyguardManager.KeyguardLock keyLock = null;
    private TelephonyManager telephonyManager = null;
    private ScreenService mService = null;
    private boolean isPhoneIdle = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        String IntentAction = intent.getAction();
        Log.i(TAG,"Intent Action == "+IntentAction);

        mService = new ScreenService();

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) |
                intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //화면이 꺼졌을 때(따로 서비스 구현해서 BroadcastReceiver를 등록해줘야 함)

            if (isPhoneIdle){
                Intent intent11 = new Intent(context, LockScreenActivity.class);
                intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //activity에서 startActivity하는게 아니기 때문에 넣어줘야 함
                context.startActivity(intent11);
            }
        }

        if (km == null) {
            km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }

        if (keyLock == null) {
            keyLock = km.newKeyguardLock("IN");
        }

        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void reenableKeyguard() {
        keyLock.reenableKeyguard(); //기본 잠금화면 나타내기
    }

    public void disableKeyguard() {
        keyLock.disableKeyguard();  //기본 잠금화면 없애기
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
                    mService.view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_RINGING");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    isPhoneIdle = false;
                    mService.view.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    break;
            }
        }
    };
}
