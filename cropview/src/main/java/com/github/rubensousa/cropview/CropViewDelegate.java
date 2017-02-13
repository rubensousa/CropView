package com.github.rubensousa.cropview;


import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

class CropViewDelegate implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private CropView cropView;
    private Rect cropRect;
    private int defaultWidth;
    private int defaultHeight;
    private boolean firstLayout;

    public CropViewDelegate(CropView cropView, int defaultWidth, int defaultHeight) {
        this.cropView = cropView;
        this.cropView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.cropView.setOnTouchListener(this);
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        cropRect = new Rect();
        firstLayout = true;
    }

    public Rect getCropRect() {
        return cropRect;
    }

    public Path getCropPath() {
        Path path = new Path();
        path.addRect(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom, Path.Direction.CW);
        return path;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onGlobalLayout() {
        if (cropView.getWidth() != 0 && cropView.getHeight() != 0) {
            if (firstLayout) {
                cropRect.left = cropView.getWidth() / 2 - defaultWidth / 2;
                cropRect.right = cropView.getWidth() / 2 + defaultWidth / 2;
                cropRect.top = cropView.getHeight() / 2 - defaultHeight / 2;
                cropRect.bottom = cropView.getHeight() / 2 + defaultHeight / 2;
                firstLayout = false;
            }
            cropView.invalidate();
            cropView.requestLayout();
        }
    }

}
