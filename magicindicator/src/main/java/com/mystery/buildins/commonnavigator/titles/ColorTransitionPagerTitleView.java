package com.mystery.buildins.commonnavigator.titles;

import android.content.Context;
import android.graphics.Typeface;

import com.mystery.buildins.ArgbEvaluatorHolder;

/**
 * 两种颜色过渡的指示器标题
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class ColorTransitionPagerTitleView extends SimplePagerTitleView {

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    private boolean isBoldText = false;

    public void setBoldText(boolean boldText) {
        isBoldText = boldText;
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
//        getPaint().setFakeBoldText(false);
        setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
//        getPaint().setFakeBoldText(true);
        setTextColor(color);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (isBoldText) {
            setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (isBoldText) {
            setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

}
