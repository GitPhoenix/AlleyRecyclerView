package com.alley.rv.base;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 万能ViewHolder
 *
 * @author Phoenix
 * @date 2017/4/14 9:14
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private Context mContext;

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        mViews = new SparseArray<>();
        mContext = context;
    }

    public <TView extends View> TView getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (TView) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setTextSize(int viewId, float size) {
        TextView view = getView(viewId);
        view.setTextSize(size);
        return this;
    }

    public BaseViewHolder setImageURI(int viewId, Uri uri) {
        ImageView view = getView(viewId);
        view.setImageURI(uri);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundResource(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setVisibility(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId, BaseRVAdapter.OnItemChildClickListener listener) {
        View view = getView(viewId);
        listener.mViewHolder = this;
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, BaseRVAdapter.OnItemChildLongClickListener listener) {
        View view = getView(viewId);
        listener.mViewHolder = this;
        view.setOnLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }
}
