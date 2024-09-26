package com.afundacion.fp.library;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T018ThirdFragment {
    private static Class cls = null;

    @BeforeClass
    public static void getActivityClass() throws ClassNotFoundException {
        cls = Class.forName("com.afundacion.fp.library.MonstersActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkFragment3ClassExists() throws ClassNotFoundException {
        Class fragment3Cls = Class.forName("com.afundacion.fp.library.Fragment3");
    }

    @Test
    public void checkCorrectImageShownAfterClick() {
        Espresso.onView(withBottomMenuItem("item3")).perform(click());
        Matcher<View> contentDescription = withContentDescription("Un último Digimon poderoso");
        Espresso.onView(contentDescription).check(matches(withDrawable(R.drawable.birdramon)));
    }

    @Test
    public void checkCorrectImageShownAfterClickWithCorrectScaleType() {
        Espresso.onView(withBottomMenuItem("item3")).perform(click());
        Matcher<View> contentDescription = withContentDescription("Un último Digimon poderoso");
        Espresso.onView(contentDescription).check(matches(withScaleType(ImageView.ScaleType.FIT_XY)));
    }

    @Test
    public void checkCorrectImageShownAfterClickAndNotOther() {
        Matcher<View> contentDescription1 = withContentDescription("Un Digimon poderoso");
        Matcher<View> contentDescription2 = withContentDescription("Un último Digimon poderoso");
        Espresso.onView(contentDescription1).check(matches(isDisplayed()));
        Espresso.onView(withBottomMenuItem("item3")).perform(click());
        Espresso.onView(contentDescription2).check(matches(withDrawable(R.drawable.birdramon)));
        Espresso.onView(contentDescription1).check(doesNotExist());
    }

    /**
     * // TODO: Find a better solution
     * @param itemId The toString method will be matched against this parameter
     */
    private static TypeSafeMatcher<View> withBottomMenuItem(String itemId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof BottomNavigationItemView)) {
                    return false;
                }
                BottomNavigationItemView itemView = (BottomNavigationItemView) item;
                return itemView.toString().contains(itemId);
            }

            @Override
            public void describeTo(Description description) {}
        };
    }

    private static TypeSafeMatcher<View> withScaleType(ImageView.ScaleType scaleType) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof ImageView)) {
                    return false;
                }
                ImageView imageView = (ImageView) item;
                return imageView.getScaleType() == scaleType;
            }

            @Override
            public void describeTo(Description description) {}
        };
    }
    // https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f#.zem2ltpr7
    private static TypeSafeMatcher<View> withDrawable(int resId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof ImageView)){
                    return false;
                }
                ImageView imageView = (ImageView) target;
                Resources resources = target.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(resId);
                if (expectedDrawable == null) {
                    return false;
                }
                Bitmap bitmap = getBitmap(imageView.getDrawable());
                Bitmap otherBitmap = getBitmap(expectedDrawable);
                return bitmap.sameAs(otherBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(resId);
            }
        };
    }

    private static Bitmap getBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
