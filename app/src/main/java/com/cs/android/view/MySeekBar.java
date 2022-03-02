package com.cs.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author ChenSen
 * @desc
 * @since 2022/2/16 12:31
 **/
public class MySeekBar extends View {


    public MySeekBar(Context context) {
        super(context);
        init();
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint mLinePaint;
    private Paint mPaint;
    private Path mPath;

    private int mHeight;
    private int mWidth;
    private float mPercentage = 1f;
    private float mMax = 100;
    private float mCurrent = 0;

    private OnSeekBarChangeListener mListener;


    public void setMax(float max) {
        this.mMax = max;
        this.mCurrent = 0;
        this.mPercentage = 0f;
        invalidate();
    }

    public void setCurrent(float current) {
        this.mCurrent = current;
        this.mPercentage = mCurrent / mMax;
        invalidate();
    }


    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setColor(Color.parseColor("#979797"));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);

        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient gradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                Color.parseColor("#6d6d6d"), Color.parseColor("#ffffff"), Shader.TileMode.CLAMP);
        mPaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(mHeight, 1, mWidth, 1, mLinePaint);

        mPath.reset();
        mPath.moveTo(0, mHeight);

        if (mWidth * mPercentage <= mHeight) { //绘制起始部分（一个三角形）
            mPath.lineTo(mWidth * mPercentage, mHeight - mWidth * mPercentage);
            mPath.lineTo(mWidth * mPercentage, mHeight);
        } else if (mWidth * mPercentage <= mHeight * 2) {
            mPath.lineTo(mHeight, 0);
            mPath.lineTo(mWidth * mPercentage, 0);
            mPath.lineTo(mHeight, mWidth * mPercentage - mHeight);
            mPath.lineTo(mHeight, mHeight);
        } else {
            mPath.lineTo(mHeight, 0);
            mPath.lineTo(mWidth * mPercentage, 0);
            mPath.lineTo(mWidth * mPercentage - mHeight, mHeight);
        }

        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mPercentage = event.getX() / mWidth;
                if (mPercentage < 0) {
                    mPercentage = 0;
                }
                if (mPercentage > 1) {
                    mPercentage = 1;
                }

                if (mListener != null) {
                    mListener.onStartTrackingTouch(this);
                    mListener.onProgressChanged(this, mPercentage);
                }
                invalidate();
            }

            case MotionEvent.ACTION_MOVE: {
                mPercentage = event.getX() / mWidth;
                if (mPercentage < 0) {
                    mPercentage = 0;
                }
                if (mPercentage > 1) {
                    mPercentage = 1;
                }
                if (mListener != null) {
                    mListener.onProgressChanged(this, mPercentage);
                }
                invalidate();
            }

            case MotionEvent.ACTION_POINTER_UP: {

                if (mListener != null) {
                    mListener.onStopTrackingTouch(this);
                }
            }
        }
        return true;
    }


    public interface OnSeekBarChangeListener {

        void onProgressChanged(MySeekBar seekBar, float percentage);

        void onStartTrackingTouch(MySeekBar seekBar);

        void onStopTrackingTouch(MySeekBar seekBar);
    }


    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.mListener = listener;
    }
}
