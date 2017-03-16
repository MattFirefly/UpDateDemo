package com.gm.updatelibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gm.updatelibrary.io.UpdateAPP;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UpdateAPP().binding(this);
    }

}
