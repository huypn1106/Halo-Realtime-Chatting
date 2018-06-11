package com.deadk.halo.views.message.holder;

import android.view.View;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;

public class OutcomingTextMessageViewHolder<MessageObj extends Message>
        extends BaseMessageViewHolder<MessageObj> {

    protected TextView messageText;

    public OutcomingTextMessageViewHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.messageText);
    }

    @Override
    public void onBind(MessageObj message) {
        super.onBind(message);

        if (messageText != null) {
            messageText.setText(message.getText());
        }
    }
}
