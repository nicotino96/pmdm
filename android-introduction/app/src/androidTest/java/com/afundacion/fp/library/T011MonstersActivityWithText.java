package com.afundacion.fp.library;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import android.view.View;
import android.widget.TextView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T011MonstersActivityWithText {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkMonstersActivityTitle() {
       Matcher<View> button = withText("Lanzar otra actividad");
        Espresso.onView(button).perform(click());
        Matcher<View> withTitleText = withText("Biblioteca de monstruos");
        Matcher<View> withCorrectSize = withFontSize(24);
        Matcher<View> withCorrectColor = withTextColor(0xff080701);
        Matcher<View> withTitleAndSize = allOf(withTitleText, withCorrectSize);
        Matcher<View> withTitleAndSizeAndColor = allOf(withTitleAndSize, withCorrectColor);
        Espresso.onView(withTitleAndSizeAndColor).check(matches(isDisplayed()));
    }

    private static Matcher<View> withFontSize(float expectedPointsSize) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof TextView)) {
                    return false;
                }
                TextView targetEditText = (TextView) target;
                float pixels = targetEditText.getTextSize();
                float actualSize = pixels / target.getResources().getDisplayMetrics().scaledDensity;
                return Math.abs(expectedPointsSize - actualSize) < 1;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with fontSize: ");
                description.appendValue(expectedPointsSize);
            }
        };
    }

    private static Matcher<View> withTextColor(int expectedColor) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof TextView)) {
                    return false;
                }
                TextView targetEditText = (TextView) target;
                int actualColor = targetEditText.getCurrentTextColor();
                return actualColor == expectedColor;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with textColor: ");
                description.appendValue(expectedColor);
            }
        };
    }
}
