package com.lucas.go4lunch.Controllers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucas.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment {


    public static MapViewFragment newInstance() {
        return (new MapViewFragment());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }
}
