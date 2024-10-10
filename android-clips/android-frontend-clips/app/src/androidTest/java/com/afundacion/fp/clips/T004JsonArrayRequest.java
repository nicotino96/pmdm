package com.afundacion.fp.clips;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T004JsonArrayRequest extends TestRun {
    private static Class cls = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.clips.MainActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkMethodExistsAndSendsRequestAndShowsSnackBar() throws NoSuchMethodException {
        Method privateMethod = cls.getDeclaredMethod("getClips");
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Matcher<View> snackbar = withText(containsString("List obtained"));
        Espresso.onView(snackbar).check(matches(isDisplayed()));
    }
}
