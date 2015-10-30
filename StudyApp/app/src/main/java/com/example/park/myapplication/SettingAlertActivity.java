package com.example.park.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by PARK on 15. 10. 28..
 */
public class SettingAlertActivity extends Activity {
    private static final String TAG = "Setting_Alert";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_alert);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button okButton = (Button) findViewById(R.id.alert_ok_btn);
        final NumberPicker numNumberPicker = (NumberPicker) findViewById(R.id.alert_number_numberPicker);
        final NumberPicker timeNumberPicker = (NumberPicker) findViewById(R.id.alert_time_numberPicker);

        numNumberPicker.setMaxValue(3);
        numNumberPicker.setMinValue(1);
        timeNumberPicker.setMaxValue(15);
        timeNumberPicker.setMinValue(1);

        int number = pref.getInt("alertNum", 1);
        int time = pref.getInt("alertTime", 1);
        numNumberPicker.setValue(number);
        timeNumberPicker.setValue(time);

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = numNumberPicker.getValue();
                int time = timeNumberPicker.getValue();

                Toast.makeText(SettingAlertActivity.this, "사용 횟수: "+String.valueOf(number), Toast.LENGTH_SHORT).show();
                Toast.makeText(SettingAlertActivity.this, "사용 시간: "+String.valueOf(time), Toast.LENGTH_SHORT).show();

                editor.putInt("alertInitialNum", number);
                editor.putInt("alertInitialTime", time);
                editor.putInt("alertNum", number);
                editor.putInt("alertTime", time);
                editor.commit();
                finish();
            }
        });
    }
}
