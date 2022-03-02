package com.cs.android.usb;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/21 13:59
 **/
public class ScreenUtils {

    public static int screenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float screenXDpi(Context context) {
        return context.getResources().getDisplayMetrics().xdpi;
    }

    public static float screenYDpi(Context context) {
        return context.getResources().getDisplayMetrics().ydpi;
    }


    /**
     * dp转px
     */
    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float px) {
        return (px / context.getResources().getDisplayMetrics().scaledDensity);
    }
}
