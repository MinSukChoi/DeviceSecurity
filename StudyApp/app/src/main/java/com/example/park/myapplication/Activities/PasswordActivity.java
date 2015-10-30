package com.example.park.myapplication.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.park.myapplication.Elements.ReferenceMonitor;
import com.example.park.myapplication.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends Activity {
    ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private int CURSOR=0;
    private String password="";

    @Bind({R.id.imageView_passwordBox1,R.id.imageView_passwordBox2,R.id.imageView_passwordBox3,R.id.imageView_passwordBox4}) ImageView[] passwordBoxes;
    @OnClick({R.id.imageView_passwordButton1,R.id.imageView_passwordButton2,R.id.imageView_passwordButton3,R.id.imageView_passwordButton4,R.id.imageView_passwordButton5,R.id.imageView_passwordButton6,R.id.imageView_passwordButton7,R.id.imageView_passwordButton8,R.id.imageView_passwordButton9,R.id.imageView_passwordButton10,R.id.imageView_passwordButton11,R.id.imageView_passwordButton12}) public void onPasswordButtonClicked(View view) {
        int action=-1;
        switch(view.getId()) {
            case R.id.imageView_passwordButton1:
                action=1;
                break;
            case R.id.imageView_passwordButton2:
                action=2;
                break;
            case R.id.imageView_passwordButton3:
                action=3;
                break;
            case R.id.imageView_passwordButton4:
                action=4;
                break;
            case R.id.imageView_passwordButton5:
                action=5;
                break;
            case R.id.imageView_passwordButton6:
                action=6;
                break;
            case R.id.imageView_passwordButton7:
                action=7;
                break;
            case R.id.imageView_passwordButton8:
                action=8;
                break;
            case R.id.imageView_passwordButton9:
                action=9;
                break;
            case R.id.imageView_passwordButton10:
                action=10;
                break;
            case R.id.imageView_passwordButton11:
                action=0;
                break;
            case R.id.imageView_passwordButton12:
                action=11;
                break;
        }
        if(action<0 || action > 11) {
            /*error*/
        }else if(action<10) {
            /* number selected */

            passwordBoxes[CURSOR++].setImageResource(R.drawable.icon_secret);
            password += action;
            if(CURSOR==3) {
                /* checking password */
            }
        }else if(action==10) {
            CURSOR=0;
            for(int i=0 ; i<4 ; i++) {
                passwordBoxes[i].setImageResource(R.drawable.icon_none);
            }

        }else if(action==11) {
            if(CURSOR>0){
                passwordBoxes[--CURSOR].setImageResource(R.drawable.icon_none);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
        ButterKnife.bind(this);
    }
}
