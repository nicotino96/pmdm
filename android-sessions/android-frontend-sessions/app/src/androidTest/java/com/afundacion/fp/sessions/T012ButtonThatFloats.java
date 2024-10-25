package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T012ButtonThatFloats {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        try {
            cls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        } catch (ClassNotFoundException e) {
            // StatusActivity wasn't added
            // Instead of fail before the tests, let them run in a
            // different screen so that they'll count as failed
            e.printStackTrace();
            cls = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
        }
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkButtonIsPresentAndShowsEmptyDialog() throws ClassNotFoundException, NoSuchFieldException {
        cls.getDeclaredField("buttonPutStatus");
        Espresso.onView(buttonWithText("Cambiar estado")).check(doesNotExist());
        Matcher<View> fab = withClassName(containsString("FloatingActionButton"));
        Espresso.onView(fab).check(matches(isDisplayed()));
        Espresso.onView(fab).perform(click());
        Espresso.onView(buttonWithText("Cambiar estado")).check(matches(isDisplayed()));
    }

    private Matcher<View> buttonWithText(String text) {
        return allOf(withClassName(containsString("Button")), withText(text));
    }
}
