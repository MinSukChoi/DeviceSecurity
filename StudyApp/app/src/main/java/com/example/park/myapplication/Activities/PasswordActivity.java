package com.example.park.myapplication.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.park.myapplication.Elements.ReferenceMonitor;
import com.example.park.myapplication.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends Activity {
    ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private int CURSOR=0;
    private String password="";
    private int STATE;
    private final static int RESET=0;
    private final static int RECHECK=1;
    private final static int CHECK=2;


    @Bind({R.id.imageView_passwordBox1,R.id.imageView_passwordBox2,R.id.imageView_passwordBox3,R.id.imageView_passwordBox4}) ImageView[] passwordBoxes;
    @Bind(R.id.textVeiw_passwordAlert) TextView passwordAlert;
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
    private void init() {
        switch(STATE) {
            case 0:
                /* 처음으로 패스워드를 설정할 때 */
                passwordAlert.setText("비밀번호를 설정하세요.");
                break;
            case 1:
                /* 비밀번호 재확인 */
                passwordAlert.setText("한번 더 입력해주세요.");
                break;
            case 2:
                /* 비밀번호 확인 */
                passwordAlert.setText("비밀번호를 입력하세요.");
                break;
            default:
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
        ButterKnife.bind(this);
        STATE = getIntent().getExtras().getInt("state");
        init();
    }
}
