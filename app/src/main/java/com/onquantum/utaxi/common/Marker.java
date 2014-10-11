package com.onquantum.utaxi.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.onquantum.utaxi.R;

/**
 * Created by Admin on 10/10/14.
 */
public class Marker extends View {
    private Bitmap marker;
    float markerWidth = 0;
    float markerHeight = 0;

    public Marker(Context context, AttributeSet attrs) {
        super(context, attrs);
        marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        markerWidth = marker.getWidth();
        markerHeight = marker.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(
                marker,
                ((canvas.getWidth() / 2) - (markerWidth / 2)),
                (canvas.getHeight() / 2 - markerHeight),
                null
        );
    }
}
