package com.lucas.go4lunch.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lucas.go4lunch.Controllers.Activities.MainActivity;
import com.lucas.go4lunch.Models.PlaceDetails.Location;

public class SharedPref {

    private static SharedPreferences mSharedPref;
    public static String currentPositionLat = "currentPositionLat";
    public static String currentPositionLng = "currentPositionLng";
    public static String radius = "radius";
    public static String notificationAllow = "notificationAllow";
    public static String currentLanguage = "currentLanguage";
    public static String dayRestaurant = "dayRestaurant";

    private SharedPref() { }

    public static void init(Context context) {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public static String getCurrentPosition () {
        String result = SharedPref.read(currentPositionLat, "") + "," + SharedPref.read(currentPositionLng, "");
        return result;
    }
}
