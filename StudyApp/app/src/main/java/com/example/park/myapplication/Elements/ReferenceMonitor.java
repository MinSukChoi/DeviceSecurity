package com.example.park.myapplication.Elements;

import com.example.park.myapplication.SecurityModules.PasswordModule;

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
    protected static void checkPassword(String password) {
    }
}
