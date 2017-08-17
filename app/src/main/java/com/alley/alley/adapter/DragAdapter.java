package com.alley.alley.adapter;

import android.content.Context;

import com.alley.alley.R;
import com.alley.rv.base.BaseRVDragAdapter;
import com.alley.rv.base.BaseViewHolder;

import java.util.List;


public class DragAdapter extends BaseRVDragAdapter<String> {

    public DragAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item, int position) {
        holder.setText(R.id.tv,item);
    }

    @Override
    protected int getItemLayoutID(int position) {
        return R.layout.adapter_draggable_layout;
    }
}
