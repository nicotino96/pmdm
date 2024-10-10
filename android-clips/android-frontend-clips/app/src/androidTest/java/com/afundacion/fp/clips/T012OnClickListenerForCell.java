package com.afundacion.fp.clips;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.app.Instrumentation;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.Root;
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
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T012OnClickListenerForCell extends TestRun {
    private static Class cls = null;

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
    public void setupIntents() {
        Intents.init();
        // Prevents VideoActivity from really starting, because startup Toasts in such Activity
        // (added in following tasks) would break the current test
        intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void checkToastIsCorrectWhenClickingCells() {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        int pos = (new Random()).nextInt(3) + 1;
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(pos));
        Espresso.onView(recyclerViewHolderAt(pos)).perform(click());
        Espresso.onView(withText("Clicked on ViewHolder with clipId: " + (pos+1))).inRoot(isOverlayWindow()).check(matches(isDisplayed()));
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

