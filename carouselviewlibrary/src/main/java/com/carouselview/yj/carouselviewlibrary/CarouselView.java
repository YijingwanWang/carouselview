package com.carouselview.yj.carouselviewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.carouselview.yj.carouselviewlibrary.transformer.DepthPageTransformer;
import com.carouselview.yj.carouselviewlibrary.transformer.ZoomOutPageTransformer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangyijin on 2016/6/28.
 */
public class CarouselView extends RelativeLayout implements ViewPager.OnPageChangeListener {
    /**
     * 1dp 所对应的像素——即1dp的像素表达
     */
    private final float ONE_DP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

    private final Object mCarouselViewLock = new Object();
    /**
     * 黄金分割比例
     */
    public static final float GOLDEN_SECTION = 0.618f;


    private final int CAROUSE_SIZE_MULTIPLE = 100;
    /**
     * 某方向上无限循环
     */
    public static final int CAROUSE_TYPE_INFINITE = 0;
    /**
     * 默认，有限循环
     */
    public static final int CAROUSE_TYPE_FINITE = 1;
    /**
     * 默认Tag的大小——直径
     */
    private final int DEFAULT_TAG_DIAMETER = (int) (10 * ONE_DP);

    /**
     * 页面切换默认速度
     */
    public static final int DEFAULT_CAROUSEL_SPEED=1000;
    /**
     * 默认选中tag颜色
     */
    private final int DEFAULT_TAG_SELECTEDCOLOR = 0xFFFF8800;
    /**
     * 默认非选中tag 颜色
     */
    private final int DEFAULT_TAG_UNSELECTEDCOLOR = 0xFFCCCCCC;
    /**
     * 默认页面切换动画
     */
    public static final int DEFAULT_TRANSFORMER = 0;
    /**
     * 页面切换动画
     */
    public static final int ZOOM_OUT = 1;
    /**
     * 页面切换动画
     */
    public static final int DEPTH = 2;

    /**
     * tag 居中显示
     */
    public static final int CENTER_TAG = 0;
    /**
     * 带有文字说明的tag,但文字默认居左，tag居右 。文字框与tag的宽度按黄金风格比划分
     */
    public static final int TEXT_TAG = 1;
    /**
     * 只有tag且tag左边的起始位置为按黄金分割划分
     */
    public static final int GOLDEN_SECTION_TAG = 2;


    public static final int DEFAULT_INT = -1;
    /**
     * 默认间隔时间
     */
    private final int DEFAULT_ROTATION_INTERVAL = 3000;

    /**
     * 轮播类型
     * 0可以朝一个方向无限循环，1有限循环
     */
    private int mCarouseType;

    /**
     * viewpager 子页面间切换 动画类型
     * 0默认切换动画，
     * 1 zoomOut，2 depth ——使用官方范例中的动画详见：
     * https://developer.android.com/training/animation/screen-slide.html
     */
    private int mPageTransformer;
    /**
     * 轮播间隔时间
     */
    private int mRotationInterval;
    /**
     * 页面切换时间
     */
    private int mCarouselSpeed;

    /**
     * 当前是否正在轮播
     */
    private boolean mIsCurrentCarousel = false;
    private LayoutInflater mLi;
    private ViewPager mVgCarousel;
    private String[] mDescriptionContent;

    private OnCarouselViewPageChangeListener mOnCarouselViewPageChangeListener;

    private IndicatorConfig mIndicatorConfig;

    private CarouselAdapter mCarouselAdapter;


    private OnItemClickListener mOnItemClickListener;

    /**
     * 使用定时器周期性切换轮播图
     */
    private Timer timer;

    private TimerTask task;

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public ViewPager getmVgCarousel() {
        return mVgCarousel;
    }

    public OnCarouselViewPageChangeListener getmOnCarouselViewPageChangeListener() {
        return mOnCarouselViewPageChangeListener;
    }

    public void addmOnCarouselViewPageChangeListener(OnCarouselViewPageChangeListener mOnCarouselViewPageChangeListener) {
        this.mOnCarouselViewPageChangeListener = mOnCarouselViewPageChangeListener;
    }

    /**
     * 数据初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mIndicatorConfig = new IndicatorConfig(getContext());

        initAttrs(attrs, defStyleAttr);
        initLayout(context);
    }

    /**
     * 属性初始化
     *
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.carouselViewArrtibute, defStyleAttr, 0);
        mCarouseType = array.getInt(R.styleable.carouselViewArrtibute_carouselType, CAROUSE_TYPE_INFINITE);
        mIndicatorConfig.mTagDiameter = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_tag_diameter, DEFAULT_TAG_DIAMETER);
        mIndicatorConfig.mTagSelectedColor = array.getColor(R.styleable.carouselViewArrtibute_tag_selectedColor, DEFAULT_TAG_SELECTEDCOLOR);
        mIndicatorConfig.mTagUnselectedColor = array.getColor(R.styleable.carouselViewArrtibute_tag_unselectedColor, DEFAULT_TAG_UNSELECTEDCOLOR);
        mPageTransformer = array.getInt(R.styleable.carouselViewArrtibute_pageTransformer, DEFAULT_TRANSFORMER);
        mIndicatorConfig.mIndicatorLayout = array.getResourceId(R.styleable.carouselViewArrtibute_indicatorLayout, DEFAULT_INT);
        mIndicatorConfig.mIndicatorModel = array.getInt(R.styleable.carouselViewArrtibute_indicatorModel, CENTER_TAG);
        mRotationInterval = array.getInt(R.styleable.carouselViewArrtibute_rotationInterval, DEFAULT_ROTATION_INTERVAL);
        mIndicatorConfig.mIndicatorBackaground = array.getDrawable(R.styleable.carouselViewArrtibute_indicator_backaground);
        mIndicatorConfig.mIndicatorPadding = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_indicator_padding, 0);
        if (mIndicatorConfig.mIndicatorPadding == 0) {
            mIndicatorConfig.mIndicatorPaddingLeft = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_indicator_paddingLeft, 0);
            mIndicatorConfig.mIndicatorPaddingTop = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_indicator_paddingTop, 0);
            mIndicatorConfig.mIndicatorPaddingRight = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_indicator_paddingRight, 0);
            mIndicatorConfig.mIndicatorPaddingBottom = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_indicator_paddingBottom, 0);
        } else {
            //如果同时设置
            mIndicatorConfig.mIndicatorPaddingLeft = mIndicatorConfig.mIndicatorPadding;
            mIndicatorConfig.mIndicatorPaddingTop = mIndicatorConfig.mIndicatorPadding;
            mIndicatorConfig.mIndicatorPaddingRight = mIndicatorConfig.mIndicatorPadding;
            mIndicatorConfig.mIndicatorPaddingBottom = mIndicatorConfig.mIndicatorPadding;
        }
        mIndicatorConfig.mDescriptionTextColor = array.getColorStateList(R.styleable.carouselViewArrtibute_descriptionTextColor);
        mIndicatorConfig.mDescriptionTextSize = array.getDimensionPixelSize(R.styleable.carouselViewArrtibute_descriptionTextSize, mIndicatorConfig.mDescriptionTextSize);
        mCarouselSpeed = array.getInt(R.styleable.carouselViewArrtibute_carouselSpeed, DEFAULT_CAROUSEL_SPEED);
        array.recycle();
    }

    /**
     * 布局初始化
     * 如果同时设置了 indicatorLayout属性（mIndicatorLayout），indicatorModel属性（mIndicatorModel）
     * 则优先考虑使用indicatorLayout 替换指示器布局，indicatorModel次之
     *
     * @param context
     */
    private void initLayout(Context context) {
        mLi = LayoutInflater.from(context);
        View carouselView = mLi.inflate(R.layout.carousel_view_layout, this);
        mVgCarousel = (ViewPager) carouselView.findViewById(R.id.vgCarousel);
        if (mPageTransformer == ZOOM_OUT) {
            mVgCarousel.setPageTransformer(true, new ZoomOutPageTransformer());
        } else if (mPageTransformer == DEPTH) {
            mVgCarousel.setPageTransformer(true, new DepthPageTransformer());
        }
        mIndicatorConfig.initIndicatorLayout(carouselView);//初始化指示器布局
        setCarouselSpeed(mCarouselSpeed);
        mVgCarousel.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnCarouselViewPageChangeListener != null) {
            mOnCarouselViewPageChangeListener.onCarouselPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        int nextPosition = position % getCarouselCount();
        mIndicatorConfig.setCurrentTag(nextPosition, true);
        //填充文字描述
        if (mDescriptionContent != null) {
            if (nextPosition >= 0 && nextPosition <= (mDescriptionContent.length - 1)) {
                mIndicatorConfig.setTextDescription(mDescriptionContent[nextPosition]);
            } else {
                mIndicatorConfig.setTextDescription("");
            }
        }
        setUnDestroyItem(position);
        if (mOnCarouselViewPageChangeListener != null) {
            mOnCarouselViewPageChangeListener.onCarouselPageSelected(nextPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //当切换动画完成后再启动自动切换程序
        if(mIsCurrentCarousel){
            if(state== ViewPager.SCROLL_STATE_SETTLING){
                touchStopCarousel();
            }else if(state== ViewPager.SCROLL_STATE_IDLE){
                touchStartCarousel();
            }
        }
        if (mOnCarouselViewPageChangeListener != null) {
            mOnCarouselViewPageChangeListener.onCarouselPageScrollStateChanged(state);
        }
    }

    /**
     * 计算并设置不销毁的选中项
     */
    private void setUnDestroyItem(int position) {
        if (mCarouselAdapter == null) {
            return;
        }
        int unDestroyItem = -1;
        if (mCarouseType == CAROUSE_TYPE_INFINITE) {
            int p = position % getCarouselCount();
            unDestroyItem = position - p + mCarouselAdapter.getRealCount();
        }
        mCarouselAdapter.setUnDestroyItem(unDestroyItem);
    }
    /**
     * 轮播图混动切换接口
     */
    public interface OnCarouselViewPageChangeListener {
        public void onCarouselPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onCarouselPageSelected(int position);

        public void onCarouselPageScrollStateChanged(int state);
    }

    /**
     * 设置item点击事件
     */
    public interface OnItemClickListener {
        public void onItemClick(int absolutelyPosition, int relativePosition, View view);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * 设置item监听事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        if (mCarouselAdapter != null && mCarouselAdapter.getViewList() != null) {
            List<View> viewList = mCarouselAdapter.getViewList();
            for (int i = 0; i < viewList.size(); i++) {
                final int relativePosition = i;
                View item = viewList.get(i);
                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mVgCarousel.getCurrentItem(), relativePosition, v);
                    }
                });
            }
        }
    }

    /**
     * 设置默认的Adapter
     * 调用该方法CarouselView 会使用自己的Adapter
     * {@link CarouselAdapter}
     *
     * @param views 具体要展示的内容集合
     */
    public void setItemData(List<View> views) {
        if (views != null && views.size() > 0) {
            int countItem = views.size();
            int currentItem = 0;
            initAdapter(views);
            if (mCarouseType == CAROUSE_TYPE_INFINITE) {//如果是无限循环则在数据集的首位
                countItem = views.size() * CAROUSE_SIZE_MULTIPLE;
                currentItem = countItem / 2;
            }
            mCarouselAdapter.setmCount(countItem);
            mVgCarousel.setAdapter(mCarouselAdapter);
            mVgCarousel.setCurrentItem(currentItem);//默认选中项为中间值
            mIndicatorConfig.createTag(getCarouselCount());
        }
    }

    /**
     * 设置当前选中的item
     *
     * @param item
     * @param smoothScroll (目前设置没有任何效果)
     */
    public void setCurrentSelectedItem(int item, boolean smoothScroll) {
        if ((item + 1) > getCarouselCount() || item < 0) {
            throw new ArrayIndexOutOfBoundsException("默认选中项 item 不在数据集合边界内");
        }
        //当前选中项
        int currentItem = mVgCarousel.getCurrentItem();
        int i = currentItem % getCarouselCount();
        if (item < i) {
            mVgCarousel.setCurrentItem(currentItem - Math.abs(i - item), smoothScroll);
        } else if (item > i) {
            mVgCarousel.setCurrentItem(currentItem + Math.abs(i - item), smoothScroll);
        }
    }

    /**
     * 设置当前选中项
     *
     * @param item
     */
    public void setCurrentSelectedItem(int item) {
        setCurrentSelectedItem(item, true);
    }

    /**
     * 实例化Adapter
     */
    private void initAdapter(List<View> views) {
        if (mCarouselAdapter == null) {
            mCarouselAdapter = new CarouselAdapter(views);
        }
    }

    private PagerAdapter getAdapter() {
        return mCarouselAdapter;
    }

    /**
     * 获得轮播图的实际个数
     */
    public int getCarouselCount() {
        if (mCarouselAdapter != null) {
            return mCarouselAdapter.getRealCount();
        }
        return 0;
    }

    /**
     * 获得当前在数据集合中实际选中项位置
     *
     * @return
     */
    public int getRelPositionInDataSet() {
        return mVgCarousel.getCurrentItem() % getCarouselCount();
    }

    private class CarouselTimerTask extends TimerTask {
        @Override
        public void run() {
            mVgCarousel.post(new Runnable() {
                @Override
                public void run() {

                    //当前在实际数据集合中的显示的item的位置
                    int currentItem = mVgCarousel.getCurrentItem();
                    if (mCarouselAdapter != null && mCarouselAdapter.getCount() == (currentItem - 1)) {
                        currentItem = mCarouselAdapter.getCount() / 2;
                        mVgCarousel.setCurrentItem(currentItem, false);
                        return;
                    }

                    int i = currentItem % getCarouselCount();
                    if (i >= 0 && i < (getCarouselCount() - 1)) {
                        currentItem++;
                        mVgCarousel.setCurrentItem(currentItem, true);
                    } else if (i == (getCarouselCount() - 1)) {
                        currentItem -= i;
                        mVgCarousel.setCurrentItem(currentItem, false);
                    }
                }
            });
        }
    }

    /**
     * 开启轮播
     * 确保该方法在 setItemData(List<View> views)方法之后调用
     */
    public void startCarousel() {
        synchronized (mCarouselViewLock) {
            mIsCurrentCarousel = true;
            touchStartCarousel();
        }
    }

    /**
     * 当触摸事件结束的时候恢复切换状态
     */
    private void touchStartCarousel() {
        if (mIsCurrentCarousel) {
            Log.d("----","touchStartCarousel");
            if (timer == null) {
                timer = new Timer();
            }
            task = new CarouselTimerTask();
            timer.schedule(task, mRotationInterval, mRotationInterval);

        }
    }

    /**
     * 暂停轮播
     */
    public void stopCarousel() {
        synchronized (mCarouselViewLock) {
            touchStopCarousel();
            mIsCurrentCarousel = false;
        }
    }

    /**
     * 当发生触摸事件时停止自动切换
     */
    private void touchStopCarousel() {
        if (mIsCurrentCarousel) {
            Log.d("----","touchStopCarousel");
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            if (task != null) {
                task.cancel();
            }
        }
    }

    /**
     * 设置文字描述集合
     *
     * @param content
     */
    public void setDescriptionContent(String[] content) {
        if (content != null && content.length > 0) {
            this.mDescriptionContent = content;
            if (getRelPositionInDataSet() < mDescriptionContent.length) {
                mIndicatorConfig.setTextDescription(mDescriptionContent[getRelPositionInDataSet()]);
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mIsCurrentCarousel) {//正在或已经开启自动切换
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    touchStopCarousel();
                    break;
                case MotionEvent.ACTION_UP:
                    touchStartCarousel();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * @return 返回指示器根容器
     */
    public FrameLayout getIndicatorRootLayout() {
        if (mIndicatorConfig != null) {
            return mIndicatorConfig.getIndicatorRootLayout();
        }
        return null;
    }

    /**
     * 使用反射设置ViewPager页面切换速度
     *
     * @param speed
     */
    public void setCarouselSpeed(int speed) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            CarouselViewScroller carouselViewScroller = new CarouselViewScroller(mVgCarousel.getContext(), new OvershootInterpolator(0.6F));
            carouselViewScroller.setDuration(speed);
            field.set(mVgCarousel, carouselViewScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
