package com.mystery.businessanim;

import android.content.Context;
import android.util.TypedValue;

/**
 * 作者:  tang
 * 时间： 2020/5/13 0013 下午 3:33
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
class DensityUtil {
    static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }
}
