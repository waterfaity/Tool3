package com.waterfairy.tool3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorView extends View {


    private String[] colors = {
            "#faca02", "#dca932", "#f16f02", "#ffe8d0", "#ffa6a1",
            "#ec3907", "#b93800", "#d43265", "#955740", "#633630",
            "#25211c", "#a0a0a0", "#ffffff", "#5a1973", "#7d41a5",
            "#340f6f", "#03549f", "#0088e2", "#7cdcff",
            "#38b38c", "#017e43", "#00b03f", "#6dc134", "#fbfb00",
    };
    private float anglePer = 360 / 24F;


    public ColorView(Context context) {
        this(context, null);
    }

    Paint paint = new Paint();

    public ColorView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < colors.length; i++) {
            paint.setColor(Color.parseColor(colors[i]));
            canvas.drawArc(0, 0, getWidth(), getHeight(), anglePer * i, anglePer, true, paint);
        }
    }
}
