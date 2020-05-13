package com.mystery;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Dimension;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.glc.magiclib.R;
import com.mystery.buildins.UIUtil;
import com.mystery.buildins.commonnavigator.CommonNavigator;
import com.mystery.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import com.mystery.buildins.commonnavigator.abs.IPagerIndicator;
import com.mystery.buildins.commonnavigator.abs.IPagerTitleView;
import com.mystery.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.mystery.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import com.mystery.buildins.commonnavigator.titles.SimplePagerTitleView;
import com.mystery.buildins.commonnavigator.titles.badge.BadgeAnchor;
import com.mystery.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import com.mystery.buildins.commonnavigator.titles.badge.BadgeRule;

import java.util.List;

/**
 * 作者:  tang
 * 时间： 2018/10/11 0011 上午 10:31
 * 邮箱： 3349913147@qq.com
 * 描述： 将ViewPager与MagicIndicator绑定
 */

public class BindMagicUtil {

    private MagicParams mMagicParams;

    private BindMagicUtil(MagicParams magicParams) {
        this.mMagicParams = magicParams;
    }

    private List<String> titles;
    private CommonNavigator commonNavigator;

    private void bindViewPager() {
        if (commonNavigator == null) {
            commonNavigator = new CommonNavigator(mMagicParams.mContext);
            commonNavigator.setAdjustMode(mMagicParams.mAdjustMode);
            commonNavigator.setAdapter(new CommonNavigatorAdapter() {

                @Override
                public int getCount() {
                    return titles != null ? titles.size() : 0;
                }

                @Override
                public IPagerTitleView getTitleView(Context context, final int index) {
                    ColorTransitionPagerTitleView pagerTitleView;
                    if (mMagicParams.isCustomPageView) {
                        pagerTitleView = mMagicParams.customPageView.getPageView(context);
                    } else {
                        pagerTitleView = new ScaleTransitionPagerTitleView(context);
                    }

                    if (mMagicParams.mMinWidth > 0)
                        pagerTitleView.setMinWidth(mMagicParams.mMinWidth);
                    else if (mMagicParams.mLRPadding > 0) {
                        int padding = UIUtil.dip2px(context, mMagicParams.mLRPadding);
                        pagerTitleView.setPadding(padding, 0, padding, 0);
                    }
                    pagerTitleView.setText(titles.get(index));
                    pagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMagicParams.mTextSize);
                    pagerTitleView.setNormalColor(mMagicParams.mNormalColor);
                    pagerTitleView.setSelectedColor(mMagicParams.mSelectedColor);
                    pagerTitleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mMagicParams.mCheckable) return;
                            OnTabSelectedListener mListener = mMagicParams.mListener;
                            if (mListener != null) {
                                if (index == mMagicParams.mViewPager.getCurrentItem())
                                    mListener.onTabReselected(index);
                                else
                                    mListener.onTabSelected(index);
                            }
                            mMagicParams.mViewPager.setCurrentItem(index);
                        }
                    });
                    //支持角标
                    BadgePagerListener badgeListener = mMagicParams.badgePagerListener;
                    if (badgeListener != null) {
                        BadgePagerTitleView badgePagerView = badgeListener.
                                getPageView(context, index, pagerTitleView, mMagicParams);
                        badgePagerView.setInnerPagerTitleView(pagerTitleView);
                        return badgePagerView;
                    }
                    return pagerTitleView;
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    if (!mMagicParams.isIndicatorEnable) return null;
                    LinePagerIndicator indicator = new LinePagerIndicator(context);
                    indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                    indicator.setLineHeight(UIUtil.dip2px(context, 3));
                    indicator.setLineWidth(UIUtil.dip2px(context, 30));
                    indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                    indicator.setStartInterpolator(new AccelerateInterpolator());
                    indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                    indicator.setColors(Color.parseColor("#894292"));
                    return indicator;
                }
            });
            mMagicParams.mMagicIndicator.setNavigator(commonNavigator);
            ViewPagerHelper.bind(mMagicParams.mMagicIndicator, mMagicParams.mViewPager);
        }
    }

    public void bind(List<String> titles) {
        bind(titles, 0);
    }

    public void bind(final List<String> titles, int position) {
        this.titles = titles;
        bindViewPager();
        commonNavigator.notifyDataSetChanged();
        mMagicParams.mMagicIndicator.onPageSelected(position);
    }

    public static class Builder {
        private MagicParams magicParams;

        public Builder(Context context) {
            magicParams = new MagicParams();
            //默认
            magicParams.mContext = context;
            magicParams.mTextSize = UIUtil.dip2px(context, 16);
            magicParams.mNormalColor = Color.parseColor("#222222");
            magicParams.mSelectedColor = Color.parseColor("#894292");
        }

        public Builder customPageView(CustomPageView customPageView) {
            magicParams.isCustomPageView = true;
            magicParams.customPageView = customPageView;
            return this;
        }

        public Builder badgePageView(BadgePagerListener listener) {
            magicParams.badgePagerListener = listener;
            return this;
        }

        public Builder setMinWidth(int minPixels) {
            magicParams.mMinWidth = minPixels;
            return this;
        }

        public Builder setLRPadding(int lrPadding) {
            magicParams.mLRPadding = lrPadding;
            return this;
        }

        public Builder setTextSize(@Dimension int textSize) {
            magicParams.mTextSize = textSize;
            return this;
        }

        public Builder setTextColor(int mNormalColor, int mSelectedColor) {
            magicParams.mSelectedColor = mSelectedColor;
            magicParams.mNormalColor = mNormalColor;
            return this;
        }

        public Builder setTextNormalColor(int mNormalColor) {
            magicParams.mNormalColor = mNormalColor;
            return this;
        }

        public Builder setTextSelectColor(int mSelectedColor) {
            magicParams.mSelectedColor = mSelectedColor;
            return this;
        }

        public Builder setListener(OnTabSelectedListener listener) {
            magicParams.mListener = listener;
            return this;
        }

        public Builder setIndicatorEnable(boolean indicatorEnable) {
            magicParams.isIndicatorEnable = indicatorEnable;
            return this;
        }

        public Builder setAdjustMode(boolean mAdjustMode) {
            magicParams.mAdjustMode = mAdjustMode;
            return this;
        }

        public Builder setCheckable(boolean checkable) {
            magicParams.mCheckable = checkable;
            return this;
        }

        /**
         * 创建对象
         */
        public BindMagicUtil create(ViewPager viewPager, MagicIndicator magicIndicator) {
            magicParams.mViewPager = viewPager;
            magicParams.mMagicIndicator = magicIndicator;
            return new BindMagicUtil(magicParams);
        }
    }

    /**
     * 绑定的参数
     */
    public static class MagicParams {
        Context mContext;
        int mMinWidth = -1;
        int mLRPadding = -1;
        int mTextSize;
        int mSelectedColor;
        int mNormalColor;
        OnTabSelectedListener mListener;
        boolean isIndicatorEnable = true;
        boolean mAdjustMode = false;
        boolean mCheckable = true;
        ViewPager mViewPager;
        MagicIndicator mMagicIndicator;

        CustomPageView customPageView;
        boolean isCustomPageView;
        //支持角标
        BadgePagerListener badgePagerListener;
    }

    public interface CustomPageView {
        ColorTransitionPagerTitleView getPageView(Context context);
    }

    public interface BadgePagerListener {
        BadgePagerTitleView getPageView(Context context, int index, SimplePagerTitleView titleView, MagicParams params);

        SparseArray<BadgePagerTitleView> getPositions();
    }

    /**
     * 带颜色渐变和缩放的指示器标题
     */
    private static class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
        private float mMinScale = 0.9375f;

        public ScaleTransitionPagerTitleView(Context context) {
            super(context);
        }

        @Override
        public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
            super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
            setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
            setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
        }

        @Override
        public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
            super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
            setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
            setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int position);

        void onTabReselected(int position);
    }

    public static class OnTabSelectedListenerImp implements OnTabSelectedListener {

        @Override
        public void onTabSelected(int position) {

        }

        @Override
        public void onTabReselected(int position) {

        }
    }


    //----------------------解决角标---------
    public static BadgePagerListener BADGE = new BadgePagerListener() {

        private SparseArray<BadgePagerTitleView> mPositions = new SparseArray<>();

        @Override
        public SparseArray<BadgePagerTitleView> getPositions() {
            return mPositions;
        }

        @Override
        public BadgePagerTitleView getPageView(Context context, int index, SimplePagerTitleView titleView, MagicParams params) {
            BadgePagerTitleView badgeView = new BadgePagerTitleView(context);
            mPositions.put(index, badgeView);

            // setup badge
            TextView badgeTextView = (TextView) View.inflate(context, R.layout.simple_count_badge_layout, null);
            badgeTextView.setText("0");
            badgeView.setBadgeView(badgeTextView);

            // set badge position
            badgeView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, -5));
            badgeView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));

//        if (index == 0) {
//            badgeView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_LEFT, -UIUtil.dip2px(mContext, 6)));
//            badgeView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));
//        } else if (index == 1) {
//            badgeView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, 0));
//            badgeView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));
//        } else if (index == 2) {
//            badgeView.setXBadgeRule(new BadgeRule(BadgeAnchor.CENTER_X, -UIUtil.dip2px(mContext, 3)));
//            badgeView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_BOTTOM, UIUtil.dip2px(mContext, 2)));
//        }

            // don't cancel badge when tab selected
            badgeView.setAutoCancelBadge(false);
            return badgeView;
        }
    };
//--------------------------------

}