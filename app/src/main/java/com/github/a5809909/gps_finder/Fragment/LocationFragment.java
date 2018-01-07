package com.github.a5809909.gps_finder.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.a5809909.gps_finder.R;


public class LocationFragment extends Fragment {
    Context context;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // задаем разметку фрагменту
        final View view = inflater.inflate(R.layout.fragment_location, container, false);
        // ну и контекст, так как фрагменты не содержат собственного
        context = view.getContext();
        // выводим текст который хотим
        TextView wordsCount = (TextView) view.findViewById(R.id.text_cell_id);
        wordsCount.setText("SecondActivity - Swipe Left - Right");
        return view;
    }


}
