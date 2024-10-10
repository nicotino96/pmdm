package com.afundacion.fp.clips;

import static org.junit.Assert.assertTrue;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class T010ClipViewHolder {

    @Test
    public void checkClipViewHolderClassIsCorrect() throws NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
        Class clipClass = Class.forName("com.afundacion.fp.clips.ClipViewHolder");
        clipClass.getDeclaredConstructor(View.class);
        clipClass.getDeclaredField("cellTitle");
        assertTrue(clipClass.getSuperclass().toString().contains("ViewHolder"));
    }
}

