package com.afundacion.fp.clips;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isBottomAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.PositionAssertions.isLeftAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isRightAlignedWith;
import static androidx.test.espresso.assertion.PositionAssertions.isTopAlignedWith;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
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
import androidx.test.platform.app.InstrumentationRegistry;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T016Appearances extends TestRun {
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
    public void checkModelObjectsAreCorrect() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, JSONException, InstantiationException {
        // Character
        Class characterCls = Class.forName("com.afundacion.fp.clips.Character");
        characterCls.getDeclaredField("name"); // Ignore result; just test it exists
        characterCls.getDeclaredField("lastName"); // Ignore result; just test it exists
        characterCls.getDeclaredField("description"); // Ignore result; just test it exists
        characterCls.getDeclaredField("imageUrl"); // Ignore result; just test it exists
        Method nameGetter = characterCls.getDeclaredMethod("getName");
        Constructor characterConstructor = characterCls.getDeclaredConstructor(JSONObject.class);
        JSONObject aChar = new JSONObject("{\"name\": \"LisaTest\",\"surname\": \"Simpson\", \"description\": \"Benevolente y educada hija de la familia Simpson\", \"imageUrl\": \"https://raw.githubusercontent.com/rubenmv0/fp/main/simpsons/lisa.png\"}");
        Object character = characterConstructor.newInstance(aChar);
        assertEquals("LisaTest", nameGetter.invoke(character));

        // CharactersList
        Class listCls = Class.forName("com.afundacion.fp.clips.CharactersList");
        Method getListMethod = listCls.getDeclaredMethod("getCharacters");
        Constructor listConstructor = listCls.getDeclaredConstructor(JSONArray.class);
        JSONArray aList = new JSONArray("[{\"name\": \"LisaTest\",\"surname\": \"Simpson\", \"description\": \"Benevolente y educada hija de la familia Simpson\", \"imageUrl\": \"https://raw.githubusercontent.com/rubenmv0/fp/main/simpsons/lisa.png\"},{\"name\": \"LisaTest2\",\"surname\": \"Simpson\", \"description\": \"Benevolente y educada hija de la familia Simpson\", \"imageUrl\": \"https://raw.githubusercontent.com/rubenmv0/fp/main/simpsons/lisa.png\"}]");
        Object list = listConstructor.newInstance(aList);
        List<Object> deserializedList = (List<Object>) getListMethod.invoke(list);
        assertEquals(2, deserializedList.size());
        assertEquals("LisaTest", nameGetter.invoke(deserializedList.get(0)));
        assertEquals("LisaTest2", nameGetter.invoke(deserializedList.get(1)));
    }

    @Test
    public void checkViewHolderXmlIsCorrect() {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int id = c.getResources().getIdentifier("character_recycler_cell", "layout", "com.afundacion.fp.clips");
        assertNotEquals(0, id);
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                View c = activity.getLayoutInflater().inflate(id, null);
                assertTrue(c instanceof ConstraintLayout);
                ConstraintLayout cell = (ConstraintLayout) c;
                assertEquals(2, cell.getChildCount());
                TextView textViewChild = getChildTextView(cell);
                assertNotNull(textViewChild);
                assertNotEquals(-1, textViewChild.getId());
                assertTrue(densityIndependantCompareTextSize(14, textViewChild.getTextSize(), activity));
                ImageView imageViewChild = getChildImageView(cell);
                assertNotNull(imageViewChild);
                assertNotEquals(-1, imageViewChild.getId());
                assertEquals("Personaje en clip", imageViewChild.getContentDescription());
            }
        });
    }

    @Test
    public void checkCharacterViewHolderClassIsCorrect() throws NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
        Class viewHolderClass = Class.forName("com.afundacion.fp.clips.CharacterViewHolder");
        viewHolderClass.getDeclaredConstructor(View.class);
        viewHolderClass.getDeclaredField("textName");
        viewHolderClass.getDeclaredField("characterImageView");
        assertTrue(viewHolderClass.getSuperclass().toString().contains("ViewHolder"));
    }

    @Test
    public void checkRecyclerClassHasCorrectConstructorAndAttributes() throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException {
        Class listCls = Class.forName("com.afundacion.fp.clips.CharactersList");
        Class viewHolderClass = Class.forName("com.afundacion.fp.clips.CharacterViewHolder");
        Class recyclerCls = Class.forName("com.afundacion.fp.clips.CharactersAdapter");
        Constructor constructor = recyclerCls.getDeclaredConstructor(listCls);
        Field field = recyclerCls.getDeclaredField("characters");
        Method create = recyclerCls.getDeclaredMethod("onCreateViewHolder", ViewGroup.class, int.class);
        Method bind = recyclerCls.getDeclaredMethod("onBindViewHolder", viewHolderClass, int.class);
        Method size = recyclerCls.getDeclaredMethod("getItemCount");
    }

    @Test
    public void checkRecyclerIsCorrectlyPositioned() {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Espresso.onView(recyclerViewHolderAt(0)).perform(click());
        Espresso.onView(withVideoView()).perform(click());
        assertTrue(getSessionClientRequests().contains("GET", "/clips/1/appearances?milliseconds="));
        Espresso.onView(withRecyclerView()).check(isTopAlignedWith(withChild(withRecyclerView())));
        Espresso.onView(withRecyclerView()).check(isBottomAlignedWith(withChild(withRecyclerView())));
        String startOrEnd = "Start";
        if (startOrEnd.equals("Start")) {
            Espresso.onView(withRecyclerView()).check(isLeftAlignedWith(withChild(withRecyclerView())));
        }
        if (startOrEnd.equals("End")) {
            Espresso.onView(withRecyclerView()).check(isRightAlignedWith(withChild(withRecyclerView())));
        }
        Espresso.onView(withRecyclerView()).check(hasWidthInDp(73));
    }

    @Test
    public void checkAppearance1IsShown() {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        Espresso.onView(withRecyclerView()).perform(scrollToPosition(4));
        Espresso.onView(recyclerViewHolderAt(4)).perform(click());
        Espresso.onView(withVideoView()).perform(waitFourSeconds());
        Espresso.onView(withVideoView()).perform(click());
        assertTrue(getSessionClientRequests().contains("GET", "/clips/5/appearances?milliseconds="));
        Matcher<View> image = withContentDescription("Personaje en clip");
        Matcher<View> text = withText("Bart");
        Espresso.onView(image).check(isTopAlignedWith(withChild(image)));
        Espresso.onView(image).check(isCenteredOnParent());
        Espresso.onView(image).check(hasWidthAndHeightInDp(56));
        Espresso.onView(text).check(isBottomAlignedWith(withChild(text)));
        Espresso.onView(text).check(isCenteredOnParent());
        Espresso.onView(text).check(isCompletelyBelow(image));
    }

    private TextView getChildTextView(ConstraintLayout c) {
        for (int i = 0; i<c.getChildCount(); i++) {
            View item = c.getChildAt(i);
            if (item instanceof TextView) {
                return (TextView) item;
            }
        }
        return null;
    }

    private ImageView getChildImageView(ConstraintLayout c) {
        for (int i = 0; i<c.getChildCount(); i++) {
            View item = c.getChildAt(i);
            if (item instanceof ImageView) {
                return (ImageView) item;
            }
        }
        return null;
    }

    private boolean densityIndependantCompareTextSize(float expectedPointsSize, float actualPixelsSize, Context context) {
        float actualSize = actualPixelsSize / context.getResources().getDisplayMetrics().scaledDensity;
        return Math.abs(expectedPointsSize - actualSize) < 1;
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

    private static ViewAssertion hasWidthInDp(int size) {
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
                            widthEquals(view, sizeInPixels),
                            is(true));

                }
            }
        };
    }

    private static boolean widthEquals(View v, float value) {
        if (!(v.getParent() instanceof View)) {
            return false;
        }
        return Math.abs(v.getWidth() - value) < 1;
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
                    assertThat(
                            description.toString(),
                            leftToParentEqualsRightToParent(view),
                            is(true));

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
