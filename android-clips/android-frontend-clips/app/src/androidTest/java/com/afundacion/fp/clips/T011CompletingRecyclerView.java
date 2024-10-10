package com.afundacion.fp.clips;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import android.view.View;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T011CompletingRecyclerView extends TestRun {
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

    @Test
    public void checkRecyclerClassHasCorrectConstructorAndAttributesAndCell1IsShown() throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Class listCls = Class.forName("com.afundacion.fp.clips.ClipsList");
        Class recyclerCls = Class.forName("com.afundacion.fp.clips.ClipsAdapter");
        Constructor constructor = recyclerCls.getDeclaredConstructor(listCls);
        Field clipsField = recyclerCls.getDeclaredField("clipsToBePresented");
        Espresso.onView(withText("Automatic Dialer (Part II)")).check(hasParentMatching(recyclerViewHolderAt(0)));
    }

    @Test
    public void checkViewHolderHasCorrectMethodAndCells2to5AreShown() throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Class clipCls = Class.forName("com.afundacion.fp.clips.Clip");
        Class viewHolderCls = Class.forName("com.afundacion.fp.clips.ClipViewHolder");
        Method showData = viewHolderCls.getDeclaredMethod("showClip", clipCls);
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(1));
        Espresso.onView(withText("Desparasitándome")).check(hasParentMatching(recyclerViewHolderAt(1)));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(2));
        Espresso.onView(withText("Glasses")).check(hasParentMatching(recyclerViewHolderAt(2)));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(3));
        Espresso.onView(withText("Marcador Automático")).check(hasParentMatching(recyclerViewHolderAt(3)));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(4));
        Espresso.onView(withText("Tarta")).check(hasParentMatching(recyclerViewHolderAt(4)));
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

    private ViewAssertion hasParentMatching(Matcher<View> parentMatcher) {
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
                            .appendText(" does not have a parent matching given matcher");
                    assertThat(
                            description.toString(),
                            someParentMatches(parentMatcher, view),
                            is(true));

                }
            }

            private boolean someParentMatches(Matcher<View> parentMatcher, View view) {
                if (!(view.getParent() instanceof View)) { return false; }
                View parent = (View) view.getParent();
                while (parent != null) {
                    if (parentMatcher.matches(parent)) {
                        return true;
                    }
                    if (parent.getParent() instanceof View) {
                        parent = (View) parent.getParent();
                    } else {
                        parent = null;
                    }
                }
                return false;
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
