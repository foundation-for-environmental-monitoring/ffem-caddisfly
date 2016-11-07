/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly
 *
 * Akvo Caddisfly is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.caddisfly.sensor.colorimetry.strip.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

import org.akvo.caddisfly.util.detector.FinderPattern;

import java.util.List;

/**
 * Created by linda on 9/9/15
 */
public class FinderPatternIndicatorView extends SurfaceView {

    private final Paint paint;
    private List<FinderPattern> patterns;
    private int width;
    private int height;

    public FinderPatternIndicatorView(Context context) {
        this(context, null);
    }

    public FinderPatternIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FinderPatternIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public void showPatterns(List<FinderPattern> patternList, int width, int height) {

        patterns = patternList;
        this.width = width;
        this.height = height;

        invalidate();

    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    public void onDraw(Canvas canvas) {

        // Have the view being transparent
        canvas.drawARGB(0, 0, 0, 0);

        if (patterns != null) {
            //canvas has a rotation of 90 degrees in respect to the camera preview
            //Camera preview size is in landscape mode, canvas is in portrait mode
            //the width of the canvas corresponds to the height of the size,
            //the height of the canvas corresponds to the width of the size.
            float ratioW = (float) canvas.getWidth() / (float) height;
            float ratioH = (float) canvas.getHeight() / (float) width;

            for (int i = 0; i < patterns.size(); i++) {
                //The x of the canvas corresponds to the y of the pattern,
                //The y of the canvas corresponds to the x of the pattern.
                float x = canvas.getWidth() - patterns.get(i).getY() * ratioW;
                float y = patterns.get(i).getX() * ratioH;
                canvas.drawCircle(x, y, 10, paint);
            }
        }

        super.onDraw(canvas);
    }
}