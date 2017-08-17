package com.alley.alley.anim;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.alley.rv.helper.AlleyAnimationListener;

public class XAnimation implements AlleyAnimationListener {

    @Override
    public void setAnimator(View targetView) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(targetView, "translationX", -targetView.getRootView().getWidth(), 0));
        animatorSet.setDuration(300);
        animatorSet.start();
    }
}
