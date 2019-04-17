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
    private Context cont;

    @Override
    public void onReceive(Context context, Intent intent) {

        cont = context;

        /*if (SharedPref.read(SharedPref.notificationAllow, false)){

        }
        else {
            System.out.println("Notification denied");
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Ce midi vous mangez au Toc (bar restaurant Lille)");
        inboxStyle.addLine("4 Boulevard du Maréchal Vaillant, 59000 Lille")
                .addLine("Pierre, Paul, Jack y mangent également !");

        // 3 - Create a Channel (Android 8)
        String channelId = cont.getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(cont, channelId)
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setContentTitle(cont.getString(R.string.app_name))
                        .setContentText(cont.getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());*/
    }
}
