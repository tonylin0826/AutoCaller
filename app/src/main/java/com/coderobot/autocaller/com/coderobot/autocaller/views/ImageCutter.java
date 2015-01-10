package com.coderobot.autocaller.com.coderobot.autocaller.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Tony
 */
public class ImageCutter extends View {
    private static final String TAG = "ImageCutter";

    private Context mContext;
    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private int mWidth = 0;
    private int mHeight = 0;
    private Matrix mMatrix = new Matrix();

    public ImageCutter(Context context) {
        super(context);

    }

    public ImageCutter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImageCutter(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) return;
        float scale = getScale();

        log("mWidth : " + getWidth());
        log("mHeight : " + getHeight());
        log("bitmap width : " + mBitmap.getWidth());
        log("bitmap height : " + mBitmap.getHeight());
        log("scale : " + scale);

        mMatrix.reset();
        mMatrix.postScale(scale, scale);
        mMatrix.postTranslate(getTranslateHorizontal(), getTranslateVertical());
        canvas.drawBitmap(mBitmap, mMatrix, null);

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        canvas.drawCircle(0, 0, 100, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log("w = " + MeasureSpec.getSize(widthMeasureSpec) + "  h = " + MeasureSpec.getSize(heightMeasureSpec));
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public boolean setImageBitmap(Bitmap bitmap) {
        if (bitmap == null) return false;

        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();

        mBitmap = bitmap;
        invalidate();
        return true;
    }

    private float getScale() {
        if (mBitmap == null) return 1.0f;

        float scaleWidth = (((float) mWidth) / ((float) mBitmap.getWidth()));
        float scaleHeight = (((float) mHeight) / ((float) mBitmap.getHeight()));

        return Math.min(scaleHeight, scaleWidth);
    }

    private float getTranslateVertical() {
        if (mBitmap == null) return 0.0f;

        float scale = getScale();
        int height = (int) (mBitmap.getHeight() * scale);

        if (height == mHeight) return 0.0f;

        float translateV = ((float) (mHeight / 2) - (float) (height / 2));

        return translateV;
    }

    private float getTranslateHorizontal() {
        if (mBitmap == null) return 0.0f;

        float scale = getScale();
        int width = (int) (mBitmap.getWidth() * scale);

        if (width == mWidth) return 0.0f;

        float translateH = ((float) (mWidth / 2) - (float) (width / 2));

        return translateH;
    }

    public Point getBitmapSize() {
        if (mBitmap == null) return new Point(0, 0);

        float scale = getScale();
        int newWidth = (int) (scale * mBitmap.getWidth());
        int newHeight = (int) (scale * mBitmap.getHeight());

        return new Point(newWidth, newHeight);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
