package com.lucas.go4lunch.Controllers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.PlaceDetailSingleton;
import com.lucas.go4lunch.Utils.PlaceStreams;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class DisplayRestaurantInfo extends AppCompatActivity {

    @BindView(R.id.img_restaurant) ImageView imgRestaurant;
    @BindView(R.id.name_restaurant) TextView nameRestaurant;
    @BindView(R.id.adress_restaurant) TextView adresseRestaurant;
    @BindView(R.id.item_5_stars) ImageView fiveStars;
    @BindView(R.id.item_4_stars) ImageView fourStars;
    @BindView(R.id.item_3_stars) ImageView threeStars;
    @BindView(R.id.item_2_stars) ImageView twoStars;
    @BindView(R.id.item_1_star) ImageView oneStar;

    PlaceDetailSingleton utils = PlaceDetailSingleton.getInstance();

    private View view;

    private Disposable disposable;
    private String phone_number;
    private String restaurantUrl;
    private String webSite;
    private int rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant_info);
        ButterKnife.bind(this);

        this.executeHttpRequestWithRetrofit();
    }

    // -------------------
    // ACTION
    // -------------------

    @OnClick(R.id.call_view)
    public void onClickCallView(){
        if (phone_number != null){
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + phone_number));
            startActivity(dialIntent);
        }
        else { Toast.makeText(getApplication(), "This restaurant doesn't have have phone number", Toast.LENGTH_LONG).show(); }
    }

    @OnClick(R.id.like_view)
    public void onClickLikeView(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantUrl));
        startActivity(intent);

        Toast.makeText(getApplication(), "You can rate and comment this restaurant on google map", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.website_view)
    public void onClickWebsiteView(){
        if (webSite != null){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
            startActivity(intent);
        }
        else { Toast.makeText(getApplication(), "This restaurant doesn't have website", Toast.LENGTH_LONG).show(); }

    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        Bundle bundle = getIntent().getExtras();
        String placeId = bundle.getString(Constant.bundleKeyPlaceId, "");

        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId)
                .subscribeWith(new DisposableObserver<PlaceDetails>(){
                    @Override
                    public void onNext(PlaceDetails response) {
                        Log.e("TAG","On Next");

                        nameRestaurant.setText(response.getResult().getName());
                        adresseRestaurant.setText(response.getResult().getTypes().get(0) + " - " + response.getResult().getVicinity());

                        if (response.getResult().getPhotos() != null){
                            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" +
                                                response.getResult().getPhotos().get(0).getPhotoReference() +
                                                    "&key=AIzaSyBWZx1xMJnhvXntblI-fLoNmZY64Gu2deY";

                            Glide.with(getApplicationContext()).load(imageUrl).into(imgRestaurant);
                        }
                        else {
                            Glide.with(getApplicationContext()).load("https://upload.wikimedia.org/wikipedia/commons/thumb/" +
                                    "a/ac/No_image_available.svg/1024px-No_image_available.svg.png").into(imgRestaurant);
                        }

                        if (response.getResult().getFormattedPhoneNumber() != null){
                            phone_number = response.getResult().getFormattedPhoneNumber();
                        }
                        else { phone_number = null; }

                        if (response.getResult().getWebsite() != null){
                            webSite = response.getResult().getWebsite();
                        }
                        else { webSite = null; }

                        restaurantUrl = response.getResult().getUrl();

                        if (response.getResult().getRating() != null){
                            rate = response.getResult().getRating().intValue();
                        }

                        displayStars(rate);
                    }

                    @Override public void onError(Throwable e) { Log.e("TAG","On Error"+Log.getStackTraceString(e)); }

                    @Override public void onComplete() { Log.e("TAG","On Complete !!"); }
                });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }

    // -------------------
    // UTILS
    // -------------------

    private void displayStars (int rate){
        switch(rate) {
            case 0:
                oneStar.setVisibility(view.INVISIBLE);
                twoStars.setVisibility(view.INVISIBLE);
                threeStars.setVisibility(view.INVISIBLE);
                fourStars.setVisibility(view.INVISIBLE);
                fiveStars.setVisibility(view.INVISIBLE);
                break;
            case 1:
                twoStars.setVisibility(view.INVISIBLE);
                threeStars.setVisibility(view.INVISIBLE);
                fourStars.setVisibility(view.INVISIBLE);
                fiveStars.setVisibility(view.INVISIBLE);
                break;
            case 2:
                threeStars.setVisibility(view.INVISIBLE);
                fourStars.setVisibility(view.INVISIBLE);
                fiveStars.setVisibility(view.INVISIBLE);
                break;
            case 3:
                fourStars.setVisibility(view.INVISIBLE);
                fiveStars.setVisibility(view.INVISIBLE);
                break;
            case 4:
                fiveStars.setVisibility(view.INVISIBLE);
                break;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
