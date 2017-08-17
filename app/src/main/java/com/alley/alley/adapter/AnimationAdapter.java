package com.alley.alley.adapter;


import android.content.Context;

import com.alley.alley.R;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.base.BaseViewHolder;

import java.util.List;


public class AnimationAdapter extends BaseRVAdapter<String> {


    public AnimationAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item, int position) {

    }

    @Override
    protected int getItemLayoutID(int position) {
        return R.layout.adapter_animation_layout;
    }
}
