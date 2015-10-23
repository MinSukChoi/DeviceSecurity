package com.example.park.myapplication.Observer;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.park.myapplication.R;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observer_main);

        final Monitoring monitoring = new Monitoring();
        Normal normal = new Normal(monitoring, (EditText)findViewById(R.id.output_textbox));
        Normal normal2 = new Normal(monitoring, (EditText)findViewById(R.id.output2_textbox));

        Button submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) findViewById(R.id.input_textbox)).getText().toString();
                monitoring.setStudyMode(s);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ((Button)findViewById(R.id.watchdog_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSubActivity =
                        new Intent(MainActivity.this, WatchDog.class);
                startActivity(intentSubActivity);
            }
        });

        ((Button)findViewById(R.id.applistmove_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSubActivity =
                        new Intent(MainActivity.this, AppList.class);
                startActivity(intentSubActivity);
            }
        });

        ((Button)findViewById(R.id.applist_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ApplicationInfo> mPackageApps = new ArrayList<ApplicationInfo>();
                final PackageManager pm = getApplicationContext().getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                String s = "";
                for (ApplicationInfo packageInfo : packages) {
                    //실행되는 앱들만 null이아니다.
                    Intent intent = getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                    if (intent!=null) {
                        mPackageApps.add(packageInfo);
                        s += packageInfo.packageName + "\n";
                        HttpURLConnection conn    = null;

                        OutputStream os   = null;
                        InputStream is   = null;
                        ByteArrayOutputStream baos = null;

                        try {
                            URL url = new URL("http://getdatafor.appspot.com/data");
                            conn = (HttpURLConnection)url.openConnection();
                            conn.setConnectTimeout(1 * 1000);
                            conn.setReadTimeout(1 * 1000);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Cache-Control", "no-cache");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(packageInfo.packageName);

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

                                String apps = (String) responseJSON.getJSONArray("apps").getJSONObject(0).get("category");
                                s += apps + "\n";
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
                }

                ((TextView)findViewById(R.id.applist_textview)).setText(s);
            }
        });

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
