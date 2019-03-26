package com.lucas.go4lunch.Views.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Views.listViewViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class listViewAdapter extends RecyclerView.Adapter<listViewViewHolder> {

    //FOR DATA
    private List<Result> response;

    //CONSTRUCTOR
    public listViewAdapter(List<Result> response){
        this.response = response;
    }

    @Override
    public listViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_view_item, parent, false);

        return new listViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listViewViewHolder viewHolder, int position) {
        viewHolder.displayRestaurant(this.response.get(position));
    }

    @Override
    public int getItemCount() {
        return this.response.size();
    }

    public Result getArticle(int position){
        return this.response.get(position);
    }
}
