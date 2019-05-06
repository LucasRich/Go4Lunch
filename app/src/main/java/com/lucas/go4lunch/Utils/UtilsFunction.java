package com.lucas.go4lunch.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lucas.go4lunch.Controllers.Activities.MainActivity;
import com.lucas.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class UtilsFunction {

    public static float getAverage(List<Long> listRate){

        long total = 0;
        for (long rate : listRate){
            total += rate;
        }
        float average = (float) total / listRate.size();
        return average;
    }

    public static void restartMainActivity(Context context) {
        Intent myIntent = new Intent(context, MainActivity.class);
        context.startActivity(myIntent);
    }

    public static void displayStars (float rate, ImageView oneStar, ImageView twoStars, ImageView threeStars){
        if (rate < 0.5){
            oneStar.setImageResource(R.drawable.ic_star_empty_48dp);
            twoStars.setImageResource(R.drawable.ic_star_empty_48dp);
            threeStars.setImageResource(R.drawable.ic_star_empty_48dp);
        }

        if (rate > 0.5){
            oneStar.setImageResource(R.drawable.ic_star_48dp);
            twoStars.setImageResource(R.drawable.ic_star_empty_48dp);
            threeStars.setImageResource(R.drawable.ic_star_empty_48dp);
        }

        if (rate > 1.5){
            oneStar.setImageResource(R.drawable.ic_star_48dp);
            twoStars.setImageResource(R.drawable.ic_star_48dp);
            threeStars.setImageResource(R.drawable.ic_star_empty_48dp);
        }

        if (rate > 2.5){
            oneStar.setImageResource(R.drawable.ic_star_48dp);
            twoStars.setImageResource(R.drawable.ic_star_48dp);
            threeStars.setImageResource(R.drawable.ic_star_48dp);
        }
    }

    /*private void getRating(String placeId, String averageRate){

        List<Long> listRate = new ArrayList();

        UserHelper.getUsersCollection()
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    UserHelper.getUsersCollection()
                            .document(document.getData().get("uid").toString())
                            .collection("rate")
                            .document(placeId)
                            .get().addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            DocumentSnapshot document1 = task1.getResult();
                            if (document1.exists()) {
                                listRate.add((Long) document1.getData().get("rate"));
                                averageRate = UtilsFunction.getAverage(listRate);
                                UtilsFunction.displayStars(averageRate, oneStar, twoStars, threeStars);
                            } else {
                                if (listRate.size() == 0){ UtilsFunction.displayStars(0, oneStar, twoStars, threeStars); }
                            }
                        } else {
                            System.out.println("get failed with " + task1.getException());
                        }
                    });
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });

    }

    public static void startNotificationAtMidday(){
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
