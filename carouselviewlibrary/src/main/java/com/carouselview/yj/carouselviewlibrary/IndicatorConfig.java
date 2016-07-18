package com.carouselview.yj.carouselviewlibrary;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Created by wangyijin on 2016/7/5.
 * 管理底部CarouselView 底部Tag
 */
public class IndicatorConfig {
    private Context mContext;
    /**
     * 默认tag 间距（遵循黄金分割）
     */
    private float mDefaultTagSpacing;

    private FrameLayout mFlIndicatorRoot;
    private ViewStub mVsCenterIndicator, mVsTextIndicator, mVsGoldenSectionIndicator;
    /**
     * 内容描述控件
     * 如果未使用该对应的id实例化，或者没有使用默认提供的指示器（TEXT_TAG）
     * 则值可能为null
     */
    private TextView mTvDescription;
    /**
     * 装载tag的容器
     * 如果试图替换指示器UI并未使用对应的id，则值可能为null
     */
    private LinearLayout mLlTag;


    /**
     * The current Indicator layout resource
     */
    private View mCurrentIndicator;
    /**
     * 选中tag
     */
    private TagView mOldTagSelected;
    /**
     * 描述TextView id
     * (目前固定的控件为TextView)
     */
    private final int DESCRIPTION_VIEW_ID = R.id.tvDescription;
    /**
     * 装载tag LinearLayout id
     * (目前固定的控件为LinearLayout)
     */
    private final int TAG_VIEW_ID = R.id.llTag;

    /**
     * 底部指示器的布局
     */
    public int mIndicatorLayout;
    /**
     * 底部指示器的类型
     * 默认提供三种类型：
     * 0(center_tag) tag 居中显示
     * 1（text_tag）  带有文字说明的tag,但文字默认居左，tag居右 。文字框与tag的宽度按黄金风格比划分
     * 2（goldenSection_tag） 只有tag且tag左边的起始位置为按黄金分割划分
     */
    public int mIndicatorModel;
    /**
     * 小圆标的直径大小
     */
    public int mTagDiameter;
    /**
     * 选中时的的tag背景
     */
    public int mTagSelectedColor;
    /**
     * 未选中时的的tag背景
     */
    public int mTagUnselectedColor;
    /**
     * 指示器背景
     */
   public Drawable mIndicatorBackaground;
    /**
     * 内边距
     */
    public int mIndicatorPadding;
    public int mIndicatorPaddingLeft;
    public int mIndicatorPaddingTop;
    public int mIndicatorPaddingRight;
    public int mIndicatorPaddingBottom;

    /**
     * 描述字体文字颜色
     */
    public ColorStateList mDescriptionTextColor;
    /**
     * 描述字体文字大小
     */
    public int mDescriptionTextSize=13;

    public IndicatorConfig(Context context) {
        this.mContext = context;
    }

    /**
     * 初始化指示器布局资源
     * @param carouselView CarouselView 布局资源
     */
    public void initIndicatorLayout(View carouselView) {
        if (carouselView != null) {
            mFlIndicatorRoot = (FrameLayout) carouselView.findViewById(R.id.flIndicatorRoot);
            mVsCenterIndicator = (ViewStub) carouselView.findViewById(R.id.vsDefaultIndicator);
            mVsTextIndicator = (ViewStub) carouselView.findViewById(R.id.vsTextIndicator);
            mVsGoldenSectionIndicator = (ViewStub) carouselView.findViewById(R.id.vsGoldenSectionIndicator);
//            mTagSelected = (TagView) carouselView.findViewById(R.id.tagSelected);

            if (mIndicatorLayout == CarouselView.DEFAULT_INT) {//如果没有替换指示器布局
                if (mIndicatorModel == CarouselView.TEXT_TAG) {//使用文字描述指示器
                    mCurrentIndicator = mVsTextIndicator.inflate();
                    mTvDescription = (TextView) mCurrentIndicator.findViewById(DESCRIPTION_VIEW_ID);
                } else if (mIndicatorModel == CarouselView.GOLDEN_SECTION_TAG) {//tag居左
                    mCurrentIndicator = mVsGoldenSectionIndicator.inflate();
                } else {//默认样式——tag 居中
                    mCurrentIndicator = mVsCenterIndicator.inflate();
                }
                mLlTag = (LinearLayout) mCurrentIndicator.findViewById(TAG_VIEW_ID);
            } else {//如果替换指示器布局
                mCurrentIndicator = LayoutInflater.from(mContext).inflate(mIndicatorLayout, null, false);
                //由于无法预测替换指示器布局所要显示的具体意图，所以这里仅尝试实例化描述控件和Tag容器
                mTvDescription = (TextView) mCurrentIndicator.findViewById(DESCRIPTION_VIEW_ID);
                mLlTag = (LinearLayout) mCurrentIndicator.findViewById(TAG_VIEW_ID);
                mFlIndicatorRoot.addView(mCurrentIndicator);
            }
            setIndicatorBackaground(mIndicatorBackaground);
            setIndicatorPadding(mIndicatorPaddingLeft, mIndicatorPaddingTop, mIndicatorPaddingRight, mIndicatorPaddingBottom);
            setDescriptionTextColor(mDescriptionTextColor != null ? mDescriptionTextColor : ColorStateList.valueOf(0xFF000000));
            setDescriptionTextSize(TypedValue.COMPLEX_UNIT_PX,mDescriptionTextSize);
        }
    }

    /**
     * 创建指示器tag标签
     * @param realItemCount
     */
    public void createTag(int realItemCount) {
        if(mLlTag==null){
            return;
        }
        mLlTag.setGravity(Gravity.CENTER);
        if (realItemCount > 1) {
            TagView tag;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mTagDiameter, mTagDiameter);
            mDefaultTagSpacing = mTagDiameter / CarouselView.GOLDEN_SECTION;//tag水平间距
            params.setMargins((int) (mDefaultTagSpacing / 2), 0, (int) (mDefaultTagSpacing / 2), 0);
            for (int i = 0; i < realItemCount; i++) {
                tag = new TagView(mContext);
                tag.setId(i);
                if(i==0){
                    mOldTagSelected=tag;
                    tag.setmColor(mTagSelectedColor);
                }else{
                    tag.setmColor(mTagUnselectedColor);
                }

                mLlTag.addView(tag, i, params);
            }
        } else if (realItemCount == 1) {

        } else {
//            if (mTagSelected != null) {
//                mTagSelected.setVisibility(View.GONE);
//            }
        }
    }

    /**
     * 设置指示器背景
     * @param backaground
     */
    public void setIndicatorBackaground(Drawable backaground){
        if(mFlIndicatorRoot!=null&&backaground!=null){
            mFlIndicatorRoot.setBackgroundDrawable(backaground);
        }
    }

    /**
     * 设置内边距
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setIndicatorPadding(int left,int top,int right,int bottom){
        if(mFlIndicatorRoot!=null){
            mFlIndicatorRoot.setPadding(left, top, right, bottom);
        }
    }
    /**
     * 设置选中项
     * @param item
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     *                     (暂时未提供该相应功能)
     */
   public void setCurrentTag(int item, boolean smoothScroll){
       if(mLlTag==null){
           return;
       }
       if((item+1)<=mLlTag.getChildCount()&&item>=0){
            if(mLlTag.getChildAt(item) instanceof TagView){
               TagView newSelectedTag=(TagView) mLlTag.getChildAt(item);
                newSelectedTag.setmColor(mTagSelectedColor,true);
                mOldTagSelected.setmColor(mTagUnselectedColor, true);
                mOldTagSelected=newSelectedTag;
            }
       }
   }

    private void setDescriptionTextSize(int unit,float textSize){
        if(mTvDescription!=null){
            mTvDescription.setTextSize(unit,textSize);
        }
    }

    public void setDescriptionTextSize(float textSize){
        if(mTvDescription!=null){
            mTvDescription.setTextSize(textSize);
        }
    }
    public void setDescriptionTextColor(ColorStateList colors){
        if(mTvDescription!=null&&colors!=null){
            mTvDescription.setTextColor(colors);
        }
    }
    public void setTextDescription(String content){
        if(mTvDescription!=null){
            mTvDescription.setText(content);
        }
    }

    public FrameLayout getIndicatorRootLayout(){
        return mFlIndicatorRoot;
    }
}
