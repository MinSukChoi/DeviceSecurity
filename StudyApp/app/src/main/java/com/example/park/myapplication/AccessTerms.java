package com.example.park.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by PARK on 15. 10. 29..
 */
public class AccessTerms extends Activity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_terms);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        Button agreeButton = (Button) findViewById(R.id.agree_btn);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = pref.edit();
                editor.putInt("agree", 1);
                editor.commit();
                Intent intent = getIntent();
                intent.putExtra("agree", 1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button disagreeButton = (Button) findViewById(R.id.disagree_btn);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("agree", 0);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}