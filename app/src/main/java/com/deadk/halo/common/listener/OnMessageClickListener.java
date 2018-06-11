package com.deadk.halo.common.listener;

import com.deadk.halo.dao.model.Message;

/**
 * Interface definition for a callback to be invoked when message item is clicked.
 */
public interface OnMessageClickListener<MessageObj extends Message> {

    /**
     * Fires when message is clicked.
     *
     * @param message clicked message.
     */
    void onMessageClick(MessageObj message);
}
