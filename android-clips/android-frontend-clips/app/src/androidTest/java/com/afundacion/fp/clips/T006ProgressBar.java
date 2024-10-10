package com.afundacion.fp.clips;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import android.view.View;
import android.widget.ProgressBar;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T006ProgressBar extends TestRun {
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
    public void checkRequestSentAndServerErrorToastShown() {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Espresso.onView(withProgressBar()).check(matches(isInvisible()));
        Espresso.onView(withProgressBar()).check(isCenteredOnParent());
        Espresso.onView(withText("Hello world!")).check(doesNotExist());
    }

    private TypeSafeMatcher<View> withProgressBar() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item instanceof ProgressBar;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("progressBar");
            }
        };
    }

    private TypeSafeMatcher<View> isInvisible() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item.getVisibility() == View.INVISIBLE;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is invisible");
            }
        };
    }

    private static ViewAssertion isCenteredOnParent() {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                } else {
                    StringDescription description = new StringDescription();
                    description
                            .appendText("View:")
                            .appendText(HumanReadables.describe(view))
                            .appendText(" is not centered in parent");
                    assertThat(
                            description.toString(),
                            leftToParentEqualsRightToParent(view),
                            is(true));

                }
            }
        };
    }

    private static boolean leftToParentEqualsRightToParent(View v) {
        if (!(v.getParent() instanceof View)) {
            return false;
        }
        int[] locationChild = new int[2];
        int[] locationParent = new int[2];
        v.getLocationOnScreen(locationChild);
        ((View) v.getParent()).getLocationOnScreen(locationParent);
        int widthChild = v.getWidth();
        int widthParent = ((View) v.getParent()).getWidth();
        // Calculations
        int leftDistance = locationChild[0] - locationParent[0];
        int rightDistance = (locationParent[0] + widthParent) - (locationChild[0] + widthChild);
        return (rightDistance - leftDistance) <= 1; // delta, just in case
    }
}

