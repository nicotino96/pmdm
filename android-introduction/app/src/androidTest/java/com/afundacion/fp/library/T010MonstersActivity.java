package com.afundacion.fp.library;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T010MonstersActivity {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void checkNewActivityIsStarted() throws ClassNotFoundException {
        Class photosCls = Class.forName("com.afundacion.fp.library.MonstersActivity");
        Matcher<View> button = withText("Lanzar otra actividad");
        Espresso.onView(button).perform(click());
        intended(hasComponent(photosCls.getName()));
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}
