package com.lucas.go4lunch.Controllers.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.lucas.go4lunch.Controllers.Activities.DisplayRestaurantInfo;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.ItemClickSupport;
import com.lucas.go4lunch.Utils.UserHelper;
import com.lucas.go4lunch.Views.Adapter.WorkmatesViewAdapter;
import com.lucas.go4lunch.Views.Adapter.listViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment implements WorkmatesViewAdapter.Listener {

    @BindView(R.id.workmates_fragment_error) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.workmates_recycler_view) RecyclerView recyclerView;

    private WorkmatesViewAdapter workmatesViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        return view;
    }

    // --------------------
    // CONFIGURATION
    // --------------------

    private void configureRecyclerView(){
        this.workmatesViewAdapter = new WorkmatesViewAdapter(generateOptionsForAdapter(UserHelper.getAllUser()),this);
        workmatesViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(workmatesViewAdapter.getItemCount());
             }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(this.workmatesViewAdapter);
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_workmates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    User userItem = workmatesViewAdapter.getItem(position);
                    if (userItem.getDayRestaurant().equals("none")){

                    } else {
                        Intent myIntent = new Intent(getActivity(), DisplayRestaurantInfo.class);
                        Bundle bundle = new Bundle();

                        bundle.putString(Constant.bundleKeyPlaceId, userItem.getDayRestaurant());

                        myIntent.putExtras(bundle);
                        startActivity(myIntent);
                    }
                });
    }

    // --------------------
    // UTILS
    // --------------------

    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(this.workmatesViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
