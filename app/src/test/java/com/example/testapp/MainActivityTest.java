package com.example.testapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by dennis on 8/30/16.
 */
@RunWith(RobolectricTestRunner.class
)
@Config(constants = BuildConfig.class)
public class MainActivityTest {
    @Test
    public void testShouldNotBeNull() throws Exception {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(activity);
    }

}