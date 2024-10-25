package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T007LoginRequest extends TestRun {
    private static Class cls = null;
    private static String username, password;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException, JSONException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        // Before updating SERVER ENDPOINT, generate a test user suitable for login
        // Yes! It will remain after the test
        username = "Dev25User" + (new Date()).getTime();
        password = "abc";
        registerUserForLogin(username, password);

        // Now proceed
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.sessions.InstrumentationLoginTestHelperActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Before
    public void setupIntents() {
        Intents.init();
        // Prevents VideoActivity from really starting, because startup Toasts in such Activity
        // (added in following tasks) would break the current test
        intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void checkRequestSentAndToastShown() throws JSONException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        Class registerCls = Class.forName("com.afundacion.fp.sessions.LoginActivity");
        Field queue = registerCls.getDeclaredField("requestQueue");
        Method login = registerCls.getDeclaredMethod("sendPostLogin");
        Espresso.onView(withHint("elliot.doe")).perform(typeText(username));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", username);
        expectedRequestBody.put("password", password);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        Matcher<View> toast = withText(containsString("Token:"));
        Espresso.onView(toast).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
    }
    
    @After
    public void tearDownIntents() {
        Intents.release();
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

