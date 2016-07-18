package com.carouselview.yj.carouselviewlibrary;

import android.content.Context;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

/**
 * Created by wangyijin on 2016/7/13.
 *
 */
public class CarouselViewScroller extends Scroller{
    private int mDuration;

    public CarouselViewScroller(Context context, OvershootInterpolator overshootInterpolator) {
        super(context);
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, this.mDuration);
    }
}
