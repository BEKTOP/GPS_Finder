package com.github.a5809909.gps_finder.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

import com.github.a5809909.gps_finder.Model.LocationModel;
import com.github.a5809909.gps_finder.R;
import com.github.a5809909.gps_finder.Sql.DatabaseHelper;

import java.util.List;

public class DatabaseFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private GridView gridView;
    Context mContext;

//    public static DatabaseFragment getInstance(){
//        Bundle args= new Bundle();
//        DatabaseFragment fragment = new DatabaseFragment();
//        fragment.setArguments(args);
//
//        return fragment;
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  setContentView(R.layout.activity_data);
        databaseHelper = new DatabaseHelper(mContext);
        List<LocationModel> locationModelList = databaseHelper.getAllLocationModels();

        databaseHelper.close();
        gridView =view.findViewById(R.id.gridView1);
        Cursor mCursor = databaseHelper.getAllItems();

        String[] from = new String[] { databaseHelper.COLUMN_DAY_AND_TIME, databaseHelper.COLUMN_LAT, databaseHelper.COLUMN_LNG,
                databaseHelper.COLUMN_ACCURACY, databaseHelper.COLUMN_ADDRESS};
        int[] to = new int[] { R.id.tv_time,R.id.tv_json_string, R.id.tv_lac,R.id.tv_lng,R.id.tv_accuracy};

        SimpleCursorAdapter mCursorAd = new SimpleCursorAdapter(mContext, R.layout.item_grid_view, mCursor, from, to, 0);

        gridView.setAdapter(mCursorAd);

    }


    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_database,container,false);
        return view;


    }
}
