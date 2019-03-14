package com.lucas.go4lunch.Controllers.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;

import com.lucas.go4lunch.Controllers.Fragments.ListViewFragment;
import com.lucas.go4lunch.Controllers.Fragments.MapViewFragment;
import com.lucas.go4lunch.Controllers.Fragments.WorkmatesFragment;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.UtilsSingleton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.activity_main_bottom_navigation) BottomNavigationView bottomNavigationView;

    public static Context contextOfApplication;

    UtilsSingleton utils = UtilsSingleton.getInstance();

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.configureToolbar();
        this.configureBottomView();
        this.launchFragmentMapView();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private void configureToolbar(){
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("I'm Hungry!");
        setSupportActionBar(toolbar);
    }

    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                updateMainFragment(menuItem.getItemId());
                menuItem.setChecked(true);
                return false;
            }
        });
    }

    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // ---------------------
    // ACTION
    // ---------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on sz` items
        switch (item.getItemId()) {

            case R.id.menu_toolbar_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_top_stories :
                break;
            case R.id.activity_main_drawer_most_popular :
                break;
            case R.id.activity_main_drawer_movie_reviews :
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------
    // UI
    // ---------------------

    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.action_map:
                launchFragmentMapView();
                break;
            case R.id.action_list:
                launchFragmentListView();
                break;
            case R.id.action_wormates:
                launchFragmentWorkmates();
                break;
        }
        return true;
    }

    // ---------------------
    // LAUNCH
    // ---------------------

    public void launchFragmentMapView (){
        MapViewFragment fragment = new MapViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();
    }

    public void launchFragmentListView (){
        ListViewFragment fragment = new ListViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();
    }

    public void launchFragmentWorkmates (){
        WorkmatesFragment fragment = new WorkmatesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.commit();
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

}
