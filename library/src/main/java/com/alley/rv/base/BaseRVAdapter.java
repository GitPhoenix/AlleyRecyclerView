package com.alley.rv.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alley.rv.helper.AlleyAnimationListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * RecyclerView数据适配器
 *
 * @author Phoenix
 * @date 2017/4/13 16:54
 */
public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    public static class ViewType {
        public static final int HEADER = 0x0010;
        public static final int FOOTER = 0x0011;
    }

    /**
     * Base config
     */
    protected List<T> listBody;
    protected Context mContext;
    private LayoutInflater mInflater;

    /**
     * Listener
     */
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnRecyclerItemChildClickListener mChildClickListener;
    private OnRecyclerItemChildLongClickListener mChildLongClickListener;

    /**
     * View type
     */
    private SparseIntArray layoutIdMap, viewTypeMap;
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

    public BaseRVAdapter(Context context, List<T> listBody) {
        this.listBody = listBody != null ? listBody : new ArrayList<T>();
        layoutIdMap = new SparseIntArray();
        viewTypeMap = new SparseIntArray();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder;
        switch (viewType) {
            //header
            case ViewType.HEADER:
                baseViewHolder = new BaseViewHolder(mHeaderLayout, mContext);
                break;
            //footer
            case ViewType.FOOTER:
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
    private void initItemClickListener(final BaseViewHolder holder) {
        if (null != mOnItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = holder.getAdapterPosition() - getHeaderViewCount() - 1;
                    mOnItemClickListener.onItemClick(view, listBody.get(position), position);
                }
            });
        }

        if (null != mOnItemLongClickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int position = holder.getAdapterPosition() - getHeaderViewCount() - 1;
                    mOnItemLongClickListener.onItemLongClick(v, listBody.get(position), position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderViewCount()) {
            return ViewType.HEADER;
        } else if (position >= listBody.size() + getHeaderViewCount()) {
            return ViewType.FOOTER;
        } else {
            int currentPosition = position - getHeaderViewCount();
            int currentLayoutId = getItemLayoutID(currentPosition);
            if (viewTypeMap.get(currentLayoutId) == 0) {
                mCurrentViewTypeValue++;
                viewTypeMap.put(currentLayoutId, mCurrentViewTypeValue);
                layoutIdMap.put(viewTypeMap.get(currentLayoutId), currentLayoutId);
            }
            return viewTypeMap.get(currentLayoutId);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ViewType.HEADER:
                // Do nothing
                break;
            case ViewType.FOOTER:
                // Do nothing
                break;
            default:
                int currentPosition = position - getHeaderViewCount();
                convert(holder, getItem(currentPosition), currentPosition);
                addAnimation(holder);
                break;
        }
    }

    protected T getItem(int position) {
        return listBody.get(position);
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

    @Override
    public int getItemCount() {
        return listBody.size() + getHeaderViewCount() + getFooterViewCount();
    }

    public class OnItemChildClickListener implements View.OnClickListener {
        public RecyclerView.ViewHolder mViewHolder;

        @Override
        public void onClick(View v) {
            if (mChildClickListener != null) {
                mChildClickListener.onItemChildClick(BaseRVAdapter.this, v, mViewHolder.getLayoutPosition() - getHeaderViewCount() - 1);
            }
        }
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
        if (mHeaderLayout != null) {
            mHeaderLayout.removeView(header);
            if (mHeaderLayout.getChildCount() == 0) {
                mHeaderLayout = null;
            }
            this.notifyDataSetChanged();
        }
    }

    public void removeAllHeaderView() {
        if (mHeaderLayout != null) {
            mHeaderLayout.removeAllViews();
            mHeaderLayout = null;
        }
    }

    public void removeFooterView(View footer) {
        if (mFooterLayout != null) {
            mFooterLayout.removeView(footer);
            if (mFooterLayout.getChildCount() == 0) {
                mFooterLayout = null;
            }
            this.notifyDataSetChanged();
        }
    }

    public void removeAllFooterView() {
        if (mFooterLayout != null) {
            mFooterLayout.removeAllViews();
            mFooterLayout = null;
        }
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

    public int getHeaderViewCount() {
        return null == mHeaderLayout ? 0 : 1;
    }

    public int getFooterViewCount() {
        return null == mFooterLayout ? 0 : 1;
    }

    public interface OnItemClickListener<T> {
        /**
         * item点击回调
         *
         * @param view
         * @param item
         * @param position
         */
        void onItemClick(View view, T item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener<T> {
        /**
         * item长按回调
         *
         * @param view
         * @param item
         * @param position
         */
        void onItemLongClick(View view, T item, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnRecyclerItemChildClickListener {
        /**
         * item子控件点击回调
         *
         * @param adapter
         * @param view
         * @param position
         */
        void onItemChildClick(BaseRVAdapter adapter, View view, int position);
    }

    /**
     * Register a callback to be invoked when childView in this AdapterView has
     * been clicked and held
     * {@link OnRecyclerItemChildClickListener}
     *
     * @param childClickListener The callback that will run
     */
    public void setOnItemChildClickListener(OnRecyclerItemChildClickListener childClickListener) {
        this.mChildClickListener = childClickListener;
    }

    public interface OnRecyclerItemChildLongClickListener {
        /**
         * item子控件长按回调
         *
         * @param adapter
         * @param view
         * @param position
         * @return
         */
        boolean onItemChildLongClick(BaseRVAdapter adapter, View view, int position);
    }

    /**
     * Register a callback to be invoked when childView in this AdapterView has
     * been longClicked and held
     * {@link OnRecyclerItemChildLongClickListener}
     *
     * @param childLongClickListener The callback that will run
     */
    public void setOnItemChildLongClickListener(OnRecyclerItemChildLongClickListener childLongClickListener) {
        this.mChildLongClickListener = childLongClickListener;
    }
}
