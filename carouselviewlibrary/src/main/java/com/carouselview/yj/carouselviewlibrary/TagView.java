package com.carouselview.yj.carouselviewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by wangyijin on 2016/7/5.
 */
public class TagView extends View {
    private Paint mPaint;
    private int mColor;

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        float cX, cY, radius;
        cX = cY = radius = Math.min(getWidth(), getHeight()) / 2;
        canvas.drawCircle(cX, cY, radius, mPaint);
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        setmColor(mColor, false);

    }
    public void setmColor(int mColor,boolean redraw) {
        this.mColor = mColor;
        if(redraw){
            invalidate();
        }

    }
}
