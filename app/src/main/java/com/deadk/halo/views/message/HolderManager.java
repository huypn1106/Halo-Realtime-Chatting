package com.deadk.halo.views.message;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.common.ViewHolder;
import com.deadk.halo.common.models.MessageContentType;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.DateFormatter;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;
import com.deadk.halo.views.message.holder.DateHeaderViewHolder;
import com.deadk.halo.views.message.holder.IncomingImageMessageViewHolder;
import com.deadk.halo.views.message.holder.IncomingTextMessageViewHolder;
import com.deadk.halo.views.message.holder.IncomingVoiceMessageViewHolder;
import com.deadk.halo.views.message.holder.OutcomingImageMessageViewHolder;
import com.deadk.halo.views.message.holder.OutcomingTextMessageViewHolder;
import com.deadk.halo.views.message.holder.OutcomingVoiceMessageViewHolder;

import java.lang.reflect.Constructor;
import java.util.Date;

public class HolderManager {
    private static final short VIEW_TYPE_DATE_HEADER = 1;
    private static final short VIEW_TYPE_TEXT_MESSAGE = 2;
    private static final short VIEW_TYPE_IMAGE_MESSAGE = 3;
    private static final short VIEW_TYPE_VOICE_MESSAGE = 4;

    private int dateHeaderLayout;
    private Context context;
    private String dialogId;
    private Class<DateHeaderViewHolder> dateHeaderHolder;

    public HolderManager() {
        this.dateHeaderLayout = R.layout.item_date_header;
        this.dateHeaderHolder = DateHeaderViewHolder.class;

    }

    private <HOLDER extends ViewHolder>
    ViewHolder getHolder(ViewGroup parent, @LayoutRes int layout, Class<HOLDER> holderClass) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        try {
            Constructor<HOLDER> constructor = holderClass.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            HOLDER holder = constructor.newInstance(v);
            return holder;
        } catch (Exception e) {
            throw new UnsupportedOperationException("Somehow we couldn't create the ViewHolder for message.", e);
        }
    }

    protected ViewHolder getHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DATE_HEADER:
                return getHolder(parent, dateHeaderLayout, dateHeaderHolder);
            case VIEW_TYPE_TEXT_MESSAGE:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_incoming_text_message, parent, false);
                IncomingTextMessageViewHolder holder = new IncomingTextMessageViewHolder(v);
                holder.setContext(context);
                return holder;
            case -VIEW_TYPE_TEXT_MESSAGE:
                return getHolder(parent, R.layout.item_outcoming_text_message, OutcomingTextMessageViewHolder.class);
            case VIEW_TYPE_IMAGE_MESSAGE:
                View v1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_incoming_image_message, parent, false);
                IncomingImageMessageViewHolder holder1 = new IncomingImageMessageViewHolder<>(v1);
                holder1.setContext(context);
                return holder1;
            case -VIEW_TYPE_IMAGE_MESSAGE:
                View v2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_outcoming_image_message, parent, false);
                OutcomingImageMessageViewHolder holder2 = new OutcomingImageMessageViewHolder<>(v2);
                holder2.setContext(context);
                return holder2;
            default:
                if (viewType > 0) {
                    View v3 = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_incoming_voice_message, parent, false);
                    IncomingVoiceMessageViewHolder holder3 = new IncomingVoiceMessageViewHolder<>(v3);
                    holder3.setDialogId(dialogId);
                    return holder3;
                }
                else{
                    return getHolder(parent, R.layout.item_outcoming_voice_message, OutcomingVoiceMessageViewHolder.class);
                }
        }
    }

    //xác định kiểu của tin nhắn để xử lí
    protected int getViewType(Object item, String senderId) {
        boolean isOutcoming = false;
        int viewType;

        if (item instanceof Message) {
            Message message = (Message) item;
            Log.i("id", message.getId() + " "+ senderId);
            isOutcoming = message.getSenderId().contentEquals(senderId);
            viewType = getContentViewType(message);

        } else viewType = VIEW_TYPE_DATE_HEADER;

        return isOutcoming ? viewType * -1 : viewType;
    }

    //xác định nội dung của tin nhắn thuộc dạng nào
    protected short getContentViewType(Message message) {
        if (message instanceof MessageContentType.Image
                && message.getImageUrl() != null) {
            return VIEW_TYPE_IMAGE_MESSAGE;
        }
        if(message instanceof MessageContentType.Voice
                && message.getVoice() != null){
            return VIEW_TYPE_VOICE_MESSAGE;
        }
        return VIEW_TYPE_TEXT_MESSAGE;
    }

    //set các listener và các thành phần cần cho danh sách tin nhắn
    protected void bind(final ViewHolder holder,
                        final Object item,
                        final ImageLoader imageLoader,
                        final MediaPlayer mediaPlayer,
                        final View.OnClickListener onMessageClickListener,
                        final DateFormatter.Formatter dateHeadersFormatter){

        if (item instanceof Message) {
            ((BaseMessageViewHolder) holder).imageLoader = imageLoader;
            ((BaseMessageViewHolder) holder).mediaPlayer = mediaPlayer;
            holder.itemView.setOnClickListener(onMessageClickListener);


        } else if (item instanceof Date) {
            ((DateHeaderViewHolder) holder).dateHeadersFormatter = dateHeadersFormatter;
        }

        holder.onBind(item);
    }
    public void setContext(Context context){
        this.context = context;
    }
    public void setDialogId(String dialogId){
        this.dialogId = dialogId;
    }
}
