package com.example.park.myapplication.Elements;

import com.example.park.myapplication.SecurityModules.PasswordModule;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by minseock on 2015-10-07.
 */
public class ReferenceMonitor {
    private static int STATE;
    public static final int NORMALMODE=0;
    public static final int STUDYMODE=1;
    public static final int INVALIDMODE=2;
    public static final int ALERTMODE=3;
    public static final int BREAKTIMEMODE=4;

    private static boolean PERMISSION=true;
    private static PasswordModule passwordModule = PasswordModule.getInstance();

    private static ReferenceMonitor referenceMonitor = new ReferenceMonitor();

    private ReferenceMonitor() {
    }

    public static ReferenceMonitor getInstance() {
        return referenceMonitor;
    }
    public static boolean checkPassword(String password,String input) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.matchPassword(password,input);
    }

    public static String setPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.registerKey(password);
    }
    public static int getSTATE(){
        return STATE;
    }
    public static void setStudymode() {
        STATE=STUDYMODE;
    }
    public static void setNormalmode() {
        STATE=NORMALMODE;
    }
    public static void setInvalidmode() {
        STATE=INVALIDMODE;
    }
    public static void setAlertmode() {
        STATE=ALERTMODE;
    }
    public static void setBreaktimemode() {
        STATE=BREAKTIMEMODE;
    }
}
