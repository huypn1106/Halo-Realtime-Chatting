package com.deadk.halo.views.dialog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;

import com.deadk.halo.common.models.IDialog;;

public class DialogList extends RecyclerView {
    public DialogList(Context context) {
        super(context);
    }

    public DialogList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SimpleItemAnimator animator = new DefaultItemAnimator();

        setLayoutManager(layout);
        setItemAnimator(animator);
    }

    public <DIALOG extends IDialog>
    void setAdapter(DialogListAdapter<DIALOG> adapter) {
        //reverse: xét item thêm vào trên hay dưới
        setAdapter(adapter, false);
    }

    public <DIALOG extends IDialog>
    void setAdapter(DialogListAdapter<DIALOG> adapter, boolean reverseLayout) {
        SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, reverseLayout);

        setItemAnimator(itemAnimator);
        setLayoutManager(layoutManager);

        super.setAdapter(adapter);
    }
}
