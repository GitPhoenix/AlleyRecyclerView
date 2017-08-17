package com.alley.alley.adapter;

import android.content.Context;
import android.view.View;

import com.alley.alley.R;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.base.BaseViewHolder;
import com.alley.rv.widget.AlleySwipeLayout;

import java.util.List;


public class SwipeMenuAdapter extends BaseRVAdapter<String> {

    public SwipeMenuAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item, int position) {
        final AlleySwipeLayout superSwipeMenuLayout = (AlleySwipeLayout) holder.itemView;
        superSwipeMenuLayout.setSwipeEnable(true);//设置是否可以侧滑
        if (position % 3 == 0) {
            holder.setText(R.id.name_tv, item);
            holder.setOnClickListener(R.id.btFavorite, new OnItemChildClickListener());
            holder.setOnClickListener(R.id.btGood, new OnItemChildClickListener());
            holder.setOnClickListener(R.id.image_iv, new OnItemChildClickListener());
        } else {
            holder.setText(R.id.name_tv, item);
            holder.setOnClickListener(R.id.btOpen, new OnItemChildClickListener());
            holder.setOnClickListener(R.id.btDelete, new OnItemChildClickListener());
            holder.setOnClickListener(R.id.image_iv, new OnItemChildClickListener());
            /**
             * 设置可以非滑动触发的开启菜单
             */
            holder.getView(R.id.image_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (superSwipeMenuLayout.isOpen()) {
                        superSwipeMenuLayout.closeMenu();
                    } else {
                        superSwipeMenuLayout.openMenu();
                    }
                }
            });
        }
    }

    @Override
    protected int getItemLayoutID(int position) {
        if (position % 3 == 0) {
            return R.layout.adapter_swipemenu1_layout;
        } else {
            return R.layout.adapter_swipemenu_layout;
        }
    }
}
