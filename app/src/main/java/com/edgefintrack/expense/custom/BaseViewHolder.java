package com.edgefintrack.expense.custom;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    protected RecyclerClickListener onRecyclerClickListener;

    public BaseViewHolder(View v, RecyclerClickListener onRecyclerClickListener) {
        super(v);
        this.onRecyclerClickListener = onRecyclerClickListener;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerClickListener != null)
            onRecyclerClickListener.onClick(this, getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerClickListener != null)
            onRecyclerClickListener.onLongClick(this, getAdapterPosition());
        return true;
    }

    public interface RecyclerClickListener {
        void onClick(RecyclerView.ViewHolder vh, int position);

        void onLongClick(RecyclerView.ViewHolder vh, int position);
    }

}
