package com.afundacion.fp.sessions;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.PositionAssertions.isBottomAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isRightAlignedWith;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T006ActivityForLogin extends TestRun {
    private static Class cls = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.sessions.InstrumentationLoginTestHelperActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);


    @Test
    public void checkEditTextPasswordIsCorrect() {
        Espresso.onView(editTextWithHint("Contraseña")).check(isCenteredOnParent());
        Espresso.onView(editTextWithHint("Contraseña")).perform(typeText("hola, hola"));
        Espresso.onView(editTextWithHint("Contraseña")).check(matches(withInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD)));
        Espresso.onView(editTextWithHint("Contraseña")).check(hasDeclaredId());
    }

    @Test
    public void checkEditTextUsernameIsCorrect() {
        Espresso.onView(editTextWithHint("elliot.doe")).check(isCenteredOnParent());
        Espresso.onView(editTextWithHint("Contraseña")).check(isTopToBottomOf(editTextWithHint("elliot.doe")));
        Espresso.onView(editTextWithHint("elliot.doe")).check(isLeftAlignedWith(editTextWithHint("Contraseña")));
        Espresso.onView(editTextWithHint("elliot.doe")).check(isRightAlignedWith(editTextWithHint("Contraseña")));
        Espresso.onView(editTextWithHint("elliot.doe")).perform(typeText("hola, hola"));
        Espresso.onView(editTextWithHint("elliot.doe")).check(hasDeclaredId());
    }

    @Test
    public void checkButtonsAreCorrect() {
        Matcher<View> parent = withChild(withText("Inicio de sesión"));
        Espresso.onView(withText("Inicio de sesión")).check(matches(withClassName(Matchers.containsString("Button"))));
        Espresso.onView(withText("Inicio de sesión")).check(isBottomAlignedWith(parent));
        Espresso.onView(withText("Inicio de sesión")).check(isLeftAlignedWith(parent));
        Espresso.onView(withText("Inicio de sesión")).check(isRightAlignedWith(parent));
        Espresso.onView(withText("Inicio de sesión")).check(hasDeclaredId());
        Espresso.onView(withText("¿Quieres una cuenta?")).check(matches(withClassName(Matchers.containsString("Button"))));
        Espresso.onView(withText("¿Quieres una cuenta?")).check(isLeftAlignedWith(parent));
        Espresso.onView(withText("¿Quieres una cuenta?")).check(isRightAlignedWith(parent));
        Espresso.onView(withText("Inicio de sesión")).check(isTopToBottomOf(withText("¿Quieres una cuenta?")));
    }


    @Test
    public void checkGoToRegisterButtonWorksForthAndBack() throws JSONException {
        Espresso.onView(withText("¿Quieres una cuenta?")).perform(click());
        String randUser = "Dev25User" + (new Date()).getTime();
        String randPass = "Dev25Pass" + (new Date()).getTime();
        Espresso.onView(withHint("alice.doe")).perform(typeText(randUser));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(randPass));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Registrarse")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", randUser);
        expectedRequestBody.put("password", randPass);
        assertTrue(getSessionClientRequests().contains("POST", "/users", expectedRequestBody));
        Matcher<View> toast = withText(containsString("Registro correcto"));
        Espresso.onView(toast).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
        Espresso.onView(withText("Registrarse")).check(doesNotExist());
    }

    private Matcher<View> editTextWithHint(String hint) {
        return allOf(withClassName(Matchers.containsString("EditText")), withHint(hint));
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
