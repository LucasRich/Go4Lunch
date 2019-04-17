package com.lucas.go4lunch.Views;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayRestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmates_item_img_profile) ImageView profileImg;
    @BindView(R.id.workmates_item_desc) TextView descTxt;

    public DisplayRestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    public void updateWithdisplayRestaurant(User user) {
        Glide.with(itemView).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(profileImg);
        descTxt.setText(user.getUsername() + " is joining!");
    }
}
