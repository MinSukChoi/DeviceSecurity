package com.soma.park.myapplication.DeviceAdmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.soma.park.myapplication.R;

/**
 * Created by PreNa on 2015-11-10.
 */
public class SetDeviceAdmin extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ComponentName comp = new ComponentName(this, PreventDeleteDeviceAdminReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, comp);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.devicePolicyManagerMessage));

        startActivityForResult(intent, 0);

        finish();
    }

}
