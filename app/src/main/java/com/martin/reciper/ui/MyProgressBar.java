package com.martin.reciper.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar
{
    int level = 0;

    public MyProgressBar(Context context)
    {
        super(context);
    }

    public MyProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int newWidth = (int)(level/100.0f)*width;

        Paint paint;
        paint = new Paint();
        paint.setColor(Color.rgb(0,level/100.0f, 0));

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(20f);
        canvas.drawRect(0,0, width, height, paint);
    }

    @Override
    public void setProgress(int progress)
    {
        super.setProgress(progress);
        level = progress;
        Log.i("daco", "som v setProgress: "+level);
        this.invalidate();
    }
}
