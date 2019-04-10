package com.lucas.go4lunch.Controllers.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lucas.go4lunch.Controllers.Activities.DisplayRestaurantInfo;
import com.lucas.go4lunch.Models.PlaceDetails.PlaceDetails;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.Constant;
import com.lucas.go4lunch.Utils.PlaceStreams;
import com.lucas.go4lunch.Utils.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Intent.getIntent;

public class MapViewFragment extends Fragment
        implements com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    @BindView(R.id.myLocationButton)
    FloatingActionButton myLocationBtn;
    @BindView(R.id.mapView)
    MapView mMapView;

    private static final String PERMS = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMS = 100;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Disposable disposable;
    private LatLng currentPosition;
    Marker mMarker;

    public MapViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, rootView);
        SharedPref.init(getContext());

        executeHttpRequestWithRetrofit();
        System.out.println(SharedPref.read(SharedPref.radius, 300));

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        askPermissionsAndShowMyLocation();
        getCurrentLocationAndZoomOn();
        mMap.setOnMarkerClickListener(this);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.myLocationButton)
    public void onClickLocationButton() {
        mMap.clear();
        getCurrentLocationAndZoomOn();
        executeHttpRequestWithRetrofit();
    }

    @Override
    public boolean onMarkerClick(final Marker mMarker) {
        if (mMarker.equals(mMarker))
            launchDisplayRestaurantInfo(mMarker.getTitle());
        mMarker.hideInfoWindow();
        return true;
    }

    // --------------------
    // CURRENT LOCATION
    // --------------------

    private void getCurrentLocation() {
        Criteria criteria = new Criteria();
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(criteria, true);

        if (!EasyPermissions.hasPermissions(getContext(), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_location_access), LOCATION_PERMS, PERMS);
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(provider);
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        SharedPref.write(SharedPref.currentPositionLat, location.getLatitude() + "");
        SharedPref.write(SharedPref.currentPositionLng, location.getLongitude() + "");
    }

    private void getCurrentLocationAndZoomOn() {
        getCurrentLocation();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    // --------------------
    // PERMISSION
    // --------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        System.out.println(SharedPref.read(SharedPref.radius, 300));
        this.disposable = PlaceStreams.streamFetchPlaceIdAndFetchDetails(SharedPref.getCurrentPosition(), SharedPref.read(SharedPref.radius, 300))
                .subscribeWith(new DisposableObserver<PlaceDetails>(){
                    @Override
                    public void onNext(PlaceDetails response) {
                        Log.e("TAG","On Next");

                        mMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(response.getResult().getGeometry().getLocation().getLat(),
                                        response.getResult().getGeometry().getLocation().getLng()))
                                .title(response.getResult().getPlaceId()));
                    }

                    @Override public void onError(Throwable e) {  }
                    @Override public void onComplete() {  }
                });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UTILS
    // -------------------

    private void launchDisplayRestaurantInfo(String placeId){
        Intent myIntent = new Intent(getActivity(), DisplayRestaurantInfo.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.bundleKeyPlaceId, placeId);

        myIntent.putExtras(bundle);
        this.startActivity(myIntent);
    }

    // -------------------
    // LIFE CYCLE
    // -------------------

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
        this.disposeWhenDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
