package com.example.park.myapplication.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.example.park.myapplication.LockScreenActivity;

/**
 * Created by PARK on 15. 10. 19..
 */
public class LockScreenActivityTest extends ActivityInstrumentationTestCase2<LockScreenActivity> {

    private LockScreenActivity mLockScreenActivity;

    public LockScreenActivityTest() {
        super(LockScreenActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLockScreenActivity = getActivity();

    }

    public void testPreconditions() {
        assertNotNull("mLockScreenActivity is null", mLockScreenActivity);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
