package com.afundacion.fp.clips;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T013ActivityForVideo extends TestRun {
    private static Class cls = null;
    private View decorView;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, IOException, InterruptedException {
        if (BuildConfig.USE_LOCAL_SERVER) {
            Server.name = "http://10.0.2.2:8000";
        }
        Server.name = Server.name + "/testing/Dev25";
        cls = Class.forName("com.afundacion.fp.clips.MainActivity");
        generateSessionServerSide();
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Before
    public void setUpIntentsAndDecorView() {
        Intents.init();
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @Test
    public void checkToastIsCorrectWhenStartingVideoActivity() throws InterruptedException, ClassNotFoundException, NoSuchFieldException {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        int pos = (new Random()).nextInt(3) + 1;
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(pos));
        Espresso.onView(recyclerViewHolderAt(pos)).perform(click());
        Class videoCls = Class.forName("com.afundacion.fp.clips.VideoActivity");
        Field constant1 = videoCls.getDeclaredField("INTENT_CLIP_ID");
        Field constant2 = videoCls.getDeclaredField("INTENT_CLIP_URL");
        intended(hasComponent(videoCls.getName()));
        // Check for Toast in VideoActivity
        Matcher<View> toast = withText("videoID: " + (pos+1));
        boolean toastWasFound = false;
        int millisecondsWaited = 0;
        while ((!toastWasFound) && (millisecondsWaited < 3000)) {
            Log.d("OnStop Test", "OnStop Test: I have waited " + millisecondsWaited + "ms and will check if the toast is there");
            try {
                Espresso.onView(toast).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
                toastWasFound = true;
            } catch (NoMatchingViewException e) {
                Log.d("OnStop Test", "OnStop Test: The toast is not there :(");
                Thread.sleep(500);
                millisecondsWaited += 500;
            }
        }
        Espresso.onView(toast).inRoot(withDecorView(not(decorView))).check(matches(isDisplayed()));
    }

    @After
    public void tearDownIntents() {
        Intents.release();
    }

    private TypeSafeMatcher<View> recyclerViewHolderAt(int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                RecyclerView foundRecyclerView = null;
                if (!(item.getParent() instanceof View)) { return false; }
                View parent = (View) item.getParent();
                while (parent != null) {
                    if (parent instanceof RecyclerView) {
                        foundRecyclerView = (RecyclerView) parent;
                        break;
                    }
                    if (parent.getParent() instanceof View) {
                        parent = (View) parent.getParent();
                    } else {
                        parent = null;
                    }
                }
                if (foundRecyclerView != null) {
                    RecyclerView.ViewHolder viewHolder = foundRecyclerView.findViewHolderForAdapterPosition(position);
                    if (viewHolder != null) {
                        return viewHolder.itemView == item;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("recyclerView child at " + position);
            }
        };
    }

    private TypeSafeMatcher<View> withRecyclerView() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item instanceof RecyclerView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("recyclerView");
            }
        };
    }

    private ViewAction scrollToPosition(int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return withRecyclerView();
            }

            @Override
            public String getDescription() {
                return "scroll to";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                uiController.loopMainThreadForAtLeast(500);
                RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
                if (layout == null) {
                    throw new PerformException.Builder()
                            .withActionDescription("scroll to position in RecyclerView")
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(new Throwable("layoutManager is null"))
                            .build();
                }
                recyclerView.getLayoutManager().scrollToPosition(position);
                uiController.loopMainThreadForAtLeast(2000);
            }
        };
    }
}
