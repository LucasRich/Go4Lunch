package com.lucas.go4lunch.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class UtilsFunction {

    public static float getAverage(List<Long> listRate){

        long total = 0;
        for (long rate : listRate){
            total += rate;
        }
        float average = (float) total / listRate.size();
        return average;
    }

    /*public static void startNotificationAtMidday(){
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        SharedPref.write(SharedPref.notificationRestaurantName, nameOfRestaurant);
        SharedPref.write(SharedPref.notificationRestaurantAddress, addressOfRestaurant);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,SharedPref.read(SharedPref.notificationHour, 12));
        calendar.set(Calendar.MINUTE, SharedPref.read(SharedPref.notificationMin, 0));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }*/
}
