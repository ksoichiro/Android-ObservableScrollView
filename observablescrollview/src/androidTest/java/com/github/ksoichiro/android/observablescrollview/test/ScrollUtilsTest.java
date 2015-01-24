package com.github.ksoichiro.android.observablescrollview.test;

import android.graphics.Color;
import android.test.InstrumentationTestCase;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import junit.framework.Assert;

public class ScrollUtilsTest extends InstrumentationTestCase {

    public void testGetFloat() {
        Assert.assertEquals(1.0f, ScrollUtils.getFloat(1, 0, 2));
        assertEquals(0.0f, ScrollUtils.getFloat(-1, 0, 2));
        assertEquals(2.0f, ScrollUtils.getFloat(3, 0, 2));
    }

    public void testGetColorWithAlpha() {
        assertEquals(Color.parseColor("#00123456"), ScrollUtils.getColorWithAlpha(0, Color.parseColor("#FF123456")));
        assertEquals(Color.parseColor("#FF123456"), ScrollUtils.getColorWithAlpha(1, Color.parseColor("#FF123456")));
    }

    public void testMixColors() {
        assertEquals(Color.parseColor("#000000"), ScrollUtils.mixColors(Color.parseColor("#000000"), Color.parseColor("#FFFFFF"), 0));
    }
}
