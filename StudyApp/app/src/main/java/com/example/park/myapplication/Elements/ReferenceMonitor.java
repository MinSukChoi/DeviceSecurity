package com.example.park.myapplication.Elements;

import android.content.Context;
import android.content.Intent;

import com.example.park.myapplication.Activities.PasswordActivity;
import com.example.park.myapplication.SecurityModules.PasswordModule;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by minseock on 2015-10-07.
 */
public class ReferenceMonitor {
    private static boolean PERMISSION=false;
    private static PasswordModule passwordModule = PasswordModule.getInstance();

    private static ReferenceMonitor referenceMonitor = new ReferenceMonitor();

    private ReferenceMonitor() {}

    public static ReferenceMonitor getInstance() {
        return referenceMonitor;
    }
    public static boolean checkPassword(String password,String input) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.matchPassword(password,input);
    }

    public static String setPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.registerKey(password);
    }

    public static boolean validate() {
        return PERMISSION;
    }
    public static void setPermission() {
        PERMISSION=true;
    }
    public static void unsetPermission() {
        PERMISSION=false;
    }

}
