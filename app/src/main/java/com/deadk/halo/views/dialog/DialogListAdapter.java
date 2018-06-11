package com.deadk.halo.views.dialog;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.listener.OnDialogClickListener;
import com.deadk.halo.common.models.IDialog;
import com.deadk.halo.dao.model.Dialog;
import com.deadk.halo.ultilities.DateFormatter;

import java.util.ArrayList;
import java.util.List;

public class DialogListAdapter<DIALOG extends IDialog> extends RecyclerView.Adapter<DialogViewHolder> {
    private List<DIALOG> items = new ArrayList<>();

    private ImageLoader imageLoader;
    private OnDialogClickListener<DIALOG> onDialogClickListener;
    private DateFormatter.Formatter datesFormatter;
    private Context context;
    private String uid;

    private int itemLayoutId;


    public DialogListAdapter(@LayoutRes int itemLayoutId, ImageLoader imageLoader) {
        this.itemLayoutId = itemLayoutId;
        this.imageLoader = imageLoader;
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false);
        DialogViewHolder dv = new DialogViewHolder(v);
        dv.setContext(this.context);
        dv.setUid(this.uid);
        return dv;
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder holder, int position) {
        holder.setImageLoader(imageLoader);
        holder.setOnDialogClickListener(onDialogClickListener);
        holder.setDatesFormatter(datesFormatter);
        holder.onBind((Dialog) items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<DIALOG> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnDialogClickListener(OnDialogClickListener<DIALOG> onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    public void setContext(Context context){
        this.context = context;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
}
