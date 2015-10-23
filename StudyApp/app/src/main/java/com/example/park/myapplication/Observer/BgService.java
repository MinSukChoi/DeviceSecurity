package com.example.park.myapplication.Observer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prena on 15. 10. 15..
 */
public class BgService extends Service {
    private TimerTask mTask;
    private Timer mTimer;
    private Handler mHandler;

    Context c;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "서비스 onCreate", Toast.LENGTH_SHORT).show();
        c = this;
        mHandler = new Handler();

        mTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new ToastRunnable("토스트 메세지!"));

                ActivityManager activity_manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> app_list = activity_manager.getRunningAppProcesses();
                for(int i=0; i<app_list.size(); i++)	{
                    /*
                    if("com.getpackagelist.app".equals(app_list.get(i).processName) == false)	{
                        android.os.Process.sendSignal(app_list.get(i).pid, android.os.Process.SIGNAL_KILL);
                        activity_manager.killBackgroundProcesses(app_list.get(i).processName);
                    }
                    */
                    Log.d("ObserverLog", app_list.get(i).processName + " / " + app_list.size());

                }

                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info;
                info = activityManager.getRunningTasks(7);
                ComponentName topactivityname = info.get(0).topActivity;
                Log.d("ObserverLog", topactivityname.getPackageName());

                for(int i=0;i<info.size();i++){
                    Log.d("ObserverLog", "^^" + info.get(i).topActivity.getPackageName());
                }

                for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
                    ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();
                    Log.d("ObserverLog", runningTaskInfo.topActivity.getClassName());
                }


            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 3000, 3000);

    }

    @Override
    public void onDestroy(){
        mTimer.cancel();
        super.onDestroy();
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }
}
