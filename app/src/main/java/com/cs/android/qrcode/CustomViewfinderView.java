package com.cs.android.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.cs.android.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.List;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/9 14:27
 **/

public class CustomViewfinderView extends ViewfinderView {

    //重绘时间间隔
    public static final long INT_ANIMATION_DELAY = 12;

    /* ******************************************    边角线相关属性    ************************************************/
    //"边角线长度/扫描边框长度"的占比 (比例越大，线越长)
    public float mLineRate = 0.1F;
    //边角线厚度 (建议使用dp)
    public float mLineDepth = dp2px(4);
    //边角线颜色
    public int mLineColor;

    /* *******************************************    扫描线相关属性    ************************************************/
    //扫描线起始位置
    public int mScanLinePosition = 0;
    //扫描线厚度
    public float mScanLineDepth = dp2px(4);
    //扫描线每次移动距离
    public float mScanLineDy = dp2px(3);
    //渐变线
    public LinearGradient mLinearGradient;
    //图形paint
    public Paint mBitmapPaint;
    ///颜色在渐变中所占比例，此处均衡渐变
    public float[] mPositions = new float[]{0f, 0.5f, 1f};
    //线性梯度各个位置对应的颜色值
    public int[] mScanLineColor = new int[]{0x00000000, Color.WHITE, 0x00000000};

    //扫描框宽、高
    public float mScanFrameWidth;
    public float mScanFrameHeight;

    public CustomViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewfinderView);
        mLineColor = typedArray.getColor(R.styleable.CustomViewfinderView_lineColor, Color.YELLOW);
        mScanLineColor[1] = typedArray.getColor(R.styleable.CustomViewfinderView_cornerColor, Color.YELLOW);
        mScanFrameWidth = typedArray.getDimension(R.styleable.CustomViewfinderView_scanFrameWidth, dp2px(160));
        mScanFrameHeight = typedArray.getDimension(R.styleable.CustomViewfinderView_scanFrameHeight, dp2px(160));
        typedArray.recycle();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
    }

    @SuppressLint({"DrawAllocation"})
    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewSize == null) {
            return;
        }

        final Rect frame = framingRect;

        final int width = getWidth();
        final int height = getHeight();

        //绘制扫描框外部遮罩
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        //绘制4个角
        paint.setColor(mLineColor);
        canvas.drawRect(frame.left, frame.top, frame.left + frame.width() * mLineRate, frame.top + mLineDepth, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + mLineDepth, frame.top + frame.height() * mLineRate, paint);

        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.top, frame.right, frame.top + mLineDepth, paint);
        canvas.drawRect(frame.right - mLineDepth, frame.top, frame.right, frame.top + frame.height() * mLineRate, paint);

        canvas.drawRect(frame.left, frame.bottom - mLineDepth, frame.left + frame.width() * mLineRate, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - frame.height() * mLineRate, frame.left + mLineDepth, frame.bottom, paint);

        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.bottom - mLineDepth, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - mLineDepth, frame.bottom - frame.height() * mLineRate, frame.right, frame.bottom, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            // 绘制渐变扫描线
            mScanLinePosition += mScanLineDy;
            if (mScanLinePosition >= frame.height()) {
                mScanLinePosition = 0;
            }
            mLinearGradient = new LinearGradient(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition, mScanLineColor, mPositions, Shader.TileMode.CLAMP);
            paint.setShader(mLinearGradient);
            canvas.drawRect(frame.left, frame.top + mScanLinePosition, frame.right, frame.top + mScanLinePosition + mScanLineDepth, paint);
            paint.setShader(null);

            //绘制资源图片扫描线
//            Rect lineRect = new Rect();
//            lineRect.left = frame.left;
//            lineRect.top = frame.top + mScanLinePosition;
//            lineRect.right = frame.right;
//            lineRect.bottom = frame.top + dp2px(6) + mScanLinePosition;
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_line);
//            canvas.drawBitmap(bitmap, null, lineRect, mBitmapPaint);


            //============绘制扫描时小圆点，效果为默认=======================

            final float scaleX = this.getWidth() / (float) previewSize.width;
            final float scaleY = this.getHeight() / (float) previewSize.height;
            // draw the last possible result points
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                            (int) (point.getX() * scaleX),
                            (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }

            // draw current possible result points
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            (int) (point.getX() * scaleX),
                            (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }

                // swap and clear buffers
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }

            //============绘制扫描时小圆点，效果为默认 end=======================
        }

        //定时刷新扫描框
        postInvalidateDelayed(INT_ANIMATION_DELAY,
                frame.left - POINT_SIZE,
                frame.top - POINT_SIZE,
                frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE);

    }

    protected void refreshSizes() {
        if (cameraPreview == null) {
            return;
        }
        //添加设置边框大小代码
        cameraPreview.setFramingRectSize(new Size((int) mScanFrameWidth, (int) mScanFrameHeight));

        Rect framingRect = cameraPreview.getFramingRect();
        Size previewSize = cameraPreview.getPreviewSize();
        if (framingRect != null && previewSize != null) {
            this.framingRect = framingRect;
            this.previewSize = previewSize;
        }
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}

