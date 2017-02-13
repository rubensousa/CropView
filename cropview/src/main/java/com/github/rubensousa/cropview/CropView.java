package com.github.rubensousa.cropview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;


public class CropView extends FrameLayout {

    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;
    private CropViewDelegate delegate;
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
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);

        frameColor = a.getColor(R.styleable.CropView_cropViewFrameColor,
                ContextCompat.getColor(context, outValue.resourceId));

        backgroundColor = a.getColor(R.styleable.CropView_cropViewFrameColor,
                ContextCompat.getColor(context, R.color.cropview_default_color));

        a.recycle();

        delegate = new CropViewDelegate(this,
                getResources().getDimensionPixelOffset(R.dimen.cropview_frame_default_width),
                getResources().getDimensionPixelOffset(R.dimen.cropview_frame_default_height));

        paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setWillNotDraw(false);
        setBackground(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmap.eraseColor(Color.TRANSPARENT);

        this.canvas.drawColor(backgroundColor);

        // Clear background where the frame is
        this.canvas.drawRect(delegate.getCropRect(), paint);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private void drawOverlay(Canvas canvas) {

    }

    private void drawFrame(Canvas canvas) {

    }
}
