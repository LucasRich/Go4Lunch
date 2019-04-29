package com.lucas.go4lunch.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.PlaceStreams;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WorkmatesViewViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmates_item_desc) TextView descTxt;
    @BindView(R.id.workmates_item_img_profile) ImageView profileImage;

    private Disposable disposable;

    public WorkmatesViewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    public void updateWhitWorkmates(User user){
        Glide.with(itemView).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(profileImage);

        if (user.getDayRestaurant().equals("none")){
            descTxt.setText(user.getUsername() + " " + itemView.getResources().getString(R.string.no_decided));
            descTxt.setTextColor(0xff9b9b9b);
        } else {
            this.executeHttpRequestWithRetrofit(user);
        }
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(User user){
        this.disposable = PlaceStreams.streamFetchPlaceDetails(user.getDayRestaurant())
                .subscribeWith(new DisposableObserver<PlaceDetails>(){

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(PlaceDetails response) {
                        descTxt.setTextColor(0xFF000000);
                        descTxt.setText(user.getUsername() + " " + itemView.getResources().getString(R.string.decided) + " " + response.getResult().getName() +
                                " (" + response.getResult().getTypes().get(0) + ")");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
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
}
