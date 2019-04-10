package com.lucas.go4lunch.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import com.lucas.go4lunch.R;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "Go4Lunch";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SharedPref.read(SharedPref.notificationAllow, false)){

        }
        else {
            System.out.println("Notification denied");
        }
    }
}
