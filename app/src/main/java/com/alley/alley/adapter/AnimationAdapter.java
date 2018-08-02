package com.alley.alley.adapter;


import android.content.Context;

import com.alley.alley.R;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.base.BaseViewHolder;

import java.util.List;


public class AnimationAdapter extends BaseRVAdapter<String> {

    public AnimationAdapter(Context context, List<String> listBody) {
        super(context, listBody);
    }

    @Override
    protected int getItemLayoutID(int position) {
        return R.layout.adapter_animation_layout;
    }

    @Override
    protected void convert(BaseViewHolder holder, String body, int position) {
        holder.setText(R.id.tv_text, body + "size: " + listBody.size());

    }
}
