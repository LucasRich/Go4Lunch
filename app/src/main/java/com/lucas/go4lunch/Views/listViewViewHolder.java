package com.lucas.go4lunch.Views;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;

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

    private Disposable disposable;
    private int rate = 0;
    private Context context;

    private Location currentLocation = new Location("");
    private Location restaurantLocation = new Location("");

    public listViewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        SharedPref.init(context);
    }

    public void displayRestaurant(Result response){
        this.executeHttpRequestWithRetrofit(response.getPlaceId());
    }

    private void executeHttpRequestWithRetrofit(String placeId){

        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId)
                .subscribeWith(new DisposableObserver<PlaceDetails>(){
                    @Override
                    public void onNext(PlaceDetails response) {
                        Log.e("TAG","On Next");

                        restaurantName.setText(response.getResult().getName());
                        restaurantInfo.setText(response.getResult().getTypes().get(0) + " - " + response.getResult().getVicinity());

                        if (response.getResult().getPhotos() != null){
                            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" +
                                    response.getResult().getPhotos().get(0).getPhotoReference() +
                                    "&key=AIzaSyBWZx1xMJnhvXntblI-fLoNmZY64Gu2deY";

                            Glide.with(itemView).load(imageUrl).into(restaurantImg);
                        }
                        else { Glide.with(itemView).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac" +
                                "/No_image_available.svg/1024px-No_image_available.svg.png").into(restaurantImg);
                        }

                        if(response.getResult().getOpeningHours() != null){
                            if (response.getResult().getOpeningHours().getOpenNow()){
                                restaurantOpenInfo.setText("Open now");
                            }
                            else {
                                restaurantOpenInfo.setText("Close now");
                            }
                        }

                        if (response.getResult().getRating() != null){
                            rate = response.getResult().getRating().intValue();
                        }

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

                        currentLocation.setLatitude(Double.valueOf(SharedPref.read(SharedPref.currentPositionLat, "")));
                        currentLocation.setLongitude(Double.valueOf(SharedPref.read(SharedPref.currentPositionLng, "")));

                        restaurantLocation.setLatitude(response.getResult().getGeometry().getLocation().getLat());
                        restaurantLocation.setLongitude(response.getResult().getGeometry().getLocation().getLng());

                        restaurantDistance.setText(Math.round(currentLocation.distanceTo(restaurantLocation)) + "m");
                    }

                    @Override public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override public void onComplete() { Log.e("TAG","On Complete !!"); }
                });
    }
}
