package com.soma.park.myapplication.Activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.soma.park.myapplication.R;

import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 25..
 */
public class ReservAddActivity extends Activity {
    private static final String TAG = "Reservation_Add";
    private ToggleButton toggleSun, toggleMon, toggleTue, toggleWed, toggleThu, toggleFri, toggleSat;
    public static int i = 1;
    public boolean flag = true;
    private static int position = 0;
    private static int startHour;
    private static int startMin;
    private static int endHour;
    private static int endMin;
    Calendar calendar1 = Calendar.getInstance();
    Calendar calendar2 = Calendar.getInstance();
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    ReservActivity reservActivity;

    TimePickerDialog.OnTimeSetListener timeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMin = minute;
            calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar1.set(Calendar.MINUTE, minute);
        }
    };
    TimePickerDialog.OnTimeSetListener timeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endHour = hourOfDay;
            endMin = minute;
            calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar2.set(Calendar.MINUTE, minute);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reservation);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        final EditText reservTitle = (EditText)findViewById(R.id.lock_edit);
        final CheckBox dayOfTheWeek = (CheckBox)findViewById(R.id.lock_checkBox);
        Button btnStartTime = (Button)findViewById(R.id.lock_start_btn);
        Button btnEndTime = (Button)findViewById(R.id.lock_end_btn);
        Button okButton = (Button)findViewById(R.id.lock_ok_btn);

        toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);
        reservActivity = new ReservActivity();

        position = reservActivity.pos;
        if(pref.getString("title" + position, "").equals("")){
            Log.d("Add : pos = ", String.valueOf(position));
            flag = true;
        } else {
            Log.d("Modify : pos = ", String.valueOf(position));
            i = position;
            flag = false;
        }

        String editText = pref.getString("title" + i, "");
        Boolean checkBox = pref.getBoolean("checkweek" + i, false);
        Boolean sun = pref.getBoolean("sun" + i, false);
        Boolean mon = pref.getBoolean("mon" + i, false);
        Boolean tue = pref.getBoolean("tue" + i, false);
        Boolean wed = pref.getBoolean("wed" + i, false);
        Boolean thu = pref.getBoolean("thu" + i, false);
        Boolean fri = pref.getBoolean("fri" + i, false);
        Boolean sat = pref.getBoolean("sat" + i, false);
        int starth = pref.getInt("startHour" + i, calendar1.get(Calendar.HOUR_OF_DAY));
        int startm = pref.getInt("startMin" + i, calendar1.get(Calendar.MINUTE));
        int endh = pref.getInt("endHour" + i, calendar2.get(Calendar.HOUR_OF_DAY));
        int endm = pref.getInt("endMin" + i, calendar2.get(Calendar.MINUTE));

        reservTitle.setText(editText);
        dayOfTheWeek.setChecked(checkBox);
        toggleSun.setChecked(sun);
        toggleMon.setChecked(mon);
        toggleTue.setChecked(tue);
        toggleWed.setChecked(wed);
        toggleThu.setChecked(thu);
        toggleFri.setChecked(fri);
        toggleSat.setChecked(sat);
        calendar1.set(Calendar.HOUR_OF_DAY, starth);
        calendar1.set(Calendar.MINUTE, startm);
        calendar2.set(Calendar.HOUR_OF_DAY, endh);
        calendar2.set(Calendar.MINUTE, endm);

        // 시작 시간 ~ 종료 시간 설정
        btnStartTime.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ReservAddActivity.this,
                        timeSetListener1,
                        calendar1.get(Calendar.HOUR_OF_DAY),
                        calendar1.get(Calendar.MINUTE),
                        true).show();
            }
        });

        btnEndTime.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ReservAddActivity.this,
                        timeSetListener2,
                        calendar2.get(Calendar.HOUR_OF_DAY),
                        calendar2.get(Calendar.MINUTE),
                        true).show();
            }
        });

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editText = reservTitle.getText().toString();
                Boolean checkBox = dayOfTheWeek.isChecked();
                Boolean sun = toggleSun.isChecked();
                Boolean mon = toggleMon.isChecked();
                Boolean tue = toggleTue.isChecked();
                Boolean wed = toggleWed.isChecked();
                Boolean thu = toggleThu.isChecked();
                Boolean fri = toggleFri.isChecked();
                Boolean sat = toggleSat.isChecked();

                if (editText.equals("")) {
                    editText = "제목 없음";
                }

                editor.putString("title" + i, editText);
                editor.putBoolean("checkweek" + i, checkBox);
                editor.putBoolean("sun" + i, sun);
                editor.putBoolean("mon" + i, mon);
                editor.putBoolean("tue" + i, tue);
                editor.putBoolean("wed" + i, wed);
                editor.putBoolean("thu" + i, thu);
                editor.putBoolean("fri" + i, fri);
                editor.putBoolean("sat" + i, sat);
                editor.putInt("startHour" + i, startHour);
                editor.putInt("startMin" + i, startMin);
                editor.putInt("endHour" + i, endHour);
                editor.putInt("endMin" + i, endMin);
                editor.commit();

                Intent intent = getIntent();
                if(flag) {
                    i++;
                    Log.d("Add : i = ", String.valueOf(i));
                    intent.putExtra("size", 1);
                } else {
                    Log.d("Modify : i = ", String.valueOf(i));
                    intent.putExtra("size", 2);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
