package com.lucas.go4lunch.Utils;

import com.lucas.go4lunch.Models.NearbySearch.NearbySearch;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlaceStreams {

    public static Observable<NearbySearch> streamFetchNearbySearch(String location, int radius){
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);
        return placeService.getNearbySearch(location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetails> streamFetchPlaceDetails(String placeId){
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);
        return placeService.getPlaceDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetails> streamFetchPlaceIdAndFetchDetails(String location, int radius){
        return streamFetchNearbySearch(location, radius)
                .concatMapIterable((Function<NearbySearch, Iterable<Result>>) NearbySearch::getResults)
                .concatMap((Function<Result, Observable<PlaceDetails>>) result -> streamFetchPlaceDetails(result.getPlaceId()));
    }
}
