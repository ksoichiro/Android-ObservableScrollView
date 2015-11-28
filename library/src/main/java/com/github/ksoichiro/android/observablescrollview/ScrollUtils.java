/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ksoichiro.android.observablescrollview;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Utilities for creating scrolling effects.
 */
public final class ScrollUtils {

    private ScrollUtils() {
    }

    /**
     * Return a float value within the range.
     * <p>This is just a wrapper for Math.min() and Math.max().
     * This may be useful if you feel it confusing ("Which is min and which is max?").</p>
     *
     * @param value    The target value.
     * @param minValue Minimum value. If value is less than this, minValue will be returned.
     * @param maxValue Maximum value. If value is greater than this, maxValue will be returned.
     * @return Float value limited to the range.
     */
    public static float getFloat(final float value, final float minValue, final float maxValue) {
        return Math.min(maxValue, Math.max(minValue, value));
    }

    /**
     * Create a color integer value with specified alpha.
     * <p>This may be useful to change alpha value of background color.</p>
     *
     * @param alpha     Alpha value from 0.0f to 1.0f.
     * @param baseColor Base color. alpha value will be ignored.
     * @return A color with alpha made from base color.
     */
    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    /**
     * Add an OnGlobalLayoutListener for the view.
     * <p>This is just a convenience method for using {@code ViewTreeObserver.OnGlobalLayoutListener()}.
     * This also handles removing listener when onGlobalLayout is called.</p>
     *
     * @param view     The target view to add global layout listener.
     * @param runnable Runnable to be executed after the view is laid out.
     */
    public static void addOnGlobalLayoutListener(final View view, final Runnable runnable) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                runnable.run();
            }
        });
    }

    /**
     * Mix two colors.
     * <p>{@code toColor} will be {@code toAlpha/1} percent,
     * and {@code fromColor} will be {@code (1-toAlpha)/1} percent.</p>
     *
     * @param fromColor First color to be mixed.
     * @param toColor   Second color to be mixed.
     * @param toAlpha   Alpha value of toColor, 0.0f to 1.0f.
     * @return Mixed color value in ARGB. Alpha is fixed value (255).
     */
    public static int mixColors(int fromColor, int toColor, float toAlpha) {
        float[] fromCmyk = ScrollUtils.cmykFromRgb(fromColor);
        float[] toCmyk = ScrollUtils.cmykFromRgb(toColor);
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = Math.min(1, fromCmyk[i] * (1 - toAlpha) + toCmyk[i] * toAlpha);
        }
        return 0xff000000 + (0x00ffffff & ScrollUtils.rgbFromCmyk(result));
    }

    /**
     * Convert RGB color to CMYK color.
     *
     * @param rgbColor Target color.
     * @return CMYK array.
     */
    public static float[] cmykFromRgb(int rgbColor) {
        int red = (0xff0000 & rgbColor) >> 16;
        int green = (0xff00 & rgbColor) >> 8;
        int blue = (0xff & rgbColor);
        float black = Math.min(1.0f - red / 255.0f, Math.min(1.0f - green / 255.0f, 1.0f - blue / 255.0f));
        float cyan = 1.0f;
        float magenta = 1.0f;
        float yellow = 1.0f;
        if (black != 1.0f) {
            // black 1.0 causes zero divide
            cyan = (1.0f - (red / 255.0f) - black) / (1.0f - black);
            magenta = (1.0f - (green / 255.0f) - black) / (1.0f - black);
            yellow = (1.0f - (blue / 255.0f) - black) / (1.0f - black);
        }
        return new float[]{cyan, magenta, yellow, black};
    }

    /**
     * Convert CYMK color to RGB color.
     * This method doesn't check if cmyk is not null or have 4 elements in array.
     *
     * @param cmyk Target CYMK color. Each value should be between 0.0f to 1.0f,
     *             and should be set in this order: cyan, magenta, yellow, black.
     * @return ARGB color. Alpha is fixed value (255).
     */
    public static int rgbFromCmyk(float[] cmyk) {
        float cyan = cmyk[0];
        float magenta = cmyk[1];
        float yellow = cmyk[2];
        float black = cmyk[3];
        int red = (int) ((1.0f - Math.min(1.0f, cyan * (1.0f - black) + black)) * 255);
        int green = (int) ((1.0f - Math.min(1.0f, magenta * (1.0f - black) + black)) * 255);
        int blue = (int) ((1.0f - Math.min(1.0f, yellow * (1.0f - black) + black)) * 255);
        return ((0xff & red) << 16) + ((0xff & green) << 8) + (0xff & blue);
    }
}
