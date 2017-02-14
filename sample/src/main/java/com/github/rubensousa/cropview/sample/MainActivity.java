package com.github.rubensousa.cropview.sample;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.rubensousa.cropview.CropView;

public class MainActivity extends AppCompatActivity implements CropView.OnCropSectionChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CropView cropView = (CropView) findViewById(R.id.cropView);
        cropView.addOnCropSectionChangeListener(this);
    }

    @Override
    public void onCropSectionChanged(Rect rect) {
        Log.d("Crop", rect.toShortString());
    }
}
