package com.alley.rv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.alley.rv.R;

/**
 * 侧滑菜单
 *
 * @author Phoenix
 * @date 2017/4/14 10:12
 */
public class AlleySwipeLayout extends FrameLayout {
    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;
    private static final boolean OVER_API_11 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    private int mSwipeDirection;
    private View mContentView, mMenuView;
    private int mDownX;
    private int state = STATE_CLOSE;
    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private boolean isFling;
    private OverScroller mOpenScroller, mCloseScroller;
    private Interpolator mCloseInterpolator, mOpenInterpolator;
    private int mBaseX;
    private ViewConfiguration mViewConfiguration;
    private boolean swipeEnable = true;
    private int animDuration;

    public AlleySwipeLayout(Context context) {
        this(context, null);
    }

    public AlleySwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlleySwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenu, 0, defStyle);
            animDuration = a.getInteger(R.styleable.SwipeMenu_anim_duration, 500);
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        mContentView = findViewById(R.id.recycler_item_swipe_content);
        if (mContentView == null) {
            throw new IllegalArgumentException("not find contentView by id recycler_item_swipe_content");
        }
        mMenuView = findViewById(R.id.recycler_item_swipe_menu);
        if (mMenuView == null) {
            throw new IllegalArgumentException("not find menuView by id recycler_item_swipe_menu");
        }
        mViewConfiguration = ViewConfiguration.get(getContext());
        initAlleySwipeLayout();
    }

    public void initAlleySwipeLayout() {
        mGestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX > mViewConfiguration.getScaledMinimumFlingVelocity() || velocityY > mViewConfiguration.getScaledMinimumFlingVelocity()) {
                    isFling = true;
                }
                return isFling;
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);

        mCloseScroller = new OverScroller(getContext());
        mOpenScroller = new OverScroller(getContext());
    }

    public void setCloseInterpolator(Interpolator closeInterpolator) {
        mCloseInterpolator = closeInterpolator;
        if (mCloseInterpolator != null) {
            mCloseScroller = new OverScroller(getContext(), mCloseInterpolator);
        }
    }

    public void setOpenInterpolator(Interpolator openInterpolator) {
        mOpenInterpolator = openInterpolator;
        if (mOpenInterpolator != null) {
            mOpenScroller = new OverScroller(getContext(), mOpenInterpolator);
        }
    }

    public boolean onSwipe(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                isFling = false;
                break;

            case MotionEvent.ACTION_MOVE:
                int dis = (int) (mDownX - event.getX());
                if (state == STATE_OPEN) {
                    dis += mMenuView.getWidth() * mSwipeDirection;
                }
                swipe(dis);
                break;

            case MotionEvent.ACTION_UP:
                if ((isFling || Math.abs(mDownX - event.getX()) > (mMenuView.getWidth() / 3)) && Math.signum(mDownX - event.getX()) == mSwipeDirection) {
                    smoothOpenMenu();
                } else {
                    smoothCloseMenu();
                    return false;
                }
                break;
        }
        return true;
    }

    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    private void swipe(int dis) {
        if (Math.signum(dis) != mSwipeDirection) {
            dis = 0;
        } else if (Math.abs(dis) > mMenuView.getWidth()) {
            dis = mMenuView.getWidth() * mSwipeDirection;
            state = STATE_OPEN;
        }

        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        int lGap = getPaddingLeft() + lp.leftMargin;
        mContentView.layout(lGap - dis, mContentView.getTop(), lGap + (OVER_API_11 ? mContentView.getMeasuredWidthAndState() : mContentView.getMeasuredWidth()) - dis, mContentView.getBottom());

        if (mSwipeDirection == AlleySwipeRecyclerView.DIRECTION_LEFT) {
            mMenuView.layout(getMeasuredWidth() - dis, mMenuView.getTop(), getMeasuredWidth() + (OVER_API_11 ? mMenuView.getMeasuredWidthAndState() : mMenuView.getMeasuredWidth()) - dis, mMenuView.getBottom());
        } else {
            mMenuView.layout(-(OVER_API_11 ? mMenuView.getMeasuredWidthAndState() : mMenuView.getMeasuredWidth()) - dis, mMenuView.getTop(), -dis, mMenuView.getBottom());
        }
    }

    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX() * mSwipeDirection);
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe((mBaseX - mCloseScroller.getCurrX()) * mSwipeDirection);
                postInvalidate();
            }
        }
    }

    public void smoothOpenMenu() {
        state = STATE_OPEN;
        if (mSwipeDirection == AlleySwipeRecyclerView.DIRECTION_LEFT) {
            mOpenScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, animDuration);
        } else {
            mOpenScroller.startScroll(mContentView.getLeft(), 0, mMenuView.getWidth(), 0, animDuration);
        }
        postInvalidate();
    }

    public void openMenu() {
        if (state == STATE_CLOSE) {
            state = STATE_OPEN;
            swipe(mMenuView.getWidth() * mSwipeDirection);
        }
    }

    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        if (mSwipeDirection == AlleySwipeRecyclerView.DIRECTION_LEFT) {
            mBaseX = -mContentView.getLeft();
            mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, animDuration);
        } else {
            mBaseX = mMenuView.getRight();
            mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, animDuration);
        }
        postInvalidate();
    }

    public void closeMenu() {
        if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            swipe(0);
        }
    }

    public View getMenuView() {
        return mMenuView;
    }

    public View getContentView() {
        return mContentView;
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    public void setSwipeDirection(int swipeDirection) {
        mSwipeDirection = swipeDirection;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        int lGap = getPaddingLeft() + lp.leftMargin;
        int tGap = getPaddingTop() + lp.topMargin;
        mContentView.layout(lGap, tGap, lGap + (OVER_API_11 ? mContentView.getMeasuredWidthAndState() : mContentView.getMeasuredWidth()), tGap + (OVER_API_11 ? mContentView.getMeasuredHeightAndState() : mContentView.getMeasuredHeight()));


        lp = (LayoutParams) mMenuView.getLayoutParams();
        tGap = getPaddingTop() + lp.topMargin;
        if (mSwipeDirection == AlleySwipeRecyclerView.DIRECTION_LEFT) {
            mMenuView.layout(getMeasuredWidth(), tGap, getMeasuredWidth() + (OVER_API_11 ? mMenuView.getMeasuredWidthAndState() : mMenuView.getMeasuredWidth()), tGap + mMenuView.getMeasuredHeightAndState());
        } else {
            mMenuView.layout(-(OVER_API_11 ? mMenuView.getMeasuredWidthAndState() : mMenuView.getMeasuredWidth()), tGap, 0, tGap + mMenuView.getMeasuredHeightAndState());
        }
    }
}
