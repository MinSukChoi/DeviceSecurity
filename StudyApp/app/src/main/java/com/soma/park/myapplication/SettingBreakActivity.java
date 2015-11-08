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
public class SettingBreakActivity extends Activity {
    private static final String TAG = "Setting_Break";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_break);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button okButton = (Button) findViewById(R.id.break_ok_btn);
        final NumberPicker studyNumberPicker = (NumberPicker) findViewById(R.id.break_study_numberPicker);
        final NumberPicker breakNumberPicker = (NumberPicker) findViewById(R.id.break_numberPicker);

        studyNumberPicker.setMaxValue(60);
        studyNumberPicker.setMinValue(1);
        breakNumberPicker.setMaxValue(15);
        breakNumberPicker.setMinValue(1);

        int studyTime = pref.getInt("studyTime", 50);
        int breakTime = pref.getInt("breakTime", 10);
        studyNumberPicker.setValue(studyTime);
        breakNumberPicker.setValue(breakTime);

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int studyTime = studyNumberPicker.getValue();
                int breakTime = breakNumberPicker.getValue();

//                Toast.makeText(SettingBreakActivity.this, "공부 시간: "+String.valueOf(studyTime), Toast.LENGTH_SHORT).show();
//                Toast.makeText(SettingBreakActivity.this, "휴식 시간: "+String.valueOf(breakTime), Toast.LENGTH_SHORT).show();

                editor.putInt("studyTime", studyTime);
                editor.putInt("breakTime", breakTime);
                editor.commit();
                finish();
            }
        });
    }
}
