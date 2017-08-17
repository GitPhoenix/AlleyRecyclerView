package com.alley.alley.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alley.alley.R;
import com.alley.alley.adapter.DragAdapter;
import com.alley.alley.widget.RenewDownView;
import com.alley.alley.widget.RenewUpView;
import com.alley.rv.base.BaseRVAdapter;
import com.alley.rv.base.BaseViewHolder;
import com.alley.rv.helper.ItemDragCallback;
import com.alley.rv.helper.OnItemDragListener;
import com.alley.rv.widget.AlleyRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class DragActivity extends Activity implements AlleyRecyclerView.OnDataRenewListener, BaseRVAdapter.OnItemClickListener {

    private AlleyRecyclerView mRecyclerView;
    private List<String> mData;
    private DragAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemDragCallback mItemDragAndSwipeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_layout);
        mRecyclerView = (AlleyRecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setRenewDownEnable(false);
        mRecyclerView.setRenewUpEnable(false);
        mRecyclerView.setRenewDownView(new RenewDownView(this));
        mRecyclerView.setRenewUpView(new RenewUpView(this));
        mRecyclerView.addOnDataRenewListener(this);

        mData = generateData(50);
        OnItemDragListener listener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                mRecyclerView.setRenewDownEnable(false);//在开始的时候需要禁止下拉刷新，不然在下滑动的时候会与下拉刷新冲突
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                holder.setTextColor(R.id.tv, Color.WHITE);
                ((CardView) viewHolder.itemView).setCardBackgroundColor(ContextCompat.getColor(DragActivity.this, R.color.colorAccent));
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                mRecyclerView.setRenewDownEnable(true);//在结束之后需要开启下拉刷新
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                holder.setTextColor(R.id.tv, Color.BLACK);
                ((CardView) viewHolder.itemView).setCardBackgroundColor(Color.WHITE);
            }
        };
        mAdapter = new DragAdapter(this, mData);
        mItemDragAndSwipeCallback = new ItemDragCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        mAdapter.enableDragItem(mItemTouchHelper);
        mAdapter.setOnItemDragListener(listener);

        mAdapter.setOnItemClickListener(this);

        View headerView = getLayoutInflater().inflate(R.layout.view_header_layout, (ViewGroup) mRecyclerView.getParent(), false);
        View footerView = getLayoutInflater().inflate(R.layout.view_footer_layout, (ViewGroup) mRecyclerView.getParent(), false);
//        mAdapter.addHeaderView(headerView);
//        mAdapter.addFooterView(footerView);

        mRecyclerView.setAdapter(mAdapter);
    }

    private List<String> generateData(int size) {
        ArrayList<String> data = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            data.add("item " + i);
        }
        return data;
    }

    @Override
    public void onRenewDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.endRenewDown();
            }
        }, 2000);
    }

    @Override
    public void onRenewUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.endRenewUp();
                mRecyclerView.setRenewNever();
            }
        }, 2000);
    }

    @Override
    public void onItemClick(View view, Object item, int position) {
        Toast.makeText(this, mData.get(position), Toast.LENGTH_SHORT).show();
    }

}
