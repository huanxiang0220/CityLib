package com.mystery.businessanim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


/**
 * 作者:  tang
 * 时间： 2020/5/13 0013 下午 3:46
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
public class BusinessImp implements IBusinessAnim {

    private View mShoppingCart;
    private View mTabLayout;
    private ViewGroup main_container;
    private AnimatorListener listener;

    BusinessImp(View mShoppingCart, View mTabLayout, ViewGroup main_container,AnimatorListener listener) {
        this.mShoppingCart = mShoppingCart;
        this.mTabLayout = mTabLayout;
        this.main_container = main_container;
        this.listener = listener;
    }

    @Override
    public void addToShoppingCar(Context context, View startView) {
        // 起点
        int[] startPosition = new int[2];
        startView.getLocationOnScreen(startPosition);

        // 终点
        int[] endPosition = new int[2];
        mShoppingCart.getLocationInWindow(endPosition);

        // 贝塞尔控制点
        int[] controlPosition = new int[2];
        // 当前位置
        final float[] currentPosition = new float[2];

        // tablayout位置
        int[] tabLayoutPosition = new int[2];

        mTabLayout.getLocationInWindow(tabLayoutPosition);

        startPosition[1] = startPosition[1] - mTabLayout.getHeight() + DensityUtil.dip2px(context, 10);

        // 终点进行一下居中处理
        endPosition[0] = endPosition[0] + (mShoppingCart.getWidth() / 2);
        controlPosition[0] = endPosition[0];
        controlPosition[1] = startPosition[1];


        final ImageView imageView = new ImageView(context);
        main_container.addView(imageView);
        imageView.setImageResource(R.drawable.ic_plus_by_oval_bg);
        imageView.setVisibility(View.VISIBLE);
        imageView.setX(startPosition[0]);
        imageView.setY(startPosition[1]);

        Path path = new Path();
        path.moveTo(startPosition[0], startPosition[1]);
        path.quadTo(controlPosition[0], controlPosition[1],
                endPosition[0] - DensityUtil.dip2px(context, 15),
                endPosition[1] - DensityUtil.dip2px(context, 25));
        final PathMeasure pathMeasure = new PathMeasure();

        // false表示path路径不闭合
        pathMeasure.setPath(path, false);

        // ofFloat是一个生成器
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value, currentPosition, null);
                imageView.setX(currentPosition[0]);
                imageView.setY(currentPosition[1]);
            }
        });
        valueAnimator.start();

        ObjectAnimator shoppingCartX = ObjectAnimator.ofFloat(
                mShoppingCart, "scaleX", 1.0f, 1.3f, 1.0f);
        ObjectAnimator shoppingCartY = ObjectAnimator.ofFloat(
                mShoppingCart, "scaleY", 1.0f, 1.3f, 1.0f);
        shoppingCartX.setInterpolator(new AccelerateInterpolator());
        shoppingCartY.setInterpolator(new AccelerateInterpolator());
        AnimatorSet shoppingCart = new AnimatorSet();
        shoppingCart
                .play(shoppingCartX)
                .with(shoppingCartY);
        shoppingCart.setDuration(400);
        shoppingCart.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.setVisibility(View.GONE);
                main_container.removeView(imageView);
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

}