package com.cs.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author ChenSen
 * @desc
 * @since 2021/12/21 13:18
 **/
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mDividerHeight;
    private int mDividerColor;
    private int mLeftMargin;
    private int mRightMargin;
    private Paint mDividerPaint;

    public DividerItemDecoration(@ColorInt int dividerColor, int dividerHeight, int leftMargin, int rightMargin) {
        mDividerColor = dividerColor;
        mDividerHeight = dividerHeight;
        mLeftMargin = leftMargin;
        mRightMargin = rightMargin;

        mDividerPaint = new Paint();
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = mDividerHeight;
        } else {
            outRect.top = 0;
        }
        outRect.bottom = mDividerHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + mLeftMargin;
        int right = parent.getWidth() - parent.getPaddingRight() - mRightMargin;

        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = top + mDividerHeight;
            c.drawRect(left, top, right, bottom, mDividerPaint);

            if (i == 0) {
                c.drawRect(left, view.getTop() - mDividerHeight, right, view.getTop(), mDividerPaint);
            }
        }
    }
}
