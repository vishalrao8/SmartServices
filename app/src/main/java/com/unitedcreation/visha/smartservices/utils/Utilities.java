package com.unitedcreation.visha.smartservices.utils;

import android.content.Context;
import android.util.TypedValue;

public class Utilities {

    public static float pixels(Context context){

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
    }

}
