package com.lucas.go4lunch.Utils;

import android.util.Log;
import android.widget.CheckBox;

import com.lucas.go4lunch.Models.NearbySearch.NearbySearch;
import com.lucas.go4lunch.Models.NearbySearch.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PlaceDetailSingleton {

    private static PlaceDetailSingleton instance;

    private PlaceDetailSingleton() {
    }

    List<Result> restaurant;
    private Disposable disposable;

    public static PlaceDetailSingleton getInstance() {
        if (instance == null) {
            instance = new PlaceDetailSingleton();
        }
        return instance;
    }

    public void executeHttpRequestWithRetrofit(){
        this.disposable = PlaceStreams.streamFetchNearbySearch(SharedPref.getCurrentPosition())
                .subscribeWith(new DisposableObserver<NearbySearch>(){
                    @Override
                    public void onNext(NearbySearch response) {
                        Log.e("TAG","On Next");

                        List<Result> dlRestaurant = response.getResults();
                        display(dlRestaurant);
                    }

                    @Override public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override public void onComplete() { Log.e("TAG","On Complete !!"); }
                });
    }

    private void display(List<Result> dlArticles){
        System.out.println(dlArticles.get(0).getPlaceId());
    }
}