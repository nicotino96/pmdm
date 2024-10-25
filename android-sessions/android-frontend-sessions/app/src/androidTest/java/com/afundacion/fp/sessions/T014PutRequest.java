package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
public class T014PutRequest extends TestRun {
    private static Class cls = null;
    private static String usernameOkStatus, passwordOkStatus;
    private static String usernameKoStatus, passwordKoStatus;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException, JSONException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        // Before updating SERVER ENDPOINT, generate a test user suitable for login
        // Yes! It will remain after the test
        usernameOkStatus = "Dev25UserOK" + (new Date()).getTime();
        passwordOkStatus = "abc";
        usernameKoStatus = "Dev25UserKO" + (new Date()).getTime();
        passwordKoStatus = "abc";
        registerUserForLogin(usernameOkStatus, passwordOkStatus);
        registerUserForLogin(usernameKoStatus, passwordKoStatus);

        // Now proceed
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.sessions.InstrumentationLoginTestHelperActivity");
        generateSessionServerSide("/users/" + usernameKoStatus + "/status", 400);
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkPutIsSent() throws JSONException, ClassNotFoundException, NoSuchMethodException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredMethod("updateStatus");
        // Login
        Espresso.onView(withHint("elliot.doe")).perform(typeText(usernameOkStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(passwordKoStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", usernameOkStatus);
        expectedRequestBody.put("password", passwordOkStatus);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        // Type on dialog
        Matcher<View> fab = withClassName(Matchers.containsString("FloatingActionButton"));
        Espresso.onView(fab).perform(click());
        String newStatus = "EEE" + (int) (Math.random() * 100);
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).perform(typeText(newStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(buttonWithText("Cambiar estado")).perform(click());
        // Assert
        JSONObject expectedRequestBodyForPut = new JSONObject();
        expectedRequestBodyForPut.put("status", newStatus);
        assertTrue(getSessionClientRequests().contains("PUT", "/users/" + usernameOkStatus + "/status", expectedRequestBodyForPut));
    }

    @Test
    public void checkNewStatusIsUpdated() throws JSONException, ClassNotFoundException, NoSuchMethodException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredMethod("updateStatus");
        // Login
        Espresso.onView(withHint("elliot.doe")).perform(typeText(usernameOkStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(passwordKoStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", usernameOkStatus);
        expectedRequestBody.put("password", passwordOkStatus);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        // Type on dialog
        Matcher<View> fab = withClassName(Matchers.containsString("FloatingActionButton"));
        Espresso.onView(fab).perform(click());
        String newStatus = "EEE" + (int) (Math.random() * 100);
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).perform(typeText(newStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(buttonWithText("Cambiar estado")).perform(click());
        // Assert
        JSONObject expectedRequestBodyForPut = new JSONObject();
        expectedRequestBodyForPut.put("status", newStatus);
        assertTrue(getSessionClientRequests().contains("PUT", "/users/" + usernameOkStatus + "/status", expectedRequestBodyForPut));
        assertTrue(getSessionClientRequests().contains("GET", "/users/" + usernameOkStatus + "/status", null));
        Espresso.onView(withText(newStatus)).check(matches(isDisplayed()));
    }

    @Test
    public void checkLoadingIsShown() throws JSONException, ClassNotFoundException, NoSuchMethodException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredMethod("updateStatus");
        // Login
        Espresso.onView(withHint("elliot.doe")).perform(typeText(usernameKoStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(passwordKoStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", usernameOkStatus);
        expectedRequestBody.put("password", passwordOkStatus);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        // Type on dialog
        Matcher<View> fab = withClassName(Matchers.containsString("FloatingActionButton"));
        Espresso.onView(fab).perform(click());
        String newStatus = "EEE" + (int) (Math.random() * 100);
        Espresso.onView(editTextWithHint("Hey! I have changed my status")).perform(typeText(newStatus));
        Espresso.closeSoftKeyboard();
        Espresso.onView(buttonWithText("Cambiar estado")).perform(click());
        // Assert
        JSONObject expectedRequestBodyForPut = new JSONObject();
        expectedRequestBodyForPut.put("status", newStatus);
        assertTrue(getSessionClientRequests().contains("PUT", "/users/" + usernameKoStatus + "/status", expectedRequestBodyForPut));
        assertTrue(getSessionClientRequests().contains("GET", "/users/" + usernameKoStatus + "/status", null));
        Espresso.onView(withText(newStatus)).check(doesNotExist());
        Espresso.onView(withText("Cargando")).check(matches(isDisplayed()));
    }

    private Matcher<View> buttonWithText(String text) {
        return allOf(withClassName(Matchers.containsString("Button")), withText(text));
    }

    private Matcher<View> editTextWithHint(String hint) {
        return allOf(withClassName(Matchers.containsString("EditText")), withHint(hint));
    }
}
