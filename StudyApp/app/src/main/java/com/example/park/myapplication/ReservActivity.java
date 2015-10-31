package com.example.park.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by PARK on 15. 10. 25..
 */
public class ReservActivity extends Activity {
    private static final String TAG = "Reservation";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ReservAddActivity reservAddActivity;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    public static int pos;
    private AlarmManager alarmManager;
    ScreenService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_reservation);

        mService = new ScreenService();
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        Button addButton = (Button) findViewById(R.id.reserv_add_btn);
        Button okButton = (Button) findViewById(R.id.reserv_ok_btn);
        Button clearButton = (Button) findViewById(R.id.reserv_clear_btn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mListView = (ListView) findViewById(R.id.reserv_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        reservAddActivity = new ReservAddActivity();

        // 초기에 아이템 추가
        initialReserv();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // ListData mData = mAdapter.mListData.get(position);
                pos = position + 1;
                Toast.makeText(ReservActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReservActivity.this, ReservAddActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        // 아이템 삭제
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                // 여기서 부터는 알림창의 속성 설정
                builder.setTitle(pref.getString("title"+(position+1), "")) // 제목 설정
                        .setMessage("예약을 삭제하시겠습니까?") // 메세지 설정
                        .setCancelable(false)   // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(pref.getBoolean("checkValue"+(position+1), false)) {
                                    Toast.makeText(getApplicationContext(), "해당 알람이 삭제됩니다", Toast.LENGTH_SHORT).show();
                                    onUnregist(position + 1);   //알람 삭제
                                }
                                mAdapter.remove(position);
                                prefremove(position + 1);
                                dialog.dismiss();  // AlertDialog를 닫는다.
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
                return false;
            }
        });

        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservAddActivity.i = mAdapter.getCount() + 1;
                pos = mAdapter.getCount() + 1;
                Intent intent = new Intent(ReservActivity.this, ReservAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
            }
        });
        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialReserv() {
        int size = pref.getInt("size", 0);
        if (size > 0) {
            for(int j=1; j<=size; j++) {
                Log.d(TAG, "initialReserv");
                String title = pref.getString("title" + j, null);
                int startHour = pref.getInt("startHour" + j, 0);
                int startMin = pref.getInt("startMin" + j, 0);
                int endHour = pref.getInt("endHour" + j, 0);
                int endMin = pref.getInt("endMin" + j, 0);
                String week = pref.getString("week" + j, null);

                String time = String.valueOf(startHour)+":"+String.valueOf(startMin)
                        +" - "+ String.valueOf(endHour)+":"+String.valueOf(endMin);
                String day = "";

                boolean[] sunToMon = { false, pref.getBoolean("sun"+j, false), pref.getBoolean("mon"+j, false),
                        pref.getBoolean("tue" + j, false), pref.getBoolean("wed" + j, false),
                        pref.getBoolean("thu" + j, false), pref.getBoolean("fri" + j, false),
                        pref.getBoolean("sat" + j, false) };

                for(int i=0; i<=7; i++) {
                    if(sunToMon[i]) {
                        if(i==1) {
                            day += "일 ";
                        } else if(i==2) {
                            day += "월 ";
                        } else if(i==3) {
                            day += "화 ";
                        } else if(i==4) {
                            day += "수 ";
                        } else if(i==5) {
                            day += "목 ";
                        } else if(i==6) {
                            day += "금 ";
                        } else if(i==7) {
                            day += "토 ";
                        }
                    }
                }
                mAdapter.addItem(title, time, day, week);
                mAdapter.dataChange();
            }
        }
    }

    private void addReserv() {
        int size = pref.getInt("size", 0);
        if (size > 0) {
            Log.d(TAG, "addReserv");
            String title = pref.getString("title" + size, null);
            String week;
            if (pref.getBoolean("checkweek" + size, false)) {
                week = "매주 반복";
                editor.putString("week" + size, week);
            } else {
                week = "반복 없음";
                editor.putString("week" + size, week);
            }
            int startHour = pref.getInt("startHour" + size, 0);
            int startMin = pref.getInt("startMin" + size, 0);
            int endHour = pref.getInt("endHour" + size, 0);
            int endMin = pref.getInt("endMin" + size, 0);
            String time = String.valueOf(startHour)+":"+String.valueOf(startMin)
                    +" - "+ String.valueOf(endHour)+":"+String.valueOf(endMin);
            String day = "";
            boolean[] sunToMon = { false, pref.getBoolean("sun" + size, false), pref.getBoolean("mon" + size, false),
                    pref.getBoolean("tue" + size, false), pref.getBoolean("wed" + size, false),
                    pref.getBoolean("thu" + size, false), pref.getBoolean("fri" + size, false),
                    pref.getBoolean("sat" + size, false) };
            for(int i=0; i<=7; i++) {
                if(sunToMon[i]) {
                    if(i==1) {
                        day += "일 ";
                    } else if(i==2) {
                        day += "월 ";
                    } else if(i==3) {
                        day += "화 ";
                    } else if(i==4) {
                        day += "수 ";
                    } else if(i==5) {
                        day += "목 ";
                    } else if(i==6) {
                        day += "금 ";
                    } else if(i==7) {
                        day += "토 ";
                    }
                }
            }
            mAdapter.addItem(title, time, day, week);
            editor.putInt("size", mAdapter.getCount());
            editor.commit();
            mAdapter.dataChange();
        }
    }

    public int getPos(){
        return pos;
    }

    private void modifyReserv() {
        int size = pref.getInt("size", 0);
        if (size > 0) {
            Log.d(TAG, "modifyReserv");
            String title = pref.getString("title" + pos, null);
            String week;
            if (pref.getBoolean("checkweek" + pos, false)) {
                week = "매주 반복";
                editor.putString("week" + pos, week);
            } else {
                week = "반복 없음";
                editor.putString("week" + pos, week);
            }
            int startHour = pref.getInt("startHour" + pos, 0);
            int startMin = pref.getInt("startMin" + pos, 0);
            int endHour = pref.getInt("endHour" + pos, 0);
            int endMin = pref.getInt("endMin" + pos, 0);
            String time = String.valueOf(startHour)+":"+String.valueOf(startMin)
                    +" - "+ String.valueOf(endHour)+":"+String.valueOf(endMin);
            String day = "";
            boolean[] sunToMon = { false, pref.getBoolean("sun" + pos, false), pref.getBoolean("mon" + pos, false),
                    pref.getBoolean("tue" + pos, false), pref.getBoolean("wed" + pos, false),
                    pref.getBoolean("thu" + pos, false), pref.getBoolean("fri" + pos, false),
                    pref.getBoolean("sat" + pos, false) };
            for(int i=0; i<=7; i++) {
                if(sunToMon[i]) {
                    if(i==1) {
                        day += "일 ";
                    } else if(i==2) {
                        day += "월 ";
                    } else if(i==3) {
                        day += "화 ";
                    } else if(i==4) {
                        day += "수 ";
                    } else if(i==5) {
                        day += "목 ";
                    } else if(i==6) {
                        day += "금 ";
                    } else if(i==7) {
                        day += "토 ";
                    }
                }
            }
            mAdapter.modify(pos, title, time, day, week);
            editor.commit();
            mAdapter.dataChange();
        }
    }

    private void prefremove(int position) {
        int size = pref.getInt("size", 0);
        Log.d(TAG, String.valueOf(size));
        if (position < size) {
            for (int j = position; j < size; j++) {
                editor.putString("title" + j, pref.getString("title" + (j + 1), null));
                editor.putBoolean("checkweek" + j, pref.getBoolean("checkweek" + (j + 1), false));
                editor.putString("week" + j, pref.getString("week" + (j + 1), ""));
                editor.putBoolean("checkValue" + j, pref.getBoolean("checkValue" + (j + 1), false));
                editor.putBoolean("sun" + j, pref.getBoolean("sun" + (j + 1), false));
                editor.putBoolean("mon" + j, pref.getBoolean("mon" + (j + 1), false));
                editor.putBoolean("tue" + j, pref.getBoolean("tue" + (j + 1), false));
                editor.putBoolean("wed" + j, pref.getBoolean("wed" + (j + 1), false));
                editor.putBoolean("thu" + j, pref.getBoolean("thu" + (j + 1), false));
                editor.putBoolean("fri" + j, pref.getBoolean("fri" + (j + 1), false));
                editor.putBoolean("sat" + j, pref.getBoolean("sat" + (j + 1), false));
                editor.putInt("startHour" + j, pref.getInt("startHour" + (j + 1), 0));
                editor.putInt("startMin" + j, pref.getInt("startMin" + (j + 1), 0));
                editor.putInt("endHour" + j, pref.getInt("endHour" + (j + 1), 0));
                editor.putInt("endMin" + j, pref.getInt("endMin" + (j + 1), 0));
            }
        }
        editor.remove("title" + size);
        editor.remove("checkweek" + size);
        editor.remove("week" + size);
        editor.remove("checkValue" + size);
        editor.remove("sun" + size);
        editor.remove("mon" + size);
        editor.remove("tue" + size);
        editor.remove("wed" + size);
        editor.remove("thu" + size);
        editor.remove("fri" + size);
        editor.remove("sat" + size);
        editor.remove("startHour" + size);
        editor.remove("startMin" + size);
        editor.remove("endHour" + size);
        editor.remove("endMin" + size);
        Log.d(TAG, String.valueOf(mAdapter.getCount()));
        editor.putInt("size", mAdapter.getCount());
        editor.commit();
        mAdapter.dataChange();
    }

    private class ViewHolder {
        public CheckBox mCheck;
        public TextView mTitle;
        public TextView mTime;
        public TextView mWeek;
        public TextView mRepeat;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String mTitle, String mTime, String mWeek, String mRepeat){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mTitle = mTitle;
            addInfo.mTime = mTime;
            addInfo.mWeek = mWeek;
            addInfo.mRepeat = mRepeat;

            mListData.add(addInfo);
        }

        public void modify(int position, String mTitle, String mTime, String mWeek, String mRepeat){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mTitle = mTitle;
            addInfo.mTime = mTime;
            addInfo.mWeek = mWeek;
            addInfo.mRepeat = mRepeat;

            mListData.remove(position - 1);
            mListData.add(position - 1, addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reserv_list_item, null);

                holder.mCheck = (CheckBox) convertView.findViewById(R.id.item_reserv_checkbox);
                holder.mTitle = (TextView) convertView.findViewById(R.id.item_reserv_title);
                holder.mTime = (TextView) convertView.findViewById(R.id.item_reserv_time);
                holder.mWeek = (TextView) convertView.findViewById(R.id.item_reserv_week);
                holder.mRepeat = (TextView) convertView.findViewById(R.id.item_reserv_repeat);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Log.d(TAG, "getView");
            ListData mData = mListData.get(position);

            holder.mTitle.setText(mData.mTitle);
            holder.mTime.setText(mData.mTime);
            holder.mWeek.setText(mData.mWeek);
            holder.mRepeat.setText(mData.mRepeat);
            holder.mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor = pref.edit();
                    editor.putBoolean("checkValue" + (position + 1), isChecked);
                    editor.commit();
                    Log.d(TAG, "isChecked: " + String.valueOf(isChecked));
                    if (isChecked) {
                        onRegist(position + 1);
                    } else {
                        onUnregist(position + 1);
                    }
                }
            });

            holder.mCheck.setChecked(pref.getBoolean("checkValue" + (position + 1), false));

//            holder.mCheck.setChecked(pref.getBoolean("checkValue" + (position + 1), false));
//            Log.d(TAG, "position: " + String.valueOf(position));
//            Log.d(TAG, "pref_position: " + String.valueOf(position+1));
//
//
//            holder.mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    editor = pref.edit();
//                    editor.putBoolean("checkValue" + (position + 1), isChecked);
//                    editor.commit();
//                    Log.d(TAG, "isChecked: " + String.valueOf(isChecked));
//                    if (isChecked) {
//                        onRegist(position + 1);
//                    } else {
//                        onUnregist(position + 1);
//                    }
//                }
//            });

            return convertView;
        }
    }

    private void onRegist(int position)
    {
        boolean[] week = { false, pref.getBoolean("sun"+position, false), pref.getBoolean("mon"+position, false),
                pref.getBoolean("tue" + position, false), pref.getBoolean("wed" + position, false),
                pref.getBoolean("thu" + position, false), pref.getBoolean("fri" + position, false),
                pref.getBoolean("sat" + position, false) };    // sunday=1 이라서 0의 자리에는 아무 값이나 넣었음
        boolean checkweek = pref.getBoolean("checkweek"+position, false);
        long oneday = 24 * 60 * 60 * 1000;  // 24시간

        Log.d(TAG, "position: " + String.valueOf(position-1));
        Log.d(TAG, "pref_position: " + String.valueOf(position));

        Intent intent1 = new Intent(this, AlarmStartReceiver.class);
        intent1.putExtra("weekday", week);
        intent1.putExtra("checkweek", checkweek);
        intent1.putExtra("position", position);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, pref.getInt("startHour" + position, 0));
        calendar1.set(Calendar.MINUTE, pref.getInt("startMin" + position, 0));
        calendar1.set(Calendar.SECOND, 0);
        //Log.d(TAG, String.valueOf(calendar1.getTime()));

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.SECOND, 0);
        long current = currentCal.getTimeInMillis();

//        Log.d(TAG, String.valueOf(calendar1.getTime()));
//        Log.d(TAG, String.valueOf(currentCal.getTime()));
//        Log.d(TAG, String.valueOf(calendar1.getTimeInMillis()));
//        Log.d(TAG, String.valueOf(current));

        // 만약 트리거 타임이 과거로 설정되어있다면, 알람은 즉시 트리거된다.
        // 알람이 트리거 될 때 펜딩 인텐트가 작동된다.
        // 같은 펜딩 인텐트를 사용하는 두 번째 알람을 셋팅하면, 그것은 첫번째 알람을 대체하게 된다.

        if(calendar1.getTimeInMillis() >= current) {   // 현재 시간 이후이면
            Log.d(TAG, String.valueOf(calendar1.getTime()));
            Log.d(TAG, String.valueOf(currentCal.getTime()));
            PendingIntent pIntent1 = PendingIntent.getBroadcast(this, position, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pIntent1);
        }

        Intent intent2 = new Intent(this, AlarmStopReceiver.class);
        intent2.putExtra("weekday", week);
        intent2.putExtra("checkweek", checkweek);
        intent2.putExtra("position", position);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, pref.getInt("endHour" + position, 0));
        calendar2.set(Calendar.MINUTE, pref.getInt("endMin" + position, 0));
        calendar2.set(Calendar.SECOND, 0);
        //Log.d(TAG, String.valueOf(calendar2.getTime()));

        if(calendar1.getTimeInMillis() >= current) {
            Log.d(TAG, String.valueOf(calendar2.getTime()));
            Log.d(TAG, String.valueOf(currentCal.getTime()));
            PendingIntent pIntent2 = PendingIntent.getBroadcast(this, position, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pIntent2);
        }
    }

    private void onUnregist(int position)
    {
        Log.d(TAG, String.valueOf(position-1)+"번째 예약 해제");
        Intent intent1 = new Intent(this, AlarmStartReceiver.class);
        PendingIntent pIntent1 = PendingIntent.getBroadcast(this, position, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pIntent1);

        Intent intent2 = new Intent(this, AlarmStopReceiver.class);
        PendingIntent pIntent2 = PendingIntent.getBroadcast(this, position, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pIntent2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult");
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();
        //super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    editor.putInt("size", mAdapter.getCount()+1);
                    editor.commit();
                    addReserv();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    modifyReserv();
                }
                break;
        }
    }
}