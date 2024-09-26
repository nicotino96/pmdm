package com.afundacion.fp.library;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static org.hamcrest.Matchers.not;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T012OnStop {
    private static Class cls = null;
    private View decorView;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Before
    public void setUp() {
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @Test
    public void checkOnStopToastIsThrown() throws InterruptedException {
        Matcher<View> button = withText("Lanzar otra actividad");
        Matcher<View> toast = withText("MainActivity en segundo plano");
        Espresso.onView(button).perform(click());
        boolean toastWasFound = false;
        int millisecondsWaited = 0;
        while ((!toastWasFound) && (millisecondsWaited < 3000)) {
            Log.d("OnStop Test", "OnStop Test: I have waited " + millisecondsWaited + "ms and will check if the toast is there");
            try {
                Espresso.onView(toast).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
                toastWasFound = true;
            } catch (NoMatchingViewException e) {
                Log.d("OnStop Test", "OnStop Test: The toast is not there :(");
                Thread.sleep(500);
                millisecondsWaited += 500;
            }
        }
        Espresso.onView(toast).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
    }
}
