package com.hyundai.hackathon.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Cho on 2016-08-20.
 */
public class Util {
    public static int dp2px(int dp, Resources r){
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200,
                r.getDisplayMetrics());
        return px;
    }
}
