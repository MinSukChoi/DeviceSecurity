package com.soma.park.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

/**
 * Created by PARK on 15. 11. 4..
 */
public class SettingBrowserActivity extends Activity {
    RadioButton btn1;
    RadioButton btn2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_browser);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button okButton = (Button) findViewById(R.id.browser_ok_btn);
        btn1 = (RadioButton) findViewById(R.id.radiobtn1);
        btn2 = (RadioButton) findViewById(R.id.radiobtn2);

        btn1.setChecked(pref.getBoolean("radiobtn1", false));
        btn2.setChecked(pref.getBoolean("radiobtn2", false));

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check1 = btn1.isChecked();
                boolean check2 = btn2.isChecked();
                if(check1) {
                    editor.putBoolean("studybrowser", true);
                } else {
                    editor.putBoolean("studybrowser", false);
                }
                editor.putBoolean("radiobtn1", check1);
                editor.putBoolean("radiobtn2", check2);
                editor.commit();
                finish();
            }
        });
    }
}
