package com.afundacion.fp.clips;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T009RecyclerViewCell {
    private static Class cls = null;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException {
       cls = Class.forName("com.afundacion.fp.clips.MainActivity");
    }

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule(cls);

    @Test
    public void checkRecyclerViewCellXmlIsCorrect() {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int id = c.getResources().getIdentifier("recycler_view_cell", "layout", "com.afundacion.fp.clips");
        assertNotEquals(0, id);
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                View c = activity.getLayoutInflater().inflate(id, null);
                assertTrue(c instanceof ConstraintLayout);
                ConstraintLayout cell = (ConstraintLayout) c;
                assertEquals(1, cell.getChildCount());
                View child = cell.getChildAt(0);
                assertTrue(child instanceof TextView);
                TextView textView = (TextView) child;
                assertNotEquals(-1, textView.getId());
                assertTrue(densityIndependantCompareTextSize(24, textView.getTextSize(), activity));
            }
        });
    }

    private boolean densityIndependantCompareTextSize(float expectedPointsSize, float actualPixelsSize, Context context) {
        float actualSize = actualPixelsSize / context.getResources().getDisplayMetrics().scaledDensity;
        return Math.abs(expectedPointsSize - actualSize) < 1;
    }
}
