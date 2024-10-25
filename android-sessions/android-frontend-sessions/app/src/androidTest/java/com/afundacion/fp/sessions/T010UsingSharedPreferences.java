package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
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
public class T010UsingSharedPreferences extends TestRun {
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

    @Test
    public void checkAfterLoginGetStatusIsSent() throws JSONException, ClassNotFoundException, NoSuchMethodException {
        Class launcherCls = Class.forName("com.afundacion.fp.sessions.LauncherActivity");
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        statusCls.getDeclaredMethod("obtainStatus");
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
    }
}
