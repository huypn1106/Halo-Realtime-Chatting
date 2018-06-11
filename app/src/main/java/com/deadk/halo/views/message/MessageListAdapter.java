package com.deadk.halo.views.message;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.common.ViewHolder;
import com.deadk.halo.common.listener.OnMessageClickListener;
import com.deadk.halo.common.listener.OnMessageViewClickListener;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.DateFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MessageListAdapter<MessageObj extends Message> extends RecyclerView.Adapter<ViewHolder>
        implements RecyclerScrollMoreListener.OnLoadMoreListener {

    private HolderManager holderManager;
    private String senderId;
    private List<Wrapper> items;

    private ImageLoader imageLoader;
    private MediaPlayer mediaPlayer;
    private OnLoadMoreListener loadMoreListener;
    private OnMessageViewClickListener<MessageObj> onMessageViewClickListener;
    private OnMessageClickListener<MessageObj> onMessageClickListener;

    private RecyclerView.LayoutManager layoutManager;
    private DateFormatter.Formatter dateHeadersFormatter;


    public MessageListAdapter(String senderId, HolderManager holderManager,
                               ImageLoader imageLoader, MediaPlayer mediaPlayer, List<MessageObj> messageList) {
        this.senderId = senderId;
        this.holderManager = holderManager;
        this.imageLoader = imageLoader;
        this.mediaPlayer = mediaPlayer;
        items = new ArrayList<>();
        for (int i=0; i<messageList.size(); i++){
            Wrapper<MessageObj> element = new Wrapper<>(messageList.get(i));
            items.add(0, element);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return holderManager.getHolder(parent, viewType);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holderManager.bind(holder,
                items.get(position).item,
                imageLoader,
                mediaPlayer,
                getMessageClickListener(items.get(position)),
                dateHeadersFormatter);
    }

    @Override
    public int getItemViewType(int position) {
        return holderManager.getViewType(items.get(position).item, senderId);
    }

    //sự kiện cuộn trên màn hình nhắn tin
    @Override
    public void onLoadMore(int page, int total) {
        if (loadMoreListener != null) {
            loadMoreListener.onLoadMore(page, total);
        }
    }

    //thêm tin nhắn mới vào giao diện nhắn tin
    public void addToStart(MessageObj message, boolean scroll) {
        boolean isNewMessageToday = false;

        isNewMessageToday = !isPreviousSameDate(0, message.getCreatedAt());
        if (isNewMessageToday) {
            items.add(0, new Wrapper<>(message.getCreatedAt()));
        }

        Wrapper<MessageObj> element = new Wrapper<>(message);
        items.add(0,element);
        //notifyItemRangeInserted(items.size()-1, isNewMessageToday ? 2 : 1);
        notifyDataSetChanged();;
        if (layoutManager != null && scroll) {
            layoutManager.scrollToPosition(0);
        }
    }

    public void addToStart(List<MessageObj> message, boolean scroll) {
        for (int i=0; i< message.size(); i++) {
            boolean isNewMessageToday = !isPreviousSameDate(0, message.get(i).getCreatedAt());
            if (isNewMessageToday) {
                items.add(0, new Wrapper<>(message.get(i).getCreatedAt()));
            }

            Wrapper<MessageObj> element = new Wrapper<>(message.get(i));
            items.add(0, element);
            notifyItemRangeInserted(0, isNewMessageToday ? 2 : 1);
            if (layoutManager != null && scroll) {
                layoutManager.scrollToPosition(0);
            }
        }
    }

    //thêm các tin nhắn vào phía trên giao diện khi thực hiện cuộn
    public void addToEnd(List<MessageObj> messages, boolean reverse) {
        if (reverse) Collections.reverse(messages);

        if (!items.isEmpty()) {
            int lastItemPosition = items.size() - 1;
            Date lastItem = (Date) items.get(lastItemPosition).item;
            if (DateFormatter.isSameDay(messages.get(0).getCreatedAt(), lastItem)) {
                items.remove(lastItemPosition);
                notifyItemRemoved(lastItemPosition);
            }
        }

        int oldSize = items.size();
        generateDateHeaders(messages);
        notifyItemRangeInserted(oldSize, items.size() - oldSize);
    }

    //kiểm tra tin nhắn có cùng ngày với tin nhắn cuối cùng không
    private boolean isPreviousSameDate(int position, Date dateToCompare) {
        if (items.size() <= position) return false;
        if (items.get(position).item instanceof Message) {
            Date previousPositionDate = ((MessageObj) items.get(position).item).getCreatedAt();
            return DateFormatter.isSameDay(dateToCompare, previousPositionDate);
        } else return false;
    }

    //thêm ngày mới vào list item để tọa dateheader
    private void generateDateHeaders(List<MessageObj> messages) {
        for (int i = 0; i < messages.size(); i++) {
            MessageObj message = messages.get(i);
            this.items.add(new Wrapper<>(message));
            if (messages.size() > i + 1) {
                MessageObj nextMessage = messages.get(i + 1);
                if (!DateFormatter.isSameDay(message.getCreatedAt(), nextMessage.getCreatedAt())) {
                    this.items.add(new Wrapper<>(message.getCreatedAt()));
                }
            } else {
                this.items.add(new Wrapper<>(message.getCreatedAt()));
            }
        }
    }

    //set layout manager
    void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    //set sự kiện cuộn màn hình
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    //tạo sự kiện khi tin nhắn được click
    private void notifyMessageViewClicked(View view, MessageObj message) {
        if (onMessageViewClickListener != null) {
            onMessageViewClickListener.onMessageViewClick(view, message);
        }
    }

    //hành động thực hiện khi click vào tin nhắn
    private void notifyMessageClicked(MessageObj message) {

        if (onMessageClickListener != null) {
            onMessageClickListener.onMessageClick(message);
        }
        else {
            onMessageClickListener = new OnMessageClickListener<MessageObj>() {
                @Override
                public void onMessageClick(MessageObj message) {
                    if(holderManager.getContentViewType(message) == 2)
                        Log.i("Clicked ", "Text message!");
                    if(holderManager.getContentViewType(message) == 3)
                        Log.i("Clicked ", "Image message!");
                }
            };
        }
    }

    //xử lí sự kiện nhấn vào màn hình nhắn tin
    private View.OnClickListener getMessageClickListener(final Wrapper<MessageObj> wrapper) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyMessageClicked(wrapper.item);
                notifyMessageViewClicked(view, wrapper.item);
            }
        };
    }

    //sự kiện cuộn trên giao diện tin nhắn
    public interface OnLoadMoreListener {

        /**
         * Fires when user scrolled to the end of list.
         *
         * @param page            next page to download.
         * @param totalItemsCount current items count.
         */
        void onLoadMore(int page, int totalItemsCount);
    }

    public interface Formatter<MESSAGE> {

        /**
         * Formats an string representation of the message object.
         *
         * @param message The object that should be formatted.
         * @return Formatted text.
         */
        String format(MESSAGE message);
    }

    //class dùng để tạo danh sách generic
    private class Wrapper<DATA> {
        protected DATA item;
        protected boolean isSelected;

        Wrapper(DATA item) {
            this.item = item;
        }
    }
}
