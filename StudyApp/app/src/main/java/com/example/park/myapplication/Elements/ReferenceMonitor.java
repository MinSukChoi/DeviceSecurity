package com.example.park.myapplication.Elements;

/**
 * Created by minseock on 2015-10-07.
 */
public class ReferenceMonitor {

    private static ReferenceMonitor referenceMonitor = new ReferenceMonitor();

    private ReferenceMonitor() {}

    public static ReferenceMonitor getInstance() {
        return referenceMonitor;
    }
    protected static void demoMethod() {

    }
}
