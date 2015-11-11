package com.soma.park.myapplication.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.Elements.SettingData;
import com.soma.park.myapplication.Observer.AppList;
import com.soma.park.myapplication.R;

import java.util.ArrayList;

/**
 * Created by PARK on 15. 11. 9..
 */
public class Setting extends Activity {
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        mListView = (ListView) findViewById(R.id.setting_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        // ListView에 아이템 추가
        mAdapter.add("암호 변경");
        mAdapter.add("예약 잠금");
        mAdapter.add("긴급 모드");
        mAdapter.add("휴식 모드");
        mAdapter.add("허용 가능 앱");
        mAdapter.add("스터디 브라우저");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(Setting.this, PasswordActivity.class);
                        intent.putExtra("state", 0);
                        startActivityForResult(intent, 0);
                        break;
                    case 1:
                        if (referenceMonitor.getSTATE() == referenceMonitor.NORMALMODE) {
                            Intent intent1 = new Intent(Setting.this, ReservActivity.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(getApplicationContext(), "공부 시간에는 잠금을 설정할 수 없습니다", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if (pref.getInt("alert", 1) == 1) {
                            Intent intent2 = new Intent(Setting.this, SettingAlertActivity.class);
                            startActivity(intent2);
                        } else {
                            Toast.makeText(Setting.this, "긴급모드를 모두 사용했습니다", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        Intent intent3 = new Intent(Setting.this, SettingBreakActivity.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(Setting.this, AppList.class);
                        startActivityForResult(intent4, 0);
                        break;
                    case 5:
                        Intent intent5 = new Intent(Setting.this, SettingBrowserActivity.class);
                        startActivity(intent5);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private class ViewHolder {
        public TextView mTitle;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<SettingData> mSettingData = new ArrayList<SettingData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mSettingData.size();
        }

        @Override
        public Object getItem(int position) {
            return mSettingData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(String mTitle){
            SettingData addInfo = null;
            addInfo = new SettingData();
            addInfo.mTitle = mTitle;
            mSettingData.add(addInfo);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.setting_list_item, null);
                holder.mTitle = (TextView) convertView.findViewById(R.id.setting_list_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            SettingData mData = mSettingData.get(position);
            holder.mTitle.setText(mData.mTitle);
            return convertView;
        }
    }

}