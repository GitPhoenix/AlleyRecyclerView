package com.alley.rv.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alley.rv.helper.AlleyAnimationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * RecyclerView数据适配器
 *
 * @author Phoenix
 * @date 2017/4/13 16:54
 */
public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    public static class VIEW_TYPE {
        public static final int HEADER = 0x0010;
        public static final int FOOTER = 0x0011;
    }

    /**
     * Base config
     */
    public List<T> mData;
    private Context mContext;
    private LayoutInflater mInflater;

    /**
     * Listener
     */
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnRecyclerViewItemChildClickListener mChildClickListener;
    private OnRecyclerViewItemChildLongClickListener mChildLongClickListener;

    /**
     * View type
     */
    private Map<Integer, Integer> layoutIdMap, viewTypeMap;
    private int mCurrentViewTypeValue = 0x0107;

    /**
     * Animation
     */
    private AlleyAnimationListener animationListener;
    private boolean itemAnimationRepeat = false;
    private int mLastItemPosition = -1;

    /**
     * header and footer
     */
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private LinearLayout mCopyHeaderLayout = null;
    private LinearLayout mCopyFooterLayout = null;

    public BaseRVAdapter(Context context) {
        this(context, null);
    }

    public BaseRVAdapter(Context context, List<T> data) {
        mData = null == data ? new ArrayList<T>() : data;
        layoutIdMap = new HashMap<>();
        viewTypeMap = new HashMap<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder;
        switch (viewType) {
            case VIEW_TYPE.HEADER://header
                baseViewHolder = new BaseViewHolder(mHeaderLayout, mContext);
                break;

            case VIEW_TYPE.FOOTER://footer
                baseViewHolder = new BaseViewHolder(mFooterLayout, mContext);
                break;

            default:
                baseViewHolder = new BaseViewHolder(mInflater.inflate(layoutIdMap.get(viewType), parent, false), mContext);
                initItemClickListener(baseViewHolder);
                break;
        }
        return baseViewHolder;
    }


    /**
     * init the baseViewHolder to register mOnItemClickListener and mOnItemLongClickListener
     *
     * @param holder
     */
    protected final void initItemClickListener(final BaseViewHolder holder) {
        if (null != mOnItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = holder.getAdapterPosition() - getHeaderViewCount() - 1;
                    mOnItemClickListener.onItemClick(view, mData.get(position), position);
                }
            });
        }

        if (null != mOnItemLongClickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int position = holder.getAdapterPosition() - getHeaderViewCount() - 1;
                    mOnItemLongClickListener.onItemLongClick(v, mData.get(position), position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderViewCount()) {
            return VIEW_TYPE.HEADER;
        } else if (position >= mData.size() + getHeaderViewCount()) {
            return VIEW_TYPE.FOOTER;
        } else {
            int currentPosition = position - getHeaderViewCount();
            int currentLayoutId = getItemLayoutID(currentPosition);
            if (!viewTypeMap.containsKey(currentLayoutId)) {
                mCurrentViewTypeValue++;
                viewTypeMap.put(currentLayoutId, mCurrentViewTypeValue);
                layoutIdMap.put(viewTypeMap.get(currentLayoutId), currentLayoutId);
            }
            return viewTypeMap.get(currentLayoutId);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE.HEADER:
                // Do nothing
                break;
            case VIEW_TYPE.FOOTER:
                // Do nothing
                break;
            default:
                convert(holder, getItem(position - getHeaderViewCount()), position - getHeaderViewCount());
                addAnimation(holder);
                break;
        }
    }

    /**
     * 添加item动画
     *
     * @param holder
     */
    protected final void addAnimation(final BaseViewHolder holder) {
        int currentPosition = holder.getAdapterPosition();
        if (animationListener == null) {
            return;
        }
        if (itemAnimationRepeat || currentPosition > mLastItemPosition) {
            animationListener.setAnimator(holder.itemView);
            mLastItemPosition = currentPosition;
        }
    }

    /**
     * 根据需求返回layoutID
     *
     * @param position index
     * @return
     */
    protected abstract int getItemLayoutID(int position);

    /**
     * onBindViewHolder绑定数据
     *
     * @param holder
     * @param body 数据实体
     * @param position index
     */
    protected abstract void convert(BaseViewHolder holder, T body, int position);

    protected T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size() + getHeaderViewCount() + getFooterViewCount();
    }

    /**
     * Listener api
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * Register a callback to be invoked when childView in this AdapterView has
     * been clicked and held
     * {@link OnRecyclerViewItemChildClickListener}
     *
     * @param childClickListener The callback that will run
     */
    public void setOnItemChildClickListener(OnRecyclerViewItemChildClickListener childClickListener) {
        this.mChildClickListener = childClickListener;
    }

    public class OnItemChildClickListener implements View.OnClickListener {
        public RecyclerView.ViewHolder mViewHolder;

        @Override
        public void onClick(View v) {
            if (mChildClickListener != null)
                mChildClickListener.onItemChildClick(BaseRVAdapter.this, v, mViewHolder.getLayoutPosition() - getHeaderViewCount() - 1);
        }
    }

    /**
     * Register a callback to be invoked when childView in this AdapterView has
     * been longClicked and held
     * {@link OnRecyclerViewItemChildLongClickListener}
     *
     * @param childLongClickListener The callback that will run
     */
    public void setOnItemChildLongClickListener(OnRecyclerViewItemChildLongClickListener childLongClickListener) {
        this.mChildLongClickListener = childLongClickListener;
    }

    public class OnItemChildLongClickListener implements View.OnLongClickListener {
        public RecyclerView.ViewHolder mViewHolder;

        @Override
        public boolean onLongClick(View v) {
            if (mChildLongClickListener != null) {
                return mChildLongClickListener.onItemChildLongClick(BaseRVAdapter.this, v, mViewHolder.getLayoutPosition() - getHeaderViewCount() - 1);
            }
            return false;
        }
    }

    /**
     * 在列表重复滑动时，设置item动画是否执行
     *
     * @param itemAnimationRepeat
     */
    public void setItemAnimationRepeat(boolean itemAnimationRepeat) {
        this.itemAnimationRepeat = itemAnimationRepeat;
    }

    /**
     * 设置自定义item动画类
     *
     * @param alleyAnimationListener
     */
    public void setItemAnimator(AlleyAnimationListener alleyAnimationListener) {
        animationListener = alleyAnimationListener;
    }

    /**
     * Header and footer api
     */
    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    public void addHeaderView(View header) {
        addHeaderView(header, -1);
    }

    public void addHeaderView(View header, int index) {
        if (mHeaderLayout == null) {
            if (mCopyHeaderLayout == null) {
                mHeaderLayout = new LinearLayout(header.getContext());
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                mCopyHeaderLayout = mHeaderLayout;
            } else {
                mHeaderLayout = mCopyHeaderLayout;
            }
        }
        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
        mHeaderLayout.addView(header, index);
        this.notifyDataSetChanged();
    }

    public void addFooterView(View footer) {
        addFooterView(footer, -1);
    }

    public void addFooterView(View footer, int index) {
        if (mFooterLayout == null) {
            if (mCopyFooterLayout == null) {
                mFooterLayout = new LinearLayout(footer.getContext());
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                mCopyFooterLayout = mFooterLayout;
            } else {
                mFooterLayout = mCopyFooterLayout;
            }
        }
        index = index >= mFooterLayout.getChildCount() ? -1 : index;
        mFooterLayout.addView(footer, index);
        this.notifyDataSetChanged();
    }

    public void removeHeaderView(View header) {
        if (mHeaderLayout == null) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            mHeaderLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public void removeFooterView(View footer) {
        if (mFooterLayout == null) return;

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            mFooterLayout = null;
        }
        this.notifyDataSetChanged();
    }

    public void removeAllHeaderView() {
        if (mHeaderLayout == null) return;

        mHeaderLayout.removeAllViews();
        mHeaderLayout = null;
    }

    public void removeAllFooterView() {
        if (mFooterLayout == null) return;

        mFooterLayout.removeAllViews();
        mFooterLayout = null;
    }

    public int getHeaderViewCount() {
        return null == mHeaderLayout ? 0 : 1;
    }

    public int getFooterViewCount() {
        return null == mFooterLayout ? 0 : 1;
    }

    /**
     * Some interface
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View view, T item, int position);
    }

    public interface OnRecyclerViewItemChildClickListener {
        void onItemChildClick(BaseRVAdapter adapter, View view, int position);
    }

    public interface OnRecyclerViewItemChildLongClickListener {
        boolean onItemChildLongClick(BaseRVAdapter adapter, View view, int position);
    }

    /**
     * This is parallax header view wrapper class ,it aim to clip layout height on Y.
     */
    static class CustomRelativeWrapper extends RelativeLayout {

        private int mOffset;
        private boolean mShouldClip;

        public CustomRelativeWrapper(Context context) {
            super(context);
        }

        public CustomRelativeWrapper(Context context, boolean shouldClick) {
            super(context);
            mShouldClip = shouldClick;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (mShouldClip) {
                canvas.clipRect(new Rect(getLeft(), getTop(), getRight(), getBottom() + mOffset));
            }
            super.dispatchDraw(canvas);
        }

        public void setClipY(int offset) {
            mOffset = offset;
            invalidate();
        }
    }
}
