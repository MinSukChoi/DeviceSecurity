package com.soma.park.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.soma.park.myapplication.DeviceAdmin.PreventDeleteDeviceAdminReceiver;
import com.soma.park.myapplication.DeviceAdmin.SetDeviceAdmin;
import com.soma.park.myapplication.Services.ScreenService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by prena on 15. 11. 9..
 */
public class AlertModePreventDelete{
    private static AlertModePreventDelete alertModePreventDelete = new AlertModePreventDelete();
    public static AlertModePreventDelete getInstance(){return alertModePreventDelete;}

    private TimerTask mTask;
    private Timer mTimer;
    public void setOnPreventMode(final Context c){
        mTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("Test", isServiceRunningCheck(c));
                if(isServiceRunningCheck(c).contains("packageinstaller")) {
                    Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
                    intentToResolve.addCategory(Intent.CATEGORY_HOME);
                    intentToResolve.setPackage("com.android.launcher");
                    ResolveInfo ri = c.getPackageManager().resolveActivity(intentToResolve, 0);
                    if (ri != null)
                    {
                        Intent intent = new Intent(intentToResolve);
                        intent.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        c.startActivity(intent);
                    }
                }
                DevicePolicyManager deviceMgr = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);;
                ComponentName comp = new ComponentName(c, PreventDeleteDeviceAdminReceiver.class);
                if (!deviceMgr.isAdminActive(comp)) {
                    if(!isServiceRunningCheck(c).contains("settings")){
                        Log.d("Test", "error");
                        Intent intent = new Intent(c, SetDeviceAdmin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
                    }
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 500, 500);
    }

    public void setOffPreventMode(){
        mTimer.cancel();
    }

    public String isServiceRunningCheck(Context c) {
        ActivityManager am = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> Info = am.getRunningTasks(1);
        ComponentName topActivity = Info.get(0).topActivity;
        String topactivityname = topActivity.getPackageName();
        return topactivityname;
    }
}
