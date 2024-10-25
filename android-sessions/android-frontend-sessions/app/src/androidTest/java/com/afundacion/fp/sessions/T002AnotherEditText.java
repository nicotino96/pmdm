package com.afundacion.fp.sessions;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isRightAlignedWith;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import junit.framework.AssertionFailedError;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T002AnotherEditText {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkEditTextPasswordIsCorrect() {
        Espresso.onView(editTextWithHint("Contraseña")).check(isCenteredOnParent());
        Espresso.onView(editTextWithHint("Contraseña")).perform(typeText("hola, hola"));
        Espresso.onView(editTextWithHint("Contraseña")).check(isLeftAlignedWith(editTextWithHint("alice.doe")));
        Espresso.onView(editTextWithHint("Contraseña")).check(isRightAlignedWith(editTextWithHint("alice.doe")));
        Espresso.onView(editTextWithHint("Contraseña")).check(isTopToBottomOf(editTextWithHint("alice.doe")));
        Espresso.onView(editTextWithHint("Contraseña")).check(matches(withInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD)));
        Espresso.onView(editTextWithHint("Contraseña")).check(hasDeclaredId());
        Espresso.onView(editTextWithHint("alice.doe")).check(hasDeclaredId());
    }

    private Matcher<View> editTextWithHint(String hint) {
        return allOf(withClassName(containsString("EditText")), withHint(hint));
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

    private static ViewAssertion hasDeclaredId() {
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
                            .appendText(" has not a declared ID");
                    assertThat(
                            description.toString(),
                            idIsValid(view),
                            is(true));
                }
            }
        };
    }

    private static boolean idIsValid(View v) {
        return v.getId() != -1;
    }

    private static ViewAssertion isTopToBottomOf(Matcher<View> matcher) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                } else {
                    View anotherView = findView(matcher, getTopViewGroup(view));
                    StringDescription description = new StringDescription();
                    if (anotherView == null) {
                        description.appendText("View for given matcher does not exist");
                        throw new AssertionFailedError(description.toString());
                    }
                    description
                            .appendText("View:")
                            .appendText(HumanReadables.describe(view))
                            .appendText(" is not top to bottom of specified matcher");
                    assertThat(
                            description.toString(),
                            topEqualsBottomOf(view, anotherView),
                            is(true));
                }
            }
        };
    }

    private static boolean topEqualsBottomOf(View v1, View v2) {
        int[] locationView1 = new int[2];
        int[] locationView2 = new int[2];
        v1.getLocationOnScreen(locationView1);
        v2.getLocationOnScreen(locationView2);
        int bottomOfView2 = locationView2[1] + v2.getHeight();
        int topOfView1 = locationView1[1];
        return Math.abs(bottomOfView2 - topOfView1) <= 1; // delta, just in case
    }

    private static View findView(Matcher<View> matcher, ViewGroup group) {
        if (matcher.matches(group)) {
            return group;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            if (matcher.matches(group.getChildAt(i))) {
                return group.getChildAt(i);
            }
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof ViewGroup) {
                View foundView = findView(matcher, (ViewGroup) group.getChildAt(i));
                if (foundView != null) {
                    return foundView;
                };
            }
        }
        return null;
    }

    private static ViewGroup getTopViewGroup(View view) {
        ViewParent currentParent = view.getParent();
        ViewGroup topView = null;
        while (currentParent != null) {
            if (currentParent instanceof ViewGroup) {
                topView = (ViewGroup) currentParent;
            }
            currentParent = currentParent.getParent();
        }
        return topView;
    }
}
