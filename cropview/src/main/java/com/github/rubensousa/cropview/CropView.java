package com.github.rubensousa.cropview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class CropView extends FrameLayout {

    

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }
}
