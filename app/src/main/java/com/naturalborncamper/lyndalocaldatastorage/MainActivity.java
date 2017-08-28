package com.naturalborncamper.lyndalocaldatastorage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.naturalborncamper.lyndalocaldatastorage.database.DBHelper;
import com.naturalborncamper.lyndalocaldatastorage.database.DataSource;
import com.naturalborncamper.lyndalocaldatastorage.model.DataItem;
import com.naturalborncamper.lyndalocaldatastorage.sample.SampleDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final int SIGNIN_REQUEST = 1001;
    public static final String MY_GLOBAL_PREFS = "my_global_prefs";
    private static final String TAG = "MainActivity";
    private TextView tvOut;
    List<DataItem> dataItemList = SampleDataProvider.dataItemList;
    List<String> itemNames = new ArrayList<>();

    DataSource mDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataSource = new DataSource(this);
        mDataSource.open();
        Toast.makeText(this, "Database acquired!", Toast.LENGTH_SHORT).show();

        long numItems = mDataSource.getDataItemsCount();
        if (numItems == 0) {
            for (DataItem item:
                    dataItemList) {
                try {
                    mDataSource.createItem(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(this, "Data inserted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Data already inserted", Toast.LENGTH_SHORT).show();
        }
        
        Collections.sort(dataItemList, new Comparator<DataItem>() {
            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.getItemName().compareTo(o2.getItemName());
            }
        });

        DataItemAdapter adapter = new DataItemAdapter(this, dataItemList);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean grid = settings.getBoolean(getString(R.string.pref_display_grid), false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvItems);
        if (grid) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        recyclerView.setAdapter(adapter);
    }

    // VERY IMPORTANT TO AVOID DATABASE LEAKS WITH CONNECTION STAYING OPENED
    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataSource.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signin:
                Intent intent = new Intent(this, SigninActivity.class);
                startActivityForResult(intent, SIGNIN_REQUEST);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, PrefsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_export:
                if (JSONHelper.exportToJSON(this, dataItemList))
                    Toast.makeText(this, "Data exported", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_import:
                List<DataItem> dataItems = JSONHelper.importFromJSON(this);
                if (dataItems != null)
                {
                    for (DataItem dataItem:
                            dataItems) {
                        Log.i(TAG, "onOptionsItemSelected: " + dataItem.getItemName());
                    }
                }
                else
                    Toast.makeText(this, "Fucke, export failed", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SIGNIN_REQUEST) {
            String email = data.getStringExtra(SigninActivity.EMAIL_KEY);
            Toast.makeText(this, "You signed in as: " + email, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = getSharedPreferences(MY_GLOBAL_PREFS, MODE_PRIVATE).edit();
            editor.putString(SigninActivity.EMAIL_KEY, email);
            editor.apply();
        }
    }

    public void gotoFileActivity(View view) {
        Intent fileIntent = new Intent(this, FileActivity.class);
        startActivity(fileIntent);
    }
}
