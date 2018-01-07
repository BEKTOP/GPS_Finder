package com.github.a5809909.gps_finder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.a5809909.gps_finder.R;

public class DatabaseFragment extends Fragment {

//    public static DatabaseFragment getInstance(){
//        Bundle args= new Bundle();
//        DatabaseFragment fragment = new DatabaseFragment();
//        fragment.setArguments(args);
//
//        return fragment;
//    }

    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_database,container,false);
        return view;


    }
}
