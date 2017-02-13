package com.github.rubensousa.cropview;


import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

class CropViewDelegate implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private int lastX;
    private int lastY;
    private int currentEdge;
    private float touchRadius;
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
        if (isTouchingEdge(event, MotionEvent.EDGE_LEFT)) {
            currentEdge = MotionEvent.EDGE_LEFT;
        } else if (isTouchingEdge(event, MotionEvent.EDGE_TOP)) {
            currentEdge = MotionEvent.EDGE_TOP;
        } else if (isTouchingEdge(event, MotionEvent.EDGE_RIGHT)) {
            currentEdge = MotionEvent.EDGE_RIGHT;
        } else if (isTouchingEdge(event, MotionEvent.EDGE_BOTTOM)) {
            currentEdge = MotionEvent.EDGE_BOTTOM;
        } else {
            currentEdge = 0;
        }
        return currentEdge != 0 || isWithinBounds((int) event.getX(), (int) event.getY());
    }

    private void onTouchUp(MotionEvent event) {
        currentEdge = 0;
        lastY = 0;
        lastX = 0;
    }

    private void onTouchMove(MotionEvent event) {
        if (currentEdge == 0) {
            cropRect.offset((int) event.getX() - lastX,
                    (int) event.getY() - lastY);
        } else {
            cropRect.left = getLeft(event);
            cropRect.right = getRight(event);
            cropRect.top = getTop(event);
            cropRect.bottom = getBottom(event);
        }
        cropView.invalidate();
        lastX = (int) event.getX();
        lastY = (int) event.getY();
    }

    private int getLeft(MotionEvent event) {
        if (currentEdge == MotionEvent.EDGE_BOTTOM || currentEdge == MotionEvent.EDGE_LEFT) {
            return (int) event.getX();
        }
        return cropRect.left;
    }

    private int getRight(MotionEvent event) {
        if (currentEdge == MotionEvent.EDGE_TOP || currentEdge == MotionEvent.EDGE_RIGHT) {
            return (int) event.getX();
        }
        return cropRect.right;
    }

    private int getTop(MotionEvent event) {
        if (currentEdge == MotionEvent.EDGE_LEFT || currentEdge == MotionEvent.EDGE_TOP) {
            return (int) event.getY();
        }
        return cropRect.top;
    }

    private int getBottom(MotionEvent event) {
        if (currentEdge == MotionEvent.EDGE_BOTTOM || currentEdge == MotionEvent.EDGE_RIGHT) {
            return (int) event.getY();
        }
        return cropRect.bottom;
    }

    private boolean isTouchingEdge(MotionEvent event, int edge) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (edge) {
            case MotionEvent.EDGE_LEFT:
                return isWithinCircle(x, y, cropRect.left, cropRect.top, touchRadius);
            case MotionEvent.EDGE_TOP:
                return isWithinCircle(x, y, cropRect.right, cropRect.top, touchRadius);
            case MotionEvent.EDGE_RIGHT:
                return isWithinCircle(x, y, cropRect.right, cropRect.bottom, touchRadius);
            case MotionEvent.EDGE_BOTTOM:
                return isWithinCircle(x, y, cropRect.left, cropRect.bottom, touchRadius);
        }
        return false;
    }

    private boolean isWithinCircle(int x, int y, int x2, int y2, float radius) {
        return Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2)) <= radius * 4;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= cropRect.left && x <= cropRect.right
                && y <= cropRect.bottom && y >= cropRect.top;
    }
}
