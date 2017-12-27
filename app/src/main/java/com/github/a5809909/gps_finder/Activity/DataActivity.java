package com.github.a5809909.gps_finder.Activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

import com.github.a5809909.gps_finder.Model.PhoneState;
import com.github.a5809909.gps_finder.R;
import com.github.a5809909.gps_finder.MySQL.DatabaseHelper;

import java.util.List;

public class DataActivity extends Activity {
    private Cursor mCursor;
    private DatabaseHelper databaseHelper;
    private SimpleCursorAdapter mCursorAd;
    GridView gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        databaseHelper = new DatabaseHelper(this);
        List<PhoneState> phoneStates = databaseHelper.getAllPhoneStates();

        databaseHelper.close();
        gv=findViewById(R.id.gridView1);
        mCursor = databaseHelper.getAllItems();

        String[] from = new String[] { databaseHelper.COLUMN_TIME, databaseHelper.COLUMN_JSON_STRING, databaseHelper.COLUMN_LAT,
                databaseHelper.COLUMN_LNG, databaseHelper.COLUMN_ACCURACY};
        int[] to = new int[] { R.id.tv_time,R.id.tv_json_string, R.id.tv_lac,R.id.tv_lng,R.id.tv_accuracy};

        mCursorAd = new SimpleCursorAdapter(this, R.layout.item_grid_view, mCursor, from, to, 0);

        gv.setAdapter(mCursorAd);

    }

}
