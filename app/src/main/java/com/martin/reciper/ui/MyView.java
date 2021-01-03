package com.martin.reciper.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MyView extends View
{
    public static final int NOTEXECUTED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;

    private int state = NOTEXECUTED;

    public MyView(Context context) {super(context);}
    public MyView(Context context, @Nullable AttributeSet attrs) {super(context, attrs);}
    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint;

        switch(state)
        {
            case SUCCESS:
                paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(20f);
                canvas.drawLine(0, height,width/2, 0, paint);
                canvas.drawLine(width/2, 0, width, height, paint);
                canvas.drawLine(width, height-10, 0, height-10, paint);
                paint.setTextSize(100);
                canvas.drawText("12", 80, 200, paint);
                break;
            case FAILED:
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(20f);
                canvas.drawLine(0,0, width, height, paint);
                canvas.drawLine(0, height, width, 0, paint);
                break;
            default:
                break;
        }
    }

    public int getState() {return state;}
    public void setState(int state) {this.state = state; invalidate();}
}
