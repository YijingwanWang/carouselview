package com.carouselview.yj.carouselviewlibrary;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyijin on 2016/7/5.
 */
public class CarouselAdapter extends PagerAdapter {
    private final Object mLock = new Object();
    private int mCount;

    private List<View> mViewList;
    /**
     * 不销毁的item 项
     */
    private int mUnDestroyItem=-1;
    public CarouselAdapter() {
        this(null);
    }

    /**
     * 设置不销毁的item项
     * @param unDestroy
     */
    public void setUnDestroyItem(int unDestroy){
        if(unDestroy!=mUnDestroyItem){
            mUnDestroyItem=unDestroy;
        }
    }
    public CarouselAdapter(List<View> mViewList) {
        if (mViewList == null) {
            this.mViewList = new ArrayList<>();
        } else {
            this.mViewList = mViewList;
        }
    }

    public List<View> getViewList(){
      return   mViewList;
    }
    public void add(View view){
        synchronized (mLock){
            if (view != null) {
                this.mViewList.add(view);
            }
        }
        notifyDataSetChanged();
    }

    public void addAll(List<View> viewList){
        synchronized (mLock){
            if (viewList != null&&viewList.size()>0) {
                this.mViewList.addAll(viewList);
            }
        }
        notifyDataSetChanged();
    }

    public void setmCount(int count){
        synchronized (mLock){
            this.mCount=count;
        }
    }

    /**
     * 获取实际数据集合size
     * @return
     */
    public int getRealCount(){
        return mViewList.size();
    }
    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View childView = mViewList.get(position % mViewList.size());
        ViewGroup parent = (ViewGroup) childView.getParent();
        if (parent != null) {
            parent.removeView(childView);
        }
        container.addView(childView);
        return childView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(mUnDestroyItem!=-1&&position==mUnDestroyItem){
            return;
        }
        View viewChild = mViewList.get(position % mViewList.size());
        container.removeView(viewChild);

    }
}
