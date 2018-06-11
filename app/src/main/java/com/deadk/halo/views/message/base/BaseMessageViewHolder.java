package com.deadk.halo.views.message.base;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.common.ViewHolder;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.DateFormatter;

import java.io.IOException;
import java.util.Date;

public class BaseMessageViewHolder<MessageObj extends Message> extends ViewHolder<MessageObj> {
    protected TextView messageTime;
    public ImageLoader imageLoader;
    public MediaPlayer mediaPlayer;
    protected DateFormatter.Formatter datesFormatter;

    public BaseMessageViewHolder(final View itemView) {
        super(itemView);
        messageTime = itemView.findViewById(R.id.messageTime);

    }

    @Override
    public void onBind(MessageObj messageObj) {
        //Set nhãn thời gian
        String formattedDate = null;
        Date lastMessageDate = messageObj.getCreatedAt();
        if (datesFormatter != null) formattedDate = datesFormatter.format(lastMessageDate);
        messageTime.setText(formattedDate == null
                ? getDateString(lastMessageDate)
                : formattedDate);

        mediaPlayer = new MediaPlayer() {
            @Override
            public android.media.MediaPlayer playAudioRaw(int rawID) {
                return android.media.MediaPlayer.create(itemView.getContext(), rawID);
            }

            @Override
            public android.media.MediaPlayer playAudioUrl(String url) {
                android.media.MediaPlayer mp = new android.media.MediaPlayer();
                try {
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    Log.e("Play ex", "prepare() failed");
                }
                return mp;
            }
        };
    }
    protected String getDateString(Date date) {
        return DateFormatter.format(date, DateFormatter.Template.TIME);
    }
}
