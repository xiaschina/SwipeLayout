package com.xias.demo.swipelayout.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Created by XIAS on 3/2/18.
 */

public class SwipeLayout extends FrameLayout {

    private View mContentView;//内容view
    private View mHiddenView;//隐藏view
    private ViewDragHelper mViewDragHelper;
    private OnSwipeListener onSwipeListener;
    private int mContentViewWidth;
    private int mContentViewHeight;
    private int mHiddenViewWidth;//隐藏view宽度
    private boolean mCanSwipe = true;//item是否可以滑动，默认可以滑动
    private int mTouchSlop;//最小距离

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelperCallback());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentViewWidth = mContentView.getMeasuredWidth();
        mContentViewHeight = mContentView.getMeasuredHeight();
        mHiddenViewWidth = mHiddenView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mContentView.layout(0, 0, mContentViewWidth, mContentViewHeight);
        mHiddenView.layout(mContentViewWidth, 0, mContentViewWidth + mHiddenViewWidth, mContentViewHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
        mHiddenView = getChildAt(1);
    }

    float startX;
    float startY;
    float curX;
    float curY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!mCanSwipe)
            return super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                disallowParentsInterceptTouchEvent(getParent());
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = ev.getX();
                curY = ev.getY();
                float dx = curX - startX;
                float dy = curY - startY;
                if (dx * dx + dy * dy > mTouchSlop * mTouchSlop) {
                    if (Math.abs(dy) > Math.abs(dx)) {
                        allowParentsInterceptTouchEvent(getParent());
                    }
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void disallowParentsInterceptTouchEvent(ViewParent parent) {
        if (null == parent) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(true);
        disallowParentsInterceptTouchEvent(parent.getParent());
    }

    private void allowParentsInterceptTouchEvent(ViewParent parent) {
        if (null == parent) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(false);
        allowParentsInterceptTouchEvent(parent.getParent());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!mCanSwipe)
            return super.onInterceptTouchEvent(ev);
        if (mViewDragHelper.shouldInterceptTouchEvent(ev))
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mCanSwipe)
            return super.onTouchEvent(event);
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private class ViewDragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mHiddenView.offsetLeftAndRight(dx);
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left > 0)
                return 0;
            return Math.max(left, -mHiddenViewWidth);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel >= 0 && Math.abs(mContentView.getLeft()) >= mHiddenViewWidth / 2) {
                onSwipe(true);
            } else {
                onSwipe(false);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mHiddenViewWidth;
        }
    }

    private void onSwipe(boolean flag) {
        if (flag) {
            if (mViewDragHelper.smoothSlideViewTo(mContentView, -mHiddenViewWidth, 0)) {
                if (onSwipeListener != null)
                    onSwipeListener.onSwipeOpen();
                ViewCompat.postInvalidateOnAnimation(this);
            }
            return;
        }

        if (mViewDragHelper.smoothSlideViewTo(mContentView, 0, 0)) {
            if (onSwipeListener != null)
                onSwipeListener.onSwipeClose();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public void setCanSwipe(boolean canSwipe) {
        this.mCanSwipe = canSwipe;
    }

    public interface OnSwipeListener {
        //打开
        void onSwipeOpen();

        //关闭
        void onSwipeClose();
    }
}
