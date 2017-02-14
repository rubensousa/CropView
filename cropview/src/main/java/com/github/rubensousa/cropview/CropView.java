package com.github.rubensousa.cropview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class CropView extends FrameLayout {

    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;
    private Paint framePaint;
    private CropViewDelegate delegate;
    private int defaultWidth;
    private int defaultHeight;
    private int touchPointWidth;
    private int backgroundColor;
    private int frameColor;

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
        TypedValue outValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CropView, 0, 0);
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);

        frameColor = a.getColor(R.styleable.CropView_cropViewFrameColor,
                ContextCompat.getColor(context, outValue.resourceId));

        backgroundColor = a.getColor(R.styleable.CropView_cropViewBackground,
                ContextCompat.getColor(context, R.color.cropview_default_color));

        touchPointWidth = a.getDimensionPixelOffset(R.styleable.CropView_cropViewFrameCircleWidth,
                getResources().getDimensionPixelOffset(R.dimen.cropview_touchpoint_width));

        defaultWidth = a.getDimensionPixelOffset(R.styleable.CropView_cropViewDefaultWidth,
                getResources().getDimensionPixelOffset(R.dimen.cropview_frame_default_width));

        defaultHeight = a.getDimensionPixelOffset(R.styleable.CropView_cropViewDefaultHeight,
                getResources().getDimensionPixelOffset(R.dimen.cropview_frame_default_height));

        int touchRadius = isInEditMode() ? 0
                : getResources().getDimensionPixelOffset(R.dimen.cropview_touch_radius);

        if (touchPointWidth > touchRadius) {
            touchRadius = touchPointWidth;
        }

        delegate = new CropViewDelegate(this, touchRadius,
                a.getDimensionPixelOffset(R.styleable.CropView_cropViewMinWidth,
                        touchPointWidth * 5),
                a.getDimensionPixelOffset(R.styleable.CropView_cropViewMinHeight,
                        touchPointWidth * 5));

        paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        framePaint.setColor(frameColor);
        setWillNotDraw(false);
        setBackground(null);
        a.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return delegate.onTouch(this, event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != 0 && h != 0) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            Rect cropRect = delegate.getCropRect();
            cropRect.left = w / 2 - defaultWidth / 2;
            cropRect.right = w / 2 + defaultWidth / 2;
            cropRect.top = h / 2 - defaultHeight / 2;
            cropRect.bottom = h / 2 + defaultHeight / 2;
            delegate.setCropRect(cropRect);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmap.eraseColor(Color.TRANSPARENT);
        this.canvas.drawColor(backgroundColor);

        // Clear background where the frame is
        this.canvas.drawRect(delegate.getCropRect(), paint);

        canvas.drawBitmap(bitmap, 0, 0, null);
        drawFrame(canvas);
    }

    public void addOnCropSectionChangeListener(OnCropSectionChangeListener listener) {
        delegate.addOnCropSectionChangeListener(listener);
    }

    public void removeOnCropSectionChangeListener(OnCropSectionChangeListener listener) {
        delegate.removeOnCropSectionChangeListener(listener);
    }

    public Rect getCropRect() {
        return delegate.getCropRect();
    }

    public void setCropRect(Rect rect) {
        delegate.setCropRect(rect);
    }

    private void drawFrame(Canvas canvas) {
        Rect rect = delegate.getCropRect();

        framePaint.setStrokeWidth(touchPointWidth / 4);
        // Draw lines between touch points
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, framePaint);
        canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom, framePaint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, framePaint);
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, framePaint);

        framePaint.setStrokeWidth(0);
        // Draw touch points
        canvas.drawCircle(rect.left, rect.top, touchPointWidth, framePaint);
        canvas.drawCircle(rect.right, rect.top, touchPointWidth, framePaint);
        canvas.drawCircle(rect.left, rect.bottom, touchPointWidth, framePaint);
        canvas.drawCircle(rect.right, rect.bottom, touchPointWidth, framePaint);
    }

    public interface OnCropSectionChangeListener {
        void onCropSectionChanged(Rect rect);
    }
}
