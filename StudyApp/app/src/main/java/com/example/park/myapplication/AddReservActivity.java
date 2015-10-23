package com.example.park.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 20..
 */
public class AddReservActivity extends Activity {
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    private AlarmManager alarmManager;
    private ToggleButton toggleSun, toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat;
    CheckBox mCheckBox;
    EditText mEditText;

    TimePickerDialog.OnTimeSetListener timeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar1.set(Calendar.MINUTE, minute);
        }
    };
    TimePickerDialog.OnTimeSetListener timeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar2.set(Calendar.MINUTE, minute);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reservation);

        mEditText = (EditText)findViewById(R.id.lock_edit);
        mCheckBox = (CheckBox)findViewById(R.id.lock_checkBox);

        Button btnStartTime = (Button)findViewById(R.id.lock_start);
        Button btnEndTime = (Button)findViewById(R.id.lock_end);
        Button okButton = (Button)findViewById(R.id.lock_btn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);

        btnStartTime.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddReservActivity.this,
                        timeSetListener1,
                        calendar1.get(Calendar.HOUR_OF_DAY),
                        calendar1.get(Calendar.MINUTE),
                        true).show();
            }
        });

        btnEndTime.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddReservActivity.this,
                        timeSetListener2,
                        calendar2.get(Calendar.HOUR_OF_DAY),
                        calendar2.get(Calendar.MINUTE),
                        true).show();
            }
        });

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegist(v);
                finish();
            }
        });
    }

    public void onRegist(View v)
    {
        boolean[] week = { false, toggleSun.isChecked(), toggleMon.isChecked(),
                toggleTue.isChecked(), toggleWed.isChecked(),
                toggleThu.isChecked(), toggleFri.isChecked(),
                toggleSat.isChecked() };    // sunday=1 이라서 0의 자리에는 아무 값이나 넣었음

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("weekday", week);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long oneday = 24 * 60 * 60 * 1000;// 24시간

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), oneday, pIntent);
    }

    public void onUnregist(View v)
    {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.cancel(pIntent);
    }
}
