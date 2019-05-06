package com.lucas.go4lunch.Controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lucas.go4lunch.BuildConfig;
import com.lucas.go4lunch.Controllers.Fragments.ListViewFragment;
import com.lucas.go4lunch.Controllers.Fragments.MapViewFragment;
import com.lucas.go4lunch.Controllers.Fragments.WorkmatesFragment;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.activity_main_bottom_navigation) BottomNavigationView bottomNavigationView;

    final Fragment fragment1 = new MapViewFragment();
    final Fragment fragment2 = new ListViewFragment();
    final Fragment fragment3 = new WorkmatesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Places.initialize(getApplicationContext(), BuildConfig.ApiKey);
        SharedPref.init(this);

        this.configureToolbar();
        this.configureBottomView();
        this.configureDrawerLayout();
        this.configureNavigationView();

        fm.beginTransaction().add(R.id.activity_main_frame_layout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.activity_main_frame_layout, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.activity_main_frame_layout, fragment1, "1").commit();
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    private void configureToolbar(){
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.toolbar_title_main));
        setSupportActionBar(toolbar);
    }

    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        this.displayUserInfo();
    }

    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ---------------------
    // ACTION
    // ---------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_toolbar_search:
                startAutoComplete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.your_lunch:
                this.displayYourlunch();
                break;
            case R.id.settings:
                startSettingsActivity();
                break;
            case R.id.logout:
                this.signOutUserFromFirebase();
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.action_map:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.action_list:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;

            case R.id.action_wormates:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    };

    // --------------------
    // REST REQUESTS
    // --------------------

    public void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    // ---------------------
    // PLACE AUTOCOMPLETE
    // ---------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                startDisplayRestaurantInfoActivity(place.getId());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(MainActivity.this, "Could not get location.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) { }
        }
    }

    public void startAutoComplete () {
        double currentLat = Double.parseDouble(SharedPref.read(SharedPref.currentPositionLat, ""));
        double currentLng = Double.parseDouble(SharedPref.read(SharedPref.currentPositionLng, ""));

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setLocationRestriction(RectangularBounds.newInstance(
                        new LatLng(currentLat - 0.04, currentLng - 0.05),
                        new LatLng(currentLat + 0.04, currentLng + 0.05)))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // ---------------------
    // UI
    // ---------------------

    private void displayUserInfo(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        ImageView menuImg= (ImageView) navigationView.getHeaderView(0).findViewById(R.id.menu_img_profile);
        TextView menuName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menu_name);
        TextView menuMail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menu_mail);

        if (this.getCurrentUser() != null){
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

                User currentUser = documentSnapshot.toObject(User.class);

                try {
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    menuName.setText(username);

                    UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Glide.with(this)
                                        .load(document.get("urlPicture"))
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(menuImg);
                            } else {
                                //System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with " + task.getException());
                        }
                    });

                    menuMail.setText(currentUser.getEmail());
                } catch (Exception e){this.startConnexionActivity();}
        });
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    // ---------------------
    // LAUNCH
    // ---------------------

    private void startDisplayRestaurantInfoActivity(String placeId){
        Intent myIntent = new Intent(this, DisplayRestaurantInfo.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.bundleKeyPlaceId, placeId);

        myIntent.putExtras(bundle);
        this.startActivity(myIntent);
    }

    private void displayYourlunch(){

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    if (document.get("dayRestaurant").equals("none")){
                        Toast.makeText(getApplication(), getString(R.string.noDayRestaurant), Toast.LENGTH_LONG).show();
                    } else {
                        startDisplayRestaurantInfoActivity(document.get("dayRestaurant").toString());
                    }
                } else {
                    System.out.println("No such document");
                }
            } else {
                System.out.println("get failed with " + task.getException());
            }
        });
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // ---------------------
    // LIFE CYCLE
    // ---------------------

    @Override
    protected void onResume() {
        super.onResume();
    }
}
