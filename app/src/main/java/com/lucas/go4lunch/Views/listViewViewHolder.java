package com.lucas.go4lunch.Views;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lucas.go4lunch.BuildConfig;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class listViewViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_img) ImageView restaurantImg;
    @BindView(R.id.item_name) TextView restaurantName;
    @BindView(R.id.item_distance) TextView restaurantDistance;
    @BindView(R.id.item_info) TextView restaurantInfo;
    @BindView(R.id.item_open_info) TextView restaurantOpenInfo;
    @BindView(R.id.item_5_stars) ImageView fiveStars;
    @BindView(R.id.item_4_stars) ImageView fourStars;
    @BindView(R.id.item_3_stars) ImageView threeStars;
    @BindView(R.id.item_2_stars) ImageView twoStars;
    @BindView(R.id.item_1_star) ImageView oneStar;
    @BindView(R.id.item_nb_workmates) TextView nbWorkmates;
    @BindView(R.id.item_nb_workmates_img) ImageView nbWorkmatesImg;

    private Disposable disposable;
    private int rate = 0;
    private Context context;
    private List<String> weekdayOpen = new ArrayList<>();

    private Location currentLocation = new Location("");
    private Location restaurantLocation = new Location("");
    private List<String> listUser = new ArrayList<>();

    // -------------------
    // INIT
    // -------------------

    public listViewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        SharedPref.init(context);
    }

    public void displayRestaurant(Result response){
        //init
        nbWorkmates.setVisibility(itemView.INVISIBLE);
        nbWorkmatesImg.setVisibility(itemView.INVISIBLE);

        this.executeHttpRequestWithRetrofit(response.getPlaceId());
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(String placeId){
        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId)
                .subscribeWith(new DisposableObserver<PlaceDetails>(){
                    @Override
                    public void onNext(PlaceDetails response) {
                        Log.e("TAG","On Next");

                        displayRestaurantPeople(placeId);

                        restaurantName.setText(response.getResult().getName());
                        restaurantInfo.setText(response.getResult().getTypes().get(0) + " - " + response.getResult().getVicinity());

                        if (response.getResult().getPhotos() != null){
                            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" +
                                    response.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.ApiKey;

                            Glide.with(itemView).load(imageUrl).into(restaurantImg);
                        }
                        else { Glide.with(itemView).load("https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/No_image_available_600_x_450.svg/" +
                                "600px-No_image_available_600_x_450.svg.png").into(restaurantImg);
                        }

                        if(response.getResult().getOpeningHours() != null){
                            weekdayOpen.addAll(response.getResult().getOpeningHours().getWeekdayText());
                            displayOpeningHours(weekdayOpen, response);

                            /*if (response.getResult().getOpeningHours().getOpenNow()){
                                restaurantOpenInfo.setText("Open now");
                            }
                            else {
                                restaurantOpenInfo.setText("Close now");
                            }*/
                        }

                        if (response.getResult().getRating() != null){
                            rate = response.getResult().getRating().intValue();
                        }

                        displayStars(rate);
                        displayDistance(response.getResult().getGeometry().getLocation().getLat(),
                                        response.getResult().getGeometry().getLocation().getLng());

                    }

                    @Override public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override public void onComplete() { Log.e("TAG","On Complete !!"); }
                });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.disposeWhenDestroy();
    }

    // -------------------
    // UTILS
    // -------------------

    private void displayRestaurantPeople(String placeId){
        Query query = UserHelper.getAllUserRestaurant(placeId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int i = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    i++;
                }
                if (i > 0){
                    nbWorkmates.setVisibility(itemView.VISIBLE);
                    nbWorkmatesImg.setVisibility(itemView.VISIBLE);
                    nbWorkmates.setText("(" + String.valueOf(i) + ")");
                }
            }
        });
    }

    private void displayStars (int rate){
        switch(rate) {
            case 0:
                oneStar.setVisibility(itemView.INVISIBLE);
                twoStars.setVisibility(itemView.INVISIBLE);
                threeStars.setVisibility(itemView.INVISIBLE);
                fourStars.setVisibility(itemView.INVISIBLE);
                fiveStars.setVisibility(itemView.INVISIBLE);
                break;
            case 1:
                twoStars.setVisibility(itemView.INVISIBLE);
                threeStars.setVisibility(itemView.INVISIBLE);
                fourStars.setVisibility(itemView.INVISIBLE);
                fiveStars.setVisibility(itemView.INVISIBLE);
                break;
            case 2:
                threeStars.setVisibility(itemView.INVISIBLE);
                fourStars.setVisibility(itemView.INVISIBLE);
                fiveStars.setVisibility(itemView.INVISIBLE);
                break;
            case 3:
                fourStars.setVisibility(itemView.INVISIBLE);
                fiveStars.setVisibility(itemView.INVISIBLE);
                break;
            case 4:
                fiveStars.setVisibility(itemView.INVISIBLE);
                break;
        }
    }

    private void displayDistance (double latitude, double longitude){
        currentLocation.setLatitude(Double.valueOf(SharedPref.read(SharedPref.currentPositionLat, "")));
        currentLocation.setLongitude(Double.valueOf(SharedPref.read(SharedPref.currentPositionLng, "")));

        restaurantLocation.setLatitude(latitude);
        restaurantLocation.setLongitude(longitude);

        int distance = Math.round(currentLocation.distanceTo(restaurantLocation));
        DecimalFormat myFormatter = new DecimalFormat("#,###");

        if (distance < 1000){
            restaurantDistance.setText(distance + "m");
        }
        else {
            restaurantDistance.setText(myFormatter.format(distance) + "km");
        }
    }

    private void displayOpeningHours (List weekdayOpen, PlaceDetails response) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int dayNumber = 0;

        switch (day) {
            case Calendar.MONDAY:
                dayNumber = 1;
                break;
            case Calendar.TUESDAY:
                dayNumber = 2;
                break;
            case Calendar.WEDNESDAY:
                dayNumber = 3;
                break;
            case Calendar.THURSDAY:
                dayNumber = 4;
                break;
            case Calendar.FRIDAY:
                dayNumber = 5;
                break;
            case Calendar.SATURDAY:
                dayNumber = 6;
                break;
            case Calendar.SUNDAY:
                dayNumber = 0;
                break;
        }

        if (response.getResult().getOpeningHours().getOpenNow()){
            restaurantOpenInfo.setText(itemView.getResources().getString(R.string.open_now));
        }
        else {
            try {
                restaurantOpenInfo.setText(itemView.getResources().getString(R.string.open_until)+ " " + response.getResult().getOpeningHours().getPeriods().get(dayNumber).getOpen().getTime());
            }
            catch (Exception e){ restaurantOpenInfo.setText(itemView.getResources().getString(R.string.close)); }
        }
    }
}
