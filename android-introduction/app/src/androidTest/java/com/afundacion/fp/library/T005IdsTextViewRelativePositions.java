package com.afundacion.fp.library;

import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T005IdsTextViewRelativePositions {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkTextIsCorrectlyPositioned() {
        Matcher<View> button = withText("Tostada");
        Matcher<View> text = withText("Mantequilla no incluida");
        Espresso.onView(text).check(isCenteredOnParent());
        Espresso.onView(text).check(isCompletelyBelow(button));
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
