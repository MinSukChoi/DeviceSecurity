package com.soma.park.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

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

        numNumberPicker.setMaxValue(pref.getInt("alertNum", 3));
        numNumberPicker.setMinValue(1);
        timeNumberPicker.setMaxValue(15);
        timeNumberPicker.setMinValue(1);

        editor.putInt("alertInitialNum", 3);
        editor.putInt("alertInitialTime", 15);
        editor.commit();

        int number = pref.getInt("alertNum", 3);
        int time = pref.getInt("alertTime", 15);
        numNumberPicker.setValue(number);
        timeNumberPicker.setValue(time);

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = numNumberPicker.getValue();
                int time = timeNumberPicker.getValue();
                editor.putInt("alertNum", number);
                editor.putInt("alertTime", time);
                editor.commit();
                finish();
            }
        });
    }
}
