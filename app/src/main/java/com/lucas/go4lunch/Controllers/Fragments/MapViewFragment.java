package com.lucas.go4lunch.Controllers.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lucas.go4lunch.Models.NearbySearch.NearbySearch;
import com.lucas.go4lunch.Models.NearbySearch.Result;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UtilsSingleton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment
        implements com.google.android.gms.location.LocationListener {

    @BindView(R.id.myLocationButton) FloatingActionButton myLocationBtn;
    @BindView(R.id.mapView) MapView mMapView;

    UtilsSingleton utils = UtilsSingleton.getInstance();

    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMS = 100;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Disposable disposable;

    private LatLng currentPostion;
    private List<NearbySearch> restaurant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, rootView);
        SharedPref.init(getContext());
        executeHttpRequestWithRetrofit();

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                askPermissionsAndShowMyLocation();
                getCurrentLocationAndZoomOn();
            }
        });

        return rootView;
    }


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.myLocationButton)
    public void onClickLocationButton(){
        getCurrentLocationAndZoomOn();
    }

    // --------------------
    // CURRENT LOCATION
    // --------------------

    private void getcurrentLocation () {
        Criteria criteria = new Criteria();
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(criteria, true);

        if (!EasyPermissions.hasPermissions(getContext(), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_location_access), LOCATION_PERMS, PERMS);
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        currentPostion = new LatLng(location.getLatitude(), location.getLongitude());

        SharedPref.write(SharedPref.currentPostiton, location.getLatitude() + "," + location.getLongitude());
    }

    private void getCurrentLocationAndZoomOn () {
        getcurrentLocation();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPostion)
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) { }

    // --------------------
    // PERMISSION
    // --------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void askPermissionsAndShowMyLocation() {

        if (!EasyPermissions.hasPermissions(getContext(), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_location_access), LOCATION_PERMS, PERMS);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        this.disposable = PlaceStreams.streamFetchNearbySearch(SharedPref.read(SharedPref.currentPostiton, ""))
                .subscribeWith(new DisposableObserver<NearbySearch>(){
                    @Override
                    public void onNext(NearbySearch response) {
                        Log.e("TAG","On Next");

                        List<Result> dlRestaurant = response.getResults();
                        String title;

                        for (Result restaurant : dlRestaurant){

                            if (restaurant.getName() != null){
                                title = restaurant.getName();
                            }
                            else {
                                title = "Unknow";
                            }

                            LatLng latLngRestaurant = new LatLng(restaurant.getGeometry().getLocation().getLat(),
                                    restaurant.getGeometry().getLocation().getLng());

                            mMap.addMarker(new MarkerOptions().position(latLngRestaurant)
                                    .title(title));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TAG","On Error"+Log.getStackTraceString(e));
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG","On Complete !!");
                    }
                });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
