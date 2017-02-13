package com.github.rubensousa.cropview;


import android.graphics.Path;
import android.graphics.Rect;
import android.support.v7.widget.ViewUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

class CropViewDelegate implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private float touchRadius;
    private float lastX;
    private float lastY;
    private CropView cropView;
    private Rect cropRect;
    private int defaultWidth;
    private int defaultHeight;
    private boolean firstLayout;

    public CropViewDelegate(CropView cropView, int defaultWidth, int defaultHeight,
                            int touchRadius) {
        this.cropView = cropView;
        this.cropView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        this.cropView.setOnTouchListener(this);
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        this.touchRadius = touchRadius;
        cropRect = new Rect();
        firstLayout = true;
    }

    public Rect getCropRect() {
        return cropRect;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return onTouchDown(event);
            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                return true;
        }
        return false;
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

    private boolean onTouchDown(MotionEvent event) {
        if (!isTouchingEdge(event, MotionEvent.EDGE_BOTTOM) &&
                !isTouchingEdge(event, MotionEvent.EDGE_LEFT) &&
                !isTouchingEdge(event, MotionEvent.EDGE_RIGHT) &&
                !isTouchingEdge(event, MotionEvent.EDGE_TOP)) {
            lastX = 0;
            lastY = 0;
            return false;
        }
        lastX = event.getX();
        lastY = event.getY();
        return true;
    }

    private void onTouchUp(MotionEvent event) {
        lastY = 0;
        lastX = 0;
    }

    private void onTouchMove(MotionEvent event) {
        cropRect.left += getLeftOffset(event);
        cropRect.right += getRightOffset(event);
        cropRect.top += getTopOffset(event);
        cropRect.bottom += getBottomOffset(event);
        lastY = event.getY();
        lastX = event.getX();
        cropView.invalidate();
    }

    private int getLeftOffset(MotionEvent event) {
        if (isTouchingEdge(event, MotionEvent.EDGE_LEFT)
                || isTouchingEdge(event, MotionEvent.EDGE_BOTTOM)) {
            return (int) (event.getX() - lastX);
        }
        return 0;
    }

    private int getRightOffset(MotionEvent event) {
        if (isTouchingEdge(event, MotionEvent.EDGE_TOP)
                || isTouchingEdge(event, MotionEvent.EDGE_RIGHT)) {
            return (int) (event.getX() - lastX);
        }
        return 0;
    }

    private int getTopOffset(MotionEvent event) {
        if (isTouchingEdge(event, MotionEvent.EDGE_LEFT)
                || isTouchingEdge(event, MotionEvent.EDGE_TOP)) {
            return (int) (event.getY() - lastY);
        }
        return 0;
    }

    private int getBottomOffset(MotionEvent event) {
        if (isTouchingEdge(event, MotionEvent.EDGE_BOTTOM)
                || isTouchingEdge(event, MotionEvent.EDGE_RIGHT)) {
            return (int) (event.getY() - lastY);
        }
        return 0;
    }

    private boolean isTouchingEdge(MotionEvent event, int edge) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (edge) {
            case MotionEvent.EDGE_LEFT:
                return isWithinBounds(x, y, cropRect.left, cropRect.top, touchRadius);
            case MotionEvent.EDGE_TOP:
                return isWithinBounds(x, y, cropRect.right, cropRect.top, touchRadius);
            case MotionEvent.EDGE_RIGHT:
                return isWithinBounds(x, y, cropRect.right, cropRect.bottom, touchRadius);
            case MotionEvent.EDGE_BOTTOM:
                return isWithinBounds(x, y, cropRect.left, cropRect.bottom, touchRadius);
        }
        return false;
    }

    private boolean isWithinBounds(int x, int y, int x2, int y2, float radius) {
        return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2)) <= radius;
    }
}
