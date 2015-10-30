package com.example.park.myapplication.Elements;

import com.example.park.myapplication.SecurityModules.PasswordModule;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by minseock on 2015-10-07.
 */
public class ReferenceMonitor {
    private static PasswordModule passwordModule = PasswordModule.getInstance();

    private static ReferenceMonitor referenceMonitor = new ReferenceMonitor();

    private ReferenceMonitor() {}

    public static ReferenceMonitor getInstance() {
        return referenceMonitor;
    }
    protected static boolean checkPassword(String password,String input) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.matchPassword(password,input);
    }

    public static String setPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return passwordModule.registerKey(password);
    }
}
