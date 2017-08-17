package com.alley.alley.widget;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.alley.R;
import com.alley.rv.widget.AlleyRenewUpView;

/**
 * 上拉加载更多
 *
 * @author Phoenix
 * @date 2016-11-13 16:52
 */
public class RenewUpView extends AlleyRenewUpView {
    private TextView tvHint;
    private ImageView ivAnim;
    private AnimationDrawable animDrawable;

    private int renewState = 0;

    public RenewUpView(Context context) {
        this(context, null);
    }

    public RenewUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAlleyRenewUpView();
    }

    /**
     * 初始化上拉加载布局
     */
    public void initAlleyRenewUpView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_recycler_renew_up, null);
        setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(view);

        ivAnim = (ImageView) view.findViewById(R.id.iv_recycler_renew_up_anim);
        tvHint = (TextView) view.findViewById(R.id.tv_recycler_renew_up_hint);

        animDrawable = (AnimationDrawable) ivAnim.getBackground();
    }

    @Override
    public void setRenewState(int state) {
        if (renewState == state) {
            return;
        }

        switch (state) {
            case RENEW_UP_NOW://正在加载中
                this.setVisibility(View.VISIBLE);
                animDrawable.start();
                tvHint.setText(R.string.recycler_renew_up_now);
                break;

            case RENEW_UP_END://上拉加载完成
                animDrawable.stop();
                this.setVisibility(View.GONE);
                break;

            case RENEW_UP_NEVER://没有数据了
                animDrawable.stop();
                tvHint.setText(R.string.recycler_renew_up_never);
                this.setVisibility(View.GONE);
                break;

            default:
                break;
        }
        renewState = state;
    }

    @Override
    public int getRenewState() {
        return renewState;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animDrawable != null && animDrawable.isRunning()) {
            animDrawable.stop();
        }
    }
}
