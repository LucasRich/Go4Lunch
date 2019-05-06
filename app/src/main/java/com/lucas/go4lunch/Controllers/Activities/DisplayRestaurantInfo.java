package com.lucas.go4lunch.Controllers.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lucas.go4lunch.BuildConfig;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.AlarmReceiver;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;
import com.lucas.go4lunch.Utils.UtilsFunction;
import com.lucas.go4lunch.Views.Adapter.DisplayRestaurantAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class DisplayRestaurantInfo extends BaseActivity implements DisplayRestaurantAdapter.Listener {

    @BindView(R.id.img_restaurant) ImageView imgRestaurant;
    @BindView(R.id.name_restaurant) TextView nameRestaurant;
    @BindView(R.id.adress_restaurant) TextView addressRestaurant;
    @BindView(R.id.item_3_stars) ImageView threeStars;
    @BindView(R.id.item_2_stars) ImageView twoStars;
    @BindView(R.id.item_1_star) ImageView oneStar;
    @BindView(R.id.restaurantChoiceFab) FloatingActionButton restaurantChoiceFab;
    @BindView(R.id.displayRestaurant_error) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.displayRestaurant_recycler_view) RecyclerView recyclerView;

    private Disposable disposable;
    private String phone_number;
    private String restaurantUrl;
    private String webSite;
    private String nameOfRestaurant;
    private String addressOfRestaurant;
    private int currentRate;
    private String placeId;
    private String userUid;
    private DisplayRestaurantAdapter displayRestaurantAdapter;
    private float averageRate = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        placeId = bundle.getString(Constant.bundleKeyPlaceId, "");
        userUid = this.getCurrentUser().getUid();

        this.initFab();
        this.executeHttpRequestWithRetrofit();
        this.configureRecyclerView();
        this.getRatingAndDisplay();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_display_restaurant_info;
    }

    // -------------------
    // INIT
    // -------------------

    public void initFab(){
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    if (document.get("dayRestaurant").equals(placeId)){
                        restaurantChoiceFab.setImageResource(R.drawable.ic_highlight_off_black_48dp);
                    } else {
                        restaurantChoiceFab.setImageResource(R.drawable.ic_check_circle_black_48dp);
                    }
                } else {
                    //System.out.println("No such document");
                }
            } else {
                System.out.println("get failed with " + task.getException());
            }
        });
    }

    // --------------------
    // CONFIGURATION
    // --------------------

    private void configureRecyclerView(){

        this.displayRestaurantAdapter = new DisplayRestaurantAdapter(generateOptionsForAdapter(UserHelper.getAllUserRestaurant(placeId)),this);
        displayRestaurantAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(displayRestaurantAdapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.displayRestaurantAdapter);
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
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
        else { Toast.makeText(getApplication(), getString(R.string.no_phone_number), Toast.LENGTH_LONG).show(); }
    }

    @OnClick(R.id.like_view)
    public void onClickLikeView(){
        //CREATION
        AlertDialog.Builder rateDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_stars, (ViewGroup)findViewById(R.id.ratingLayout));
        rateDialog.setView(layout);

        //DECLARATION
        ImageView star1 = (ImageView)layout.findViewById(R.id.star1);
        ImageView star2 = (ImageView)layout.findViewById(R.id.star2);
        ImageView star3 = (ImageView)layout.findViewById(R.id.star3);

        //INIT INPUT
        UtilsFunction.displayStars(averageRate, star1, star2, star3);

        star1.setOnClickListener(v -> {
            currentRate = 1;
            UtilsFunction.displayStars(currentRate, star1, star2, star3);
        });

        star2.setOnClickListener(v -> {
            currentRate = 2;
            UtilsFunction.displayStars(currentRate, star1, star2, star3);
        });

        star3.setOnClickListener(v -> {
            currentRate = 3;
            UtilsFunction.displayStars(currentRate, star1, star2, star3);
        });

        //BUTTON
        rateDialog.setPositiveButton(getString(R.string.message_save), (dialog, which) -> {
            saveRating();
            getRatingAndDisplay();
        });
        rateDialog.setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> { });

        //DISPLAY DIALOG
        rateDialog.create().show();
    }

    @OnClick(R.id.website_view)
    public void onClickWebsiteView(){
        if (webSite != null){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
            startActivity(intent);
        }
        else { Toast.makeText(getApplication(), getString(R.string.no_website), Toast.LENGTH_LONG).show(); }

    }

    @OnClick(R.id.restaurantChoiceFab)
    public void onClickRestaurantChoiceFab(){
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    if (document.get("dayRestaurant").equals(placeId)){
                        UserHelper.updateDayRestaurant("none", userUid);
                        restaurantChoiceFab.setImageResource(R.drawable.ic_check_circle_black_48dp);
                        Toast.makeText(getApplication(), getString(R.string.unSeclectRestaurant), Toast.LENGTH_LONG).show();
                        SharedPref.write(SharedPref.notificationAllow, false);
                    } else {
                        if (document.get("dayRestaurant").equals("none")){
                            UserHelper.updateDayRestaurant(placeId, userUid);
                            restaurantChoiceFab.setImageResource(R.drawable.ic_highlight_off_black_48dp);
                            Toast.makeText(getApplication(), getString(R.string.selectDayRestaurant), Toast.LENGTH_LONG).show();
                            SharedPref.write(SharedPref.notificationAllow, true);
                        } else {
                            new AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.changeDayRestaurant))
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        UserHelper.updateDayRestaurant(placeId, userUid);
                                        restaurantChoiceFab.setImageResource(R.drawable.ic_highlight_off_black_48dp);
                                        Toast.makeText(getApplication(), (R.string.selectDayRestaurant), Toast.LENGTH_LONG).show();
                                        SharedPref.write(SharedPref.notificationAllow, true);
                                    })
                                    .setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> {
                                        SharedPref.write(SharedPref.notificationAllow, false);
                                    }).show();
                        }
                    }
                } else {
                    //System.out.println("No such document");
                }
            } else {
                System.out.println("get failed with " + task.getException());
            }
        });
        SharedPref.write(SharedPref.dayRestaurant, placeId);
        this.startNotificationAtMidday();
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        this.disposable = PlaceStreams.streamFetchPlaceDetails(placeId)
                .subscribeWith(new DisposableObserver<PlaceDetails>(){
                    @Override
                    public void onNext(PlaceDetails response) {
                        Log.e("TAG","On Next");

                        nameOfRestaurant = response.getResult().getName();
                        nameRestaurant.setText(nameOfRestaurant);
                        addressOfRestaurant = response.getResult().getVicinity() ;
                        addressRestaurant.setText(response.getResult().getTypes().get(0) + " - " + response.getResult().getVicinity());

                        if (response.getResult().getPhotos() != null){
                            String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" +
                                                response.getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.ApiKey;

                            Glide.with(getApplicationContext()).load(imageUrl).into(imgRestaurant);
                        }
                        else {
                            Glide.with(getApplicationContext()).load("https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/No_image_available_600_x_450.svg/" +
                                    "600px-No_image_available_600_x_450.svg.png").into(imgRestaurant);
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

    private void getRatingAndDisplay(){

        List<Long> listRate = new ArrayList();

        UserHelper.getUsersCollection()
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    UserHelper.getUsersCollection()
                            .document(document.getData().get("uid").toString())
                            .collection("rate")
                            .document(placeId)
                            .get().addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            DocumentSnapshot document1 = task1.getResult();
                            if (document1.exists()) {
                                listRate.add((Long) document1.getData().get("rate"));
                                averageRate = UtilsFunction.getAverage(listRate);
                                UtilsFunction.displayStars(averageRate, oneStar, twoStars, threeStars);
                            } else {
                                //System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with " + task1.getException());
                        }
                    });
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });

    }

    private void saveRating(){
        System.out.println("save rating");
        if (this.getCurrentUser() != null) {
            UserHelper.addRate(placeId, currentRate, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
        }
    }

    private void startNotificationAtMidday(){
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        SharedPref.write(SharedPref.notificationRestaurantName, nameOfRestaurant);
        SharedPref.write(SharedPref.notificationRestaurantAddress, addressOfRestaurant);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,SharedPref.read(SharedPref.notificationHour, 12));
        calendar.set(Calendar.MINUTE, SharedPref.read(SharedPref.notificationMin, 0));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(this.displayRestaurantAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
