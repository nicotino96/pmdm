package com.afundacion.fp.clips;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
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
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T017FadeOut extends TestRun {
    private static Class cls = null;
    private Object activityClips = null;

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

    @Test
    public void checkAlphaTransitions() {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(4));
        Espresso.onView(recyclerViewHolderAt(4)).perform(click());
        Espresso.onView(withVideoView()).perform(waitFourSeconds());
        Espresso.onView(withVideoView()).perform(click());
        assertTrue(getSessionClientRequests().contains("GET", "/clips/5/appearances?milliseconds="));
        Matcher<View> image = withContentDescription("Personaje en clip");
        Matcher<View> text = withText("Bart");
        Espresso.onView(image).check(hasWidthAndHeightInDp(56));
        Espresso.onView(text).check(isCompletelyBelow(image));
        Espresso.onView(withRecyclerView()).check(hasAlpha(1));
        Espresso.onView(withVideoView()).perform(waitFourSeconds());
        Espresso.onView(withRecyclerView()).check(hasAlpha(0));
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

    private ViewAction waitFourSeconds() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "wait 4 secs";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(4000);
            }
        };
    }

    private static ViewAssertion hasWidthAndHeightInDp(int size) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                } else {
                    float sizeInPixels = view.getResources().getDisplayMetrics().scaledDensity * size;
                    StringDescription description = new StringDescription();
                    description
                            .appendText("View:")
                            .appendText(HumanReadables.describe(view))
                            .appendText(" has not " + size + "dp of width or height");
                    assertThat(
                            description.toString(),
                            widthAndHeightEquals(view, sizeInPixels),
                            is(true));

                }
            }
        };
    }

    private static boolean widthAndHeightEquals(View v, float value) {
        if (!(v.getParent() instanceof View)) {
            return false;
        }
        boolean matchesWidth = Math.abs(v.getWidth() - value) < 1;
        boolean matchesHeight = Math.abs(v.getHeight() - value) < 1;
        return matchesWidth && matchesHeight;
    }

    private static ViewAssertion hasAlpha(float alpha) {
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
                            .appendText(" has not " + alpha + " of alpha");
                    assertThat(
                            description.toString(),
                            alphaEquals(view, alpha),
                            is(true));

                }
            }
        };
    }

    private static boolean alphaEquals(View v, float value) {
        return Math.abs(v.getAlpha() - value) < 0.01;
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
