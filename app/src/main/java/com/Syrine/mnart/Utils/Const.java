package com.Syrine.mnart.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Syrine.mnart.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public  class Const {

    public  static final String BASE_URL="http://localhost:3000/";



    public Const() {
    }

    public String generateDateTime(String Date, String Time) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        java.util.Date date = format.parse(Date);
        return df.format(date)+" "+Time;
    }


    public String generateDate(String Date) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        java.util.Date date = format.parse(Date);
        return df.format(date);
    }


    public static String generateDateTimeFromTimestamp(String timestamp) throws ParseException {
        long date = Long.parseLong(timestamp);
        Timestamp ts = new Timestamp(date);
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
        Date dat = new Date(ts.getTime());
        return df.format(dat);
    }


    public static String generateElapsedTimee(String startTimestamp) {
        long CurrentTimestamp = System.currentTimeMillis();
        long StartTimestamp = Long.parseLong(startTimestamp);
        Log.d("TimeStamPPPPPPPP : ","CurrentTimestamp: "+CurrentTimestamp+"  StartTimestamp: "+StartTimestamp);

        //long elapsedTimeSeconds = Math.round((labelTimestamp - startTimestamp) / RunReviewFragment.MILLIS_IN_A_SECOND);
        long elapsedTimeSeconds = ((CurrentTimestamp - (StartTimestamp * 1000L)) / 1000);


        if (elapsedTimeSeconds < 0) {
            // String resource: Localization for negative values?
            return "-" + DateUtils.formatElapsedTime(-1 * elapsedTimeSeconds);
        }
        return DateUtils.formatElapsedTime(elapsedTimeSeconds);
    }

    public static String generateElapsedTime(String startTimestamp) {
        long CurrentTimestamp = System.currentTimeMillis();
        long StartTimestamp = (Long.parseLong(startTimestamp));
        CharSequence relativeTimeStr = DateUtils.getRelativeTimeSpanString(StartTimestamp,
                CurrentTimestamp, DateUtils.FORMAT_ABBREV_RELATIVE, DateUtils.FORMAT_ABBREV_ALL);
        return  relativeTimeStr.toString();

    }

    /***
     *
     * Animation methods
     *
     */




    public static void setViewBackroundRippleEffect(View v, Activity activity){
        v.setClickable(true);
        v.setFocusable(true);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = activity.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        v.setBackgroundResource(backgroundResource);
    }




}






