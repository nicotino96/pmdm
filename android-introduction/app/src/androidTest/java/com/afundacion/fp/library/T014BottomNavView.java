package com.afundacion.fp.library;

import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.PositionAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T014BottomNavView {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MonstersActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkElementsAreCorrect() {
        Matcher<View> image = withContentDescription("Un Digimon poderoso");
        Matcher<View> parent = withChild(withText("Biblioteca de monstruos"));
        Espresso.onView(withBottomMenuItem("item1")).check(PositionAssertions.isBottomAlignedWith(parent));
        Espresso.onView(withBottomMenuItem("item2")).check(PositionAssertions.isBottomAlignedWith(parent));
        Espresso.onView(withBottomMenuItem("item3")).check(PositionAssertions.isBottomAlignedWith(parent));
        Espresso.onView(withBottomMenuItem("item1")).check(PositionAssertions.isCompletelyBelow(image));
        Espresso.onView(withBottomMenuItem("item2")).check(PositionAssertions.isCompletelyBelow(image));
        Espresso.onView(withBottomMenuItem("item3")).check(PositionAssertions.isCompletelyBelow(image));
    }

    /**
     * // TODO: Find a better solution
     * @param itemId The toString method will be matched against this parameter
     */
    private static TypeSafeMatcher<View> withBottomMenuItem(String itemId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof BottomNavigationItemView)) {
                    return false;
                }
                BottomNavigationItemView itemView = (BottomNavigationItemView) item;
                return itemView.toString().contains(itemId);
            }

            @Override
            public void describeTo(Description description) {}
        };
    }
}
