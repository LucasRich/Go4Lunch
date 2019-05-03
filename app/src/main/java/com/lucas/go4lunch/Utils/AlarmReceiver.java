package com.lucas.go4lunch.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lucas.go4lunch.Controllers.Activities.BaseActivity;
import com.lucas.go4lunch.Controllers.Activities.DisplayRestaurantInfo;
import com.lucas.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "Go4Lunch";
    private Context cont;
    private List<String> listUser = new ArrayList<>();
    private String restaurantId;

    @Override
    public void onReceive(Context context, Intent intent) {

        cont = context;
        restaurantId = SharedPref.read(SharedPref.dayRestaurant, "");

        String restaurantName = SharedPref.read(SharedPref.notificationRestaurantName, "");
        String address = SharedPref.read(SharedPref.notificationRestaurantAddress, "");

        if (SharedPref.read(SharedPref.notificationAllow, false) && SharedPref.read(SharedPref.getNotificationActived, true)){
            Query query = UserHelper.getAllUserRestaurant(restaurantId);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!this.getCurrentUser().getUid().equals(String.valueOf(document.getData().get("uid")))){
                            listUser.add(String.valueOf(document.getData().get("username")));
                        }
                    }
                    StringBuilder peopleRestaurant = new StringBuilder();

                    for (int i = 0; i < listUser.size(); i++){
                            peopleRestaurant.append(listUser.get(i)).append(", ");
                    }

                    Intent mIntent = new Intent(cont, DisplayRestaurantInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.bundleKeyPlaceId, restaurantId);
                    mIntent.putExtras(bundle);
                    PendingIntent pendingIntent = PendingIntent.getActivity(cont, 0, mIntent, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                    if (listUser.size() > 0){
                        inboxStyle.setBigContentTitle(cont.getString(R.string.restaurant_lunch) + " " + restaurantName);
                        inboxStyle.addLine(address)
                                .addLine(peopleRestaurant + cont.getString(R.string.also_eat));
                    } else {
                        inboxStyle.setBigContentTitle(cont.getString(R.string.restaurant_lunch) + restaurantName);
                        inboxStyle.addLine(address);
                    }


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
                                    .setContentIntent(pendingIntent)
                                    .setStyle(inboxStyle);

                    // 5 - Add the Notification to the Notification Manager and show it.
                    NotificationManager notificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);

                    // 6 - Support Version >= Android 8
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence channelName = "Notification";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }

                    // 7 - Show notification
                    notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());

                }
            });
        }
        else {
            System.out.println("Notification denied");
        }
    }

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

}
