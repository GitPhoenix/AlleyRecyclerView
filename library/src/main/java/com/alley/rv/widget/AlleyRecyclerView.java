package com.alley.rv.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.alley.rv.helper.AppBarStateChangeListener;

/**
 * RecyclerView下拉刷新、上拉加载
 *
 * @author Phoenix
 * @date 2017/4/13 14:37
 */
public class AlleyRecyclerView extends RecyclerView {
    private static final float DRAG_RATE = 3;
    //下面的ItemViewType是保留值(ReservedItemViewType), 如果用户的adapter与它们重复将会强制抛出异常。
    // 不过为了简化, 我们检测到重复时对用户的提示是ItemViewType必须小于10000
    //设置一个很大的数字, 尽可能避免和用户的adapter冲突
    private static final int TYPE_REFRESH_HEADER = 100000;
    private static final int TYPE_LOADING_FOOTER = 100001;

    private float mLastY = -1;
    //下拉刷新状态标识量
    private boolean isRefreshing = false;
    //上拉加载状态标示量
    private boolean isLoading = false;
    //没有数据标识量，再也不会进行刷新了
    private boolean isNever = false;
    //下拉刷新
    private AlleyRenewDownView renewDownView;
    //上拉加载视图
    private AlleyRenewUpView renewUpView;
    //是否允许【下拉刷新】标识量
    private boolean renewDownEnable = false;
    //是否允许【上拉加载】标识量
    private boolean renewUpEnable = false;

    private WrapAdapter mWrapAdapter;
    private final AdapterDataObserver mDataObserver = new DataObserver();
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;

    private OnDataRenewListener onDataRenewListener;

    public AlleyRecyclerView(Context context) {
        this(context, null);
    }

    public AlleyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlleyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决和CollapsingToolbarLayout冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout == null) {
                return;
            }
            appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                @Override
                public void onStateChanged(AppBarLayout appBarLayout, State state) {
                    appbarState = state;
                }
            });
        }
    }

    /**
     * 设置是否允许执行下拉刷新
     *
     * @param enable
     */
    public void setRenewDownEnable(boolean enable) {
        renewDownEnable = enable;
    }

    /**
     * 设置是否允许执行上拉加载
     *
     * @param enable
     */
    public void setRenewUpEnable(boolean enable) {
        renewUpEnable = enable;
    }

    /**
     * 自定义下拉刷新视图
     *
     * @param renewDownView
     */
    public void setRenewDownView(AlleyRenewDownView renewDownView) {
        if (!(renewDownView instanceof AlleyRenewDownView)) {
            throw new IllegalArgumentException("下拉刷新视图必须继承AlleyRenewDownView");
        }
        this.renewDownView = renewDownView;
    }

    /**
     * 自定义上拉加载视图
     *
     * @param renewUpView
     */
    public void setRenewUpView(AlleyRenewUpView renewUpView) {
        if (!(renewUpView instanceof AlleyRenewUpView)) {
            throw new IllegalArgumentException("上拉加载视图必须继承AlleyRenewUpView");
        }
        this.renewUpView = renewUpView;
        renewUpView.setVisibility(GONE);
    }

    /**
     * 设置下拉刷新结束
     */
    public void endRenewDown() {
        if (renewDownView == null) {
            return;
        }

        isRefreshing = false;
        renewDownView.setRenewState(AlleyRenewDownView.RENEW_DOWN_END);
    }

    /**
     * 设置上拉加载结束
     */
    public void endRenewUp() {
        if (renewUpView == null) {
            return;
        }

        isLoading = false;
        //加载完成后，往回滑动8px,防止再次触发上拉加载
        if (renewUpView.getMeasuredHeight() > 0) {
            smoothScrollBy(0, -(renewUpView.getMeasuredHeight() + 8));
        }
        renewUpView.setRenewState(AlleyRenewUpView.RENEW_UP_END);
    }

    /**
     * 当上拉加载没有更多数据时，设置上拉加载动画不显示
     */
    public void setRenewNever() {
        if (renewUpView == null) {
            return;
        }

        isNever = false;
        renewUpView.setRenewState(AlleyRenewUpView.RENEW_UP_NEVER);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (!renewUpEnable || onDataRenewListener == null) {
            return;
        }
        if (isNever || isLoading || isRefreshing) {
            return;
        }
        if (state != RecyclerView.SCROLL_STATE_IDLE) {
            return;
        }

        LayoutManager layoutManager = getLayoutManager();
        int lastVisibleItemPosition;
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
            lastVisibleItemPosition = findMax(into);
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()) {
            isLoading = true;
            renewUpView.setRenewState(AlleyRenewUpView.RENEW_UP_NOW);
            onDataRenewListener.onRenewUp();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!renewDownEnable) {
            return super.onTouchEvent(ev);
        }

        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    renewDownView.onFling(deltaY / DRAG_RATE);
                    if (renewDownView.getHeight() > 0 && renewDownView.getRenewState() < AlleyRenewDownView.RENEW_DOWN_NOW) {
                        return false;
                    }
                }
                break;

            default:
                mLastY = -1; // reset
                if (isOnTop() && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (renewDownView.onUp() && onDataRenewListener != null) {
                        isRefreshing = true;
                        onDataRenewListener.onRenewDown();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 当LayoutManager为瀑布流(StaggeredGridLayoutManager)的时候，
     * 计算最后一个Item的Position
     *
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private boolean isOnTop() {
        return renewDownView.getParent() != null;
    }

    /**
     * 判断是否是RecyclerView保留的itemViewType
     *
     * @param itemViewType
     * @return
     */
    private boolean isReservedItemViewType(int itemViewType) {
        return (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_LOADING_FOOTER);
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public class WrapAdapter extends Adapter<ViewHolder> {
        private Adapter adapter;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        /**
         * 判断是否是第一个Item
         *
         * @param position
         * @return
         */
        public boolean isHeader(int position) {
            return position >= 1 && position < 1;
        }

        /**
         * 判断是否是最后一个Item
         *
         * @param position
         * @return
         */
        public boolean isFooter(int position) {
            if (renewUpEnable) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(renewDownView);
            } else if (viewType == TYPE_LOADING_FOOTER) {
                return new SimpleViewHolder(renewUpView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            int adjPosition = position - 1;
            int adapterCount;
            if (adapter == null) {
                return;
            }
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                adapter.onBindViewHolder(holder, adjPosition);
                return;
            }
        }

        @Override
        public int getItemCount() {
            if (renewUpEnable) {
                if (adapter != null) {
                    return adapter.getItemCount() + 2;
                } else {
                    return 2;
                }
            } else {
                if (adapter != null) {
                    return adapter.getItemCount() + 1;
                } else {
                    return 1;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - 1;
            if (isReservedItemViewType(adapter.getItemViewType(adjPosition))) {
                throw new IllegalStateException("RecyclerView require itemViewType in adapter should be less than 10000 ");
            }
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_LOADING_FOOTER;
            }

            int adapterCount;
            if (adapter == null) {
                return 0;
            }
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                return adapter.getItemViewType(adjPosition);
            }

            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= 1) {
                int adjPosition = position - 1;
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position)) ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {

            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public interface OnDataRenewListener {
        /**
         * 下拉刷新
         */
        void onRenewDown();

        /**
         * 上拉加载
         */
        void onRenewUp();
    }

    /**
     * 下拉刷新、上拉加载监听
     *
     * @param listener
     */
    public void addOnDataRenewListener(OnDataRenewListener listener) {
        onDataRenewListener = listener;
    }
}