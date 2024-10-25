package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T004RegisterRequest extends TestRun {
    private static Class cls = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.sessions.InstrumentationRegisterTestHelperActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkRequestSentAndInitialToastShown() throws JSONException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        Class registerCls = Class.forName("com.afundacion.fp.sessions.RegisterActivity");
        Field queue = registerCls.getDeclaredField("requestQueue");
        Method register = registerCls.getDeclaredMethod("sendPostRequest");
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
