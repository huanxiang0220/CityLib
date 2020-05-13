package com.mystery.businessanim;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


/**
 * 作者:  tang
 * 时间： 2020/5/13 0013 下午 3:30
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
public class BusinessAnim {

    private IBusinessAnim anim;

    public BusinessAnim(View mShoppingCart, View mTabLayout, ViewGroup main_container, AnimatorListener listener) {
        anim = new BusinessImp(mShoppingCart, mTabLayout, main_container, listener);
    }

    public void addToShoppingCar(Context context, View startView) {
        anim.addToShoppingCar(context, startView);
    }

}