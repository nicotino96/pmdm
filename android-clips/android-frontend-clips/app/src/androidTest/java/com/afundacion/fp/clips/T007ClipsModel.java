package com.afundacion.fp.clips;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T007ClipsModel extends TestRun {
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
    public void checkMethodExistsAndSendsRequestAndShowsSnackBar() throws NoSuchMethodException, InterruptedException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertTrue(getSessionClientRequests().contains("GET", "/clips"));
        rule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Method getClips = null;
                try {
                    getClips = cls.getDeclaredMethod("getClipsForTest");
                    Object clips = getClips.invoke(activity);
                    activityClips = clips;
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        assertNotNull(activityClips);
        // ClipsList
        Class listCls = Class.forName("com.afundacion.fp.clips.ClipsList");
        Method getListMethod = listCls.getDeclaredMethod("getClips");
        List<Object> list = (List<Object>) getListMethod.invoke(activityClips);
        // Clip
        Class clipCls = Class.forName("com.afundacion.fp.clips.Clip");
        clipCls.getDeclaredField("id"); // Ignore result; just test it exists
        clipCls.getDeclaredField("title"); // Ignore result; just test it exists
        clipCls.getDeclaredField("urlVideo"); // Ignore result; just test it exists
        Method getId = clipCls.getDeclaredMethod("getId");
        for (int i = 0; i < list.size(); i++) {
            Object clip = list.get(i);
            int actualId = (int) getId.invoke(clip);
            assertEquals(i+1, actualId);
        }
    }
}
