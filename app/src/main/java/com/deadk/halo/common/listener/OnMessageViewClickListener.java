package com.deadk.halo.common.listener;

import android.view.View;

import com.deadk.halo.dao.model.Message;

public interface OnMessageViewClickListener<MessageObj extends Message> {

    /**
     * Fires when message view is clicked.
     *
     * @param message clicked message.
     */
    void onMessageViewClick(View view, MessageObj message);
}
