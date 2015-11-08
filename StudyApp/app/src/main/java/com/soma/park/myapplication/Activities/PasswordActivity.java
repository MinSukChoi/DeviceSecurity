package com.soma.park.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soma.park.myapplication.Elements.ReferenceMonitor;
import com.soma.park.myapplication.R;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends Activity {
    private ReferenceMonitor referenceMonitor = ReferenceMonitor.getInstance();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int CURSOR=0;
    private String password="";
    private int STATE;
    private final static int RESET=0;
    private final static int RECHECK=1;
    private final static int CHECK=2;
    private String checkedPassword="";


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
            if(CURSOR < 4){
                passwordBoxes[CURSOR++].setImageResource(R.drawable.icon_secret);
                password += action;
                if (CURSOR == 4) {
                    try {
                        inputCompleted();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if(action==10) {
            CURSOR=0;
            for(int i=0 ; i<4 ; i++) {
                passwordBoxes[i].setImageResource(R.drawable.icon_none);
            }
            password="";

        }else if(action==11) {
            if(CURSOR>0){
                passwordBoxes[--CURSOR].setImageResource(R.drawable.icon_none);
                password = password.substring(0,CURSOR);
            }
        }
    }
    private void inputCompleted() throws InvalidKeySpecException, NoSuchAlgorithmException {
        switch(STATE) {
            case 0:
                checkedPassword = password;
                Log.v("PASSWORD_ACTIVITY", "checkedPassword="+checkedPassword);
                STATE=1;
                init();
                break;
            case 1:
                Log.v("PASSWORD_ACTIVITY", "password="+password);
                if(checkedPassword.equals(password)) {
                    Toast.makeText(PasswordActivity.this, "비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    editor = pref.edit();
                    editor.putString("hash", referenceMonitor.setPassword(password));
                    editor.putInt("First", 1);
                    editor.commit();
                    finish();

                }else {
                    Toast.makeText(PasswordActivity.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    Log.v("PASSWORD_ACTIVITY", "NO");
                    STATE=0;
                    init();
                }
                break;
            case 2:
                /* 비밀번호 확인 */
                Intent intent = getIntent();
                if(referenceMonitor.checkPassword(pref.getString("hash",""),password)) {
                    intent.putExtra("validation", 1);
                    //referenceMonitor.setPermission();
                    if(referenceMonitor.getSTATE()==referenceMonitor.STUDYMODE) referenceMonitor.setNormalmode();
                }else  {
                    intent.putExtra("validation", 0);
                    //referenceMonitor.unsetPermission();
                    if(referenceMonitor.getSTATE()==referenceMonitor.STUDYMODE) referenceMonitor.setInvalidmode();
                }
                setResult(RESULT_OK, intent);
                finish();
                /*
                if(referenceMonitor.checkPassword(pref.getString("hash",""),password)) {
                    referenceMonitor.setPermission();
                }else referenceMonitor.unsetPermission();
                */
                break;
            case 3:
                /* 긴급모드 진입시 비밀번호 확인 */
                Intent newintent = getIntent();
                if(referenceMonitor.checkPassword(pref.getString("hash",""),password)) {
                }else  {
                   referenceMonitor.setInvalidmode();
                }
                finish();
                /*
                if(referenceMonitor.checkPassword(pref.getString("hash",""),password)) {
                    referenceMonitor.setPermission();
                }else referenceMonitor.unsetPermission();
                */
                break;
            default:
                break;
        }
    }
    private void init() {
        CURSOR=0;
        for(int i=0 ; i<4 ; i++) {
            passwordBoxes[i].setImageResource(R.drawable.icon_none);
        }
        password="";
        switch(STATE) {
            case 0:
                /* 처음으로 패스워드를 설정할 때 */
                passwordAlert.setText("비밀번호를 설정하세요.");
                break;
            case 1:
                /* 비밀번호 재확인 */
                passwordAlert.setText("한번 더 입력해주세요.");
                break;
            default:
                /* 비밀번호 확인 */
                passwordAlert.setText("비밀번호를 입력하세요.");
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);
        ButterKnife.bind(this);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        STATE = getIntent().getExtras().getInt("state");
        init();
    }
}
