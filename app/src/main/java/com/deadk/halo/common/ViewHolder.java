package com.deadk.halo.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ViewHolder<DATA> extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void onBind(DATA data);
}
