package com.github.a5809909.gps_finder.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;
import com.github.a5809909.gps_finder.Sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFragment extends Fragment implements IListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database, container, false);
        GridView gridView = view.findViewById(R.id.gridView1);
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());

        List<LocationModel> locationModelList = new ArrayList<>();
//        locationModelList = databaseHelper.getAllLocationModels();
        String[] from = new String[]{databaseHelper.COLUMN_DAY_AND_TIME, databaseHelper.COLUMN_LAT, databaseHelper.COLUMN_LNG,
                databaseHelper.COLUMN_ACCURACY, databaseHelper.COLUMN_ADDRESS};
        Cursor mCursor = databaseHelper.getAllItems();
        int[] to = new int[]{R.id.tv_date_and_time, R.id.tv_lat, R.id.tv_lng, R.id.tv_acc, R.id.tv_address};
        Log.i("111", "onCreateView: ");
        SimpleCursorAdapter cursorAd = new SimpleCursorAdapter(getActivity(), R.layout.item_grid_view, mCursor, from, to, 0);

        gridView.setAdapter(cursorAd);


        databaseHelper.close();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("444", "onResume: ");
    }

    @Override
    public void update() {
        onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("444", "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("444", "onDestroy: ");
    }
}
