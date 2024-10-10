package com.afundacion.fp.clips;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.PositionAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T008RecyclerView {
    private static Class cls = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.clips.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkRecyclerViewExistsAndIsCorrect() {
        Matcher<View> parent = withChild(withRecyclerView());
        Espresso.onView(withRecyclerView()).check(matches(isDisplayed()));
        Espresso.onView(withRecyclerView()).check(matches(hasDeclaredId()));
        Espresso.onView(withRecyclerView()).check(PositionAssertions.isBottomAlignedWith(parent));
        Espresso.onView(withRecyclerView()).check(PositionAssertions.isTopAlignedWith(parent));
        Espresso.onView(withRecyclerView()).check(PositionAssertions.isLeftAlignedWith(parent));
        Espresso.onView(withRecyclerView()).check(PositionAssertions.isRightAlignedWith(parent));
    }

    private TypeSafeMatcher<View> withRecyclerView() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item instanceof RecyclerView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("recyclerView");
            }
        };
    }

    private TypeSafeMatcher<View> hasDeclaredId() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item.getId() != -1;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has declared id");
            }
        };
    }
}

