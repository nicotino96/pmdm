package com.afundacion.fp.library;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T015BottomNavViewRespondsToClicks {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MonstersActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkToast1IsShown() {
        Espresso.onView(withBottomMenuItem("item1")).perform(click());
        Espresso.onView(withText("Primer Digimon")).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
    }

    @Test
    public void checkToast2IsShown() {
        Espresso.onView(withBottomMenuItem("item2")).perform(click());
        Espresso.onView(withText("Segundo Digimon")).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
    }

    @Test
    public void checkToast3IsShown() {
        Espresso.onView(withBottomMenuItem("item3")).perform(click());
        Espresso.onView(withText("Tercer Digimon")).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
    }

    private static TypeSafeMatcher<Root> isOverlayWindow() {
        // From: https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
        return new TypeSafeMatcher<Root>() {
            @Override
            protected boolean matchesSafely(Root item) {
                int type = item.getWindowLayoutParams().get().type;
                if ((type == WindowManager.LayoutParams.TYPE_TOAST) || (type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)) {
                    IBinder windowToken = item.getDecorView().getWindowToken();
                    IBinder appToken = item.getDecorView().getApplicationWindowToken();
                    return windowToken == appToken;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(" is toast");
            }
        };
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
