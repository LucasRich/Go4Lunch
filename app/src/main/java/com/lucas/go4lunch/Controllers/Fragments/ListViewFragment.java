package com.lucas.go4lunch.Controllers.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucas.go4lunch.Controllers.Activities.DisplayRestaurantInfo;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.ItemClickSupport;

import com.lucas.go4lunch.Models.NearbySearch.NearbySearch;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Views.Adapter.listViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    @BindView(R.id.fragment_list_view_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fragment_list_view_swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private List<Result> response;
    private listViewAdapter adapter;
    private Disposable disposable;

    public ListViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        SharedPref.init(getContext());

        this.configureRecyclerView();
        this.configureSwipeRefreshLayout();
        this.configureOnClickRecyclerView();
        this.executeHttpRequestWithRetrofit();

        return view;
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private void configureRecyclerView(){
        this.response = new ArrayList<>();
        this.adapter = new listViewAdapter(this.response);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_list_view)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Result response = adapter.getArticle(position);
                    Intent myIntent = new Intent(getActivity(), DisplayRestaurantInfo.class);
                    Bundle bundle = new Bundle();

                    bundle.putString(Constant.bundleKeyPlaceId, response.getPlaceId());

                    myIntent.putExtras(bundle);
                    startActivity(myIntent);
                });
    }

    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(this::executeHttpRequestWithRetrofit);
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        this.disposable = PlaceStreams.streamFetchNearbySearch(SharedPref.getCurrentPosition(), SharedPref.read(SharedPref.radius, 300))
                .subscribeWith(new DisposableObserver<NearbySearch>(){
                    @Override
                    public void onNext(NearbySearch response) {
                        Log.e("TAG","On Next");

                        List<Result> dlRestaurant = response.getResults();
                        updateUI(dlRestaurant);
                    }

                    @Override public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override public void onComplete() { Log.e("TAG","On Complete !!"); }
                });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // ------------------
    //  UI
    // ------------------

    private void updateUI(List<Result> dlArticles){
        swipeRefreshLayout.setRefreshing(false);
        response.clear();
        response.addAll(dlArticles);
        adapter.notifyDataSetChanged();
    }
}
