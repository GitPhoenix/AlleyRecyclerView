package com.alley.rv.helper;

import android.support.v7.widget.RecyclerView;


public interface OnItemDragListener {
    /**
     * 拖拽开始，禁止刷新
     *
     * @param viewHolder
     * @param pos
     */
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    /**
     * 正在拖拽过程中
     *
     * @param source
     * @param from
     * @param target
     * @param to
     */
    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    /**
     * 拖拽结束，开启刷新
     *
     * @param viewHolder
     * @param pos
     */
    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
