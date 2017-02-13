package com.github.rubensousa.cropview;


import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

class CropViewDelegate implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private CropView cropView;
    private int left;
    private int top;
    private int right;
    private int bottom;
    private int defaultWidth;
    private int defaultHeight;
    private boolean firstLayout;

    public CropViewDelegate(CropView cropView, int defaultWidth, int defaultHeight) {
        this.cropView = cropView;
        this.cropView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.cropView.setOnTouchListener(this);
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        firstLayout = true;
    }

    public int getLeft() {
        return left;
    }

    public int getBottom() {
        return bottom;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onGlobalLayout() {
        if (cropView.getWidth() != 0 && cropView.getHeight() != 0) {
            if (firstLayout) {
                left = cropView.getWidth() / 2 - defaultWidth / 2;
                right = cropView.getWidth() / 2 + defaultWidth / 2;
                top = cropView.getHeight() / 2 - defaultHeight / 2;
                bottom = cropView.getHeight() / 2 + defaultHeight / 2;
                firstLayout = false;
            }
            cropView.invalidate();
            cropView.requestLayout();
        }
    }

}
