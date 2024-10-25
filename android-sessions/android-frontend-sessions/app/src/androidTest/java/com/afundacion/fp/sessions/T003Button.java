package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isBottomAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isRightAlignedWith;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T003Button {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkRegisterButtonIsCorrect() {
        Matcher<View> parent = withChild(withText("Registrarse"));
        Espresso.onView(withText("Registrarse")).check(matches(withClassName(containsString("Button"))));
        Espresso.onView(withText("Registrarse")).check(isBottomAlignedWith(parent));
        Espresso.onView(withText("Registrarse")).check(isLeftAlignedWith(parent));
        Espresso.onView(withText("Registrarse")).check(isRightAlignedWith(parent));
        Espresso.onView(withText("Registrarse")).check(hasDeclaredId());
    }

    @Test
    public void checkRegisterActivityWorksProperly() throws ClassNotFoundException, NoSuchFieldException {
        Class registerCls = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
        Field editText1 = registerCls.getDeclaredField("editTextUser");
        Field editText2 = registerCls.getDeclaredField("editTextPassword");
        Field button = registerCls.getDeclaredField("buttonRegister");
        String randomText = "Nelson said haha " + ((int)(Math.random() * 20000)) + " times";
        Espresso.onView(editTextWithHint("alice.doe")).perform(typeText(randomText));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Registrarse")).perform(click());
        Matcher<View> toast = withText("Nombre: " + randomText);
        Espresso.onView(toast).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
    }

    private Matcher<View> editTextWithHint(String hint) {
        return allOf(withClassName(containsString("EditText")), withHint(hint));
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
}

