package com.naturalborncamper.lyndalocaldatastorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.naturalborncamper.lyndalocaldatastorage.model.DataItem;

public class MainActivity extends AppCompatActivity {

    private TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataItem item = new DataItem(null, "My menu item", "a category", "a descrition", 1, 9.95, "apple_pie.jpg");

        tvOut = (TextView) findViewById(R.id.out);
        tvOut.setText(item.toString());

    }
}
