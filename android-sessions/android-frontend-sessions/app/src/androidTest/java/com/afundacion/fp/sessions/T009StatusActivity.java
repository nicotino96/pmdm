package com.afundacion.fp.sessions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
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
public class T009StatusActivity extends TestRun {
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
        generateSessionServerSide("/users/" + username + "/status", 400);
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkStatusActivityStart() throws JSONException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        Class statusCls = Class.forName("com.afundacion.fp.sessions.StatusActivity");
        Espresso.onView(withHint("elliot.doe")).perform(typeText(username));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withHint("Contraseña")).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText("Inicio de sesión")).perform(click());
        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("username", username);
        expectedRequestBody.put("password", password);
        assertTrue(getSessionClientRequests().contains("POST", "/sessions", expectedRequestBody));
        Espresso.onView(textViewWithText("Cargando")).check(isCenteredOnParent());
        Espresso.onView(textViewWithText("Cargando")).check(hasTextSizeInSp(25));
    }

    private Matcher<View> textViewWithText(String text) {
        return allOf(withClassName(containsString("TextView")), withText(text));
    }

    private static ViewAssertion hasTextSizeInSp(int expectedSize) {
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
                            .appendText(" has not " + expectedSize + "sp of textSize");
                    assertThat(
                            description.toString(),
                            densityIndependantCompareTextSize(expectedSize, (TextView) view, view.getContext()),
                            is(true));

                }
            }
        };
    }

    private static boolean densityIndependantCompareTextSize(float expectedPointsSize, TextView textView, Context context) {
        float actualSize = textView.getTextSize() / context.getResources().getDisplayMetrics().scaledDensity;
        return Math.abs(expectedPointsSize - actualSize) < 1;
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
                    ViewMatchers.assertThat(
                            description.toString(),
                            leftToParentEqualsRightToParent(view),
                            Matchers.is(true));

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
