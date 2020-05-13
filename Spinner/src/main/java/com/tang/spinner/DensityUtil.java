package com.tang.spinner;

import android.content.Context;
import android.util.TypedValue;

/**
 * @Function: TODO
 * @Author YangWei
 * @Date: 2015-11-2 下午3:19:59
 */
class DensityUtil {

    public static int getScreenHeigth(Context context) {

        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {

        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static float dp2px(Context context, float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }

    /**
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }

    /**
     * @param context
     * @param spValue
     * @return
     */
    public static float sp2px(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                context.getResources().getDisplayMetrics());

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
