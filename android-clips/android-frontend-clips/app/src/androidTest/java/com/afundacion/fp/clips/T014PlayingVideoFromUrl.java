package com.afundacion.fp.clips;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import android.view.View;
import android.widget.VideoView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T014PlayingVideoFromUrl extends TestRun {
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
    public void setUpIntentsAndDecorView() {
        Intents.init();
    }

    @Test
    public void checkVideoViewPlaysVideo() throws InterruptedException, ClassNotFoundException, NoSuchFieldException {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(2));
        Espresso.onView(recyclerViewHolderAt(2)).perform(click());
        Class videoCls = Class.forName("com.afundacion.fp.clips.VideoActivity");
        intended(hasComponent(videoCls.getName()));
        Espresso.onView(withVideoView()).perform(waitABit());
        Espresso.onView(withVideoView()).check(isPlayingVideoWithDuration(18325));
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

    private TypeSafeMatcher<View> withVideoView() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return item instanceof VideoView;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("videoView");
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

    private ViewAction waitABit() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "wait a bit";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(2000);
            }
        };
    }

    private ViewAssertion isPlayingVideoWithDuration(int duration) {
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
                            .appendText(" is not playing video with duration " + duration);
                    assertThat(
                            description.toString(),
                            isPlayingVideo(view, duration),
                            is(true));

                }
            }

            private boolean isPlayingVideo(View view, int duration) {
                if (!(view instanceof VideoView)) {
                    return false;
                }
                VideoView videoView = (VideoView) view;
                if (!videoView.isPlaying()) {
                    return false;
                }
                return videoView.getDuration() == duration;
            }
        };
    }
}
