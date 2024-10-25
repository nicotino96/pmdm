package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.android.volley.Response;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T011Authentication extends TestRun {
    private static Class cls = null;
    private static String username, password, newStatus;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException, JSONException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        // Before updating SERVER ENDPOINT, generate a test user suitable for login
        // Yes! It will remain after the test
        username = "Dev25User" + (new Date()).getTime();
        password = "abc";
        newStatus = "A" + (int) (Math.random() * 100);
        registerUserForLogin(username, password);
        putNewStatus(username, password, newStatus);

        // Now proceed
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.sessions.InstrumentationLoginTestHelperActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkCustomClassIsCorrect() throws ClassNotFoundException, NoSuchMethodException {
        Class requestCls = Class.forName("com.afundacion.fp.sessions.JsonObjectRequestAuthenticated");
        requestCls.getDeclaredConstructor(int.class, String.class, JSONObject.class, Response.Listener.class, Response.ErrorListener.class, Context.class);
        requestCls.getDeclaredMethod("getHeaders");
    }

    @Test
    public void checkAfterLoginToastIsShown() throws JSONException, ClassNotFoundException, NoSuchFieldException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredField("textViewUserStatus");
        Espresso.onView(withHint("elliot.doe")).perform(typeText(username));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", username);
        expectedRequestBody.put("password", password);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        Matcher<View> toast = withText(containsString("Estado obtenido"));
        Espresso.onView(toast).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
        assertTrue(getSessionClientRequests().contains("GET", "/users/" + username + "/status", null));
    }

    @Test
    public void checkAfterLoginTextIsShown() throws JSONException, ClassNotFoundException, NoSuchFieldException, IOException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredField("textViewUserStatus");
        Espresso.onView(withHint("elliot.doe")).perform(typeText(username));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", username);
        expectedRequestBody.put("password", password);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        assertTrue(getSessionClientRequests().contains("GET", "/users/" + username + "/status", null));
        Espresso.onView(withText(newStatus)).check(matches(isDisplayed()));
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
