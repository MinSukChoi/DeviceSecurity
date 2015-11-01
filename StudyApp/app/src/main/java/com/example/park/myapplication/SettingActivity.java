package com.example.park.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.example.park.myapplication.Activities.PasswordActivity;
import com.example.park.myapplication.Observer.AppList;

/**
 * Created by PARK on 15. 10. 20..
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private static final String TAG = "Setting";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.setting_preference);
        //getActionBar().setTitle("설정");

        Preference pChangePassword = (Preference)findPreference("keychangepassword");
        Preference pFindPassword = (Preference)findPreference("keyfindpassword");
        Preference pLockReserv = (Preference)findPreference("keylock");
        Preference pEmergencyMode = (Preference)findPreference("keyemergency");
        Preference pBreakMode = (Preference)findPreference("keybreak");
        Preference pAppList = (Preference)findPreference("keyapplist");
        SwitchPreference pBrowser = (SwitchPreference)findPreference("keyBrowser");
        Preference pHelp = (Preference)findPreference("keyhelp");
        Preference pContact = (Preference)findPreference("keycontact");

        pChangePassword.setOnPreferenceClickListener(this);
        pFindPassword.setOnPreferenceClickListener(this);
        pLockReserv.setOnPreferenceClickListener(this);
        pEmergencyMode.setOnPreferenceClickListener(this);
        pBreakMode.setOnPreferenceClickListener(this);
        pAppList.setOnPreferenceClickListener(this);
        //pBrowser

        pHelp.setOnPreferenceClickListener(this);
        pContact.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if(preference.getKey().equals("keychangepassword")) {
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra("state", 0);
            startActivityForResult(intent, 0);
        } else if(preference.getKey().equals("keyfindpassword")) {


        } else if(preference.getKey().equals("keylock")) {
            Intent intent = new Intent(this, ReservActivity.class);
            startActivity(intent);
        } else if(preference.getKey().equals("keyemergency")) {
            Intent intent = new Intent(this, SettingAlertActivity.class);
            startActivity(intent);
        } else if(preference.getKey().equals("keybreak")) {
            if(pref.getInt("alert", 1) == 1) {
                Intent intent = new Intent(this, SettingBreakActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
            }
        } else if(preference.getKey().equals("keyapplist")) {
            Intent intent = new Intent(this, AppList.class);
            startActivityForResult(intent, 0);

        } else if(preference.getKey().equals("keyhelp")) {


        } else if(preference.getKey().equals("keycontact")) {

        }

        return false;
    }
}