package com.soma.park.myapplication.Observer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soma.park.myapplication.Elements.AppDetail;
import com.soma.park.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prena on 15. 10. 9..
 */
public class AppList extends Activity {

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    String availList = "";

    public class AppInfo{
        public String appTitle;
        public Drawable appIcon;
        public String appCategory;
        public String appPackage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);

        final Handler mHandler = new Handler();

        final ProgressDialog progressDialog = ProgressDialog.show(AppList.this, "", "로딩중입니다. 잠시만 기다려주세요.", true);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        availList = pref.getString("appList", "");

        Button okButton = (Button) findViewById(R.id.applist_ok_btn);

        okButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
/*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
*/
        mListView = (ListView) findViewById(R.id.listView);

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager manager;
        manager = getPackageManager();
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        final JSONArray jsonArray = new JSONArray();

        final Map<String, AppInfo> dictionary = new HashMap<String, AppInfo>();

        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            jsonArray.put(ri.activityInfo.packageName);
            AppInfo appInfo = new AppInfo();
            appInfo.appTitle = ri.loadLabel(manager).toString();
            appInfo.appIcon = ri.activityInfo.loadIcon(manager);
            appInfo.appPackage = ri.activityInfo.packageName;
            //if(!appInfo.appTitle.contains("한시간의 의지")){
            dictionary.put(ri.activityInfo.packageName, appInfo);
            //}
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn    = null;

                OutputStream os   = null;
                InputStream is   = null;
                ByteArrayOutputStream baos = null;

                try {
                    URL url = new URL("http://getdatafor.appspot.com/data");
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setReadTimeout(0);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject job = new JSONObject();
                    job.put("packages", jsonArray);

                    os = conn.getOutputStream();
                    os.write(job.toString().getBytes());
                    os.flush();

                    String response;

                    int responseCode = conn.getResponseCode();

                    if(responseCode == HttpURLConnection.HTTP_OK) {

                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;
                        while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();

                        response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        JSONArray appList = responseJSON.getJSONArray("apps");

                        AppInfo[] appInfoListOK = new AppInfo[appList.length()];
                        AppInfo[] appInfoListNO = new AppInfo[appList.length()];
                        int cnt1 = 0;
                        int cnt2 = 0;

                        for(int idx=0;idx<appList.length();idx++){
                            final AppInfo appInfo = dictionary.get(appList.getJSONObject(idx).get("package").toString());
                            final String category = appList.getJSONObject(idx).get("category").toString();
                            appInfo.appCategory = category;
                            if((category.contains("Health") || category.contains("Education")) && !appInfo.appTitle.contains("한시간의 의지")){
                                appInfoListOK[cnt1++] = appInfo;
                            }else{
                                appInfoListNO[cnt2++] = appInfo;
                            }
                        }

                        for(int idx=0;idx<cnt1;idx++){
                            final AppInfo appInfo = appInfoListOK[idx];
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.addItem(appInfo.appIcon, appInfo.appTitle, appInfo.appCategory, appInfo.appPackage);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
/*
                        for(int idx=0;idx<cnt2;idx++){
                            final AppInfo appInfo = appInfoListNO[idx];
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.addItem(appInfo.appIcon, appInfo.appTitle, appInfo.appCategory);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
*/
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.appListTitle)).setText("허용 가능 앱 리스트");
                            }
                        });

                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setItemsCanFocus(false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ListData mData = mAdapter.mListData.get(position);

                if (availList.contains("'" + mData.mPackage + "'")) {
                    availList = availList.replaceAll("'" + mData.mPackage + "'", "");
                    mData.mColor = Color.rgb(255, 255, 255);
                    ((ViewHolder) v.getTag()).mLayout.setBackgroundColor(0xFFFFFFFF);
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("appList", availList);
                    editor.commit();
                } else {
                    if (mData.mColor == Color.rgb(255, 255, 255)) {
                        availList += "'" + mData.mPackage + "'";
                        mData.mColor = Color.rgb(0, 255, 0);
                        ((ViewHolder) v.getTag()).mLayout.setBackgroundColor(0xFF00FF00);
                    } else {
                    }
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("appList", availList);
                    editor.commit();
                }

                // preference save with encrypt

                //Toast.makeText(AppList.this, mData.mTitle, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ViewHolder {
        public ImageView mIcon;

        public TextView mText;

        public TextView mDate;

        public RelativeLayout mLayout;
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

        public void addItem(Drawable icon, String mTitle, String mDate, String mPackage){
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mPackage = mPackage;

            if((mDate.contains("Health") || mDate.contains("Education")) && !mTitle.contains("한시간의 의지")){
                addInfo.mColor = Color.rgb(255,255,255);
            }else{
                addInfo.mColor = Color.rgb(255,200,200);
            }

            if(availList.contains(mPackage)){
                addInfo.mColor = Color.rgb(0,255,0);
            }

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void sort(){
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        public void onItemClick(AdapterView<?> arg0, View v, int position,
                                long id) {
            v.setBackgroundColor(Color.BLUE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_xml, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.imageView);
                holder.mText = (TextView) convertView.findViewById(R.id.app_name_textview);
                holder.mDate = (TextView) convertView.findViewById(R.id.app_category_textview);
                holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            if (mData.mIcon != null) {
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIcon.setImageDrawable(mData.mIcon);
            }else{
                holder.mIcon.setVisibility(View.GONE);
            }

            holder.mText.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);
            holder.mLayout.setBackgroundColor(mData.mColor);

            return convertView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}