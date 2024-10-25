package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
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
public class T013DialogBody {
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
    public void checkButtonIsPresentAndShowsCorrectDialog() throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        cls.getDeclaredField("editTextModifyStatus");
        cls.getDeclaredMethod("inflateDialog");
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).check(doesNotExist());
        Matcher<View> fab = withClassName(containsString("FloatingActionButton"));
        Espresso.onView(fab).perform(click());
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).perform(typeText("aaaa"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(buttonWithText("Cambiar estado")).perform(click());
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).check(doesNotExist());
    }

    private Matcher<View> buttonWithText(String text) {
        return allOf(withClassName(containsString("Button")), withText(text));
    }

    private Matcher<View> editTextWithHint(String hint) {
        return allOf(withClassName(containsString("EditText")), withHint(hint));
    }
}

