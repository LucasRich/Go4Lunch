package com.lucas.go4lunch.Utils;

import com.lucas.go4lunch.Models.NearbySearch.NearbySearch;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceService {

    @GET("nearbysearch/json?&radius=300&type=restaurant&key=AIzaSyBWZx1xMJnhvXntblI-fLoNmZY64Gu2deY")
    Observable<NearbySearch> getNearbySearch(@Query("location") String location);

    @GET("details/json?&key=AIzaSyBWZx1xMJnhvXntblI-fLoNmZY64Gu2deY")
    Observable<PlaceDetails> getPlaceDetails(@Query("placeid") String placeId);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}