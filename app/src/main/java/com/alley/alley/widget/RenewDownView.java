package com.alley.alley.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alley.alley.R;
import com.alley.rv.widget.AlleyRenewDownView;

/**
 * 下拉刷新
 *
 * @author Phoenix
 * @date 2016-11-13 16:56
 */
public class RenewDownView extends AlleyRenewDownView {
    private static final int ROTATE_ANIM_DURATION = 180;

    private LinearLayout rootView;
    private ImageView ivIndicator;
    private ProgressBar pbBar;
    private TextView tvHint;
    private Animation mRotateUpAnim, mRotateDownAnim;

    public int mMeasuredHeight;
    private int renewState = RENEW_DOWN_GOING;

    public RenewDownView(Context context) {
        this(context, null);
    }

    public RenewDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAlleyRefreshHeader();
    }

    /**
     * 初始化下拉刷新视图
     */
    private void initAlleyRefreshHeader() {
        // 初始情况，设置下拉刷新view高度为0
        rootView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_recycler_renew_down, null);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(rootView, new LayoutParams(LayoutParams.MATCH_PARENT, 0));

        ivIndicator = (ImageView) findViewById(R.id.iv_recycler_renew_down_indicator);
        pbBar = (ProgressBar) findViewById(R.id.pbBar_recycler_renew_down_anim);
        tvHint = (TextView) findViewById(R.id.tv_recycler_renew_down_hint);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();

        initRotateAnim();
    }

    /**
     * 初始化下拉刷新指示器动画
     */
    private void initRotateAnim() {
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public void onFling(float faction) {
        if (getVisibleHeight() <= 0 && faction <= 0) {
            return;
        }

        setVisibleHeight((int) faction + getVisibleHeight());
        if (renewState > RENEW_DOWN_RELEASE) { // 未处于刷新状态，更新箭头
            return;
        }
        if (getVisibleHeight() > mMeasuredHeight) {
            setRenewState(RENEW_DOWN_RELEASE);
        } else {
            setRenewState(RENEW_DOWN_GOING);
        }
    }

    @Override
    public boolean onUp() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if (getVisibleHeight() > mMeasuredHeight && renewState < RENEW_DOWN_NOW) {
            setRenewState(RENEW_DOWN_NOW);
            isOnRefresh = true;
        }
        // RENEW_DOWN and header isn't shown fully. do nothing.
        if (renewState == RENEW_DOWN_NOW && height <= mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is RENEW_DOWN, just scroll back to show all the header.
        if (renewState == RENEW_DOWN_NOW) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    @Override
    public void setRenewState(int currState) {
        if (renewState == currState) {
            return;
        }

        switch (currState) {
            case RENEW_DOWN_GOING://下拉刷新
                if (renewState == RENEW_DOWN_RELEASE) {
                    ivIndicator.startAnimation(mRotateDownAnim);
                }
                tvHint.setText(R.string.recycler_renew_down_going);
                break;

            case RENEW_DOWN_RELEASE://释放刷新
                renewDownRelease();
                break;

            case RENEW_DOWN_NOW://正在刷新
                renewDownNow();
                break;

            case RENEW_DOWN_END://刷新完成
                renewDownEnd();
                break;

            default:
                break;
        }
        renewState = currState;
    }

    /**
     * 最初状态
     */
    private void renewDownOriginal() {
        ivIndicator.setVisibility(View.VISIBLE);
        pbBar.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(VISIBLE);
        tvHint.setText(R.string.recycler_renew_down_going);
        renewState = RENEW_DOWN_GOING;
    }

    /**
     * 提示用户释放手势，进行刷新操作
     */
    private void renewDownRelease() {
        ivIndicator.clearAnimation();
        ivIndicator.startAnimation(mRotateUpAnim);
        tvHint.setText(R.string.recycler_renew_down_release);
    }

    /**
     * 正在刷新
     */
    private void renewDownNow() {
        ivIndicator.clearAnimation();
        ivIndicator.setVisibility(View.INVISIBLE);
        pbBar.setVisibility(View.VISIBLE);
        tvHint.setText(R.string.recycler_renew_down_now);
    }

    /**
     * 刷新结束
     */
    private void renewDownEnd() {
        ivIndicator.setVisibility(View.INVISIBLE);
        pbBar.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(INVISIBLE);

        smoothScrollTo(0);
        new Handler().postDelayed(() -> renewDownOriginal(), 6 * 100);
    }

    @Override
    public int getRenewState() {
        return renewState;
    }

    /**
     * 动态改变视图高度
     *
     * @param height 目标值
     */
    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) rootView.getLayoutParams();
        lp.height = height;
        rootView.setLayoutParams(lp);
    }

    /**
     * 获取视图，当前高度
     *
     * @return
     */
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) rootView.getLayoutParams();
        return lp.height;
    }

    /**
     * 动态改变视图高度
     *
     * @param destHeight 目标值
     */
    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}