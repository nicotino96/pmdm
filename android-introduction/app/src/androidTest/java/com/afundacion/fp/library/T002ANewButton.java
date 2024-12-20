package com.afundacion.fp.library;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T002ANewButton {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkToastButtonExists() {
        Espresso.onView(withText("Tostada")).check(matches(isDisplayed()));
    }
}
