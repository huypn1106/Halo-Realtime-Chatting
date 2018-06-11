package com.deadk.halo.activities.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.views.message.MessageListAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public abstract class BaseMessagesActivity extends AppCompatActivity
        implements MessageListAdapter.OnLoadMoreListener {

    private static final int TOTAL_MESSAGES_COUNT = 100;


    protected ImageLoader imageLoader;
    protected MediaPlayer mediaPlayer;
    protected MessageListAdapter<Message> messagesAdapter;

    private Menu menu;
    private Date lastLoadedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = new MediaPlayer() {
            @Override
            public android.media.MediaPlayer playAudioRaw(int rawID) {
                return android.media.MediaPlayer.create(BaseMessagesActivity.this, rawID);
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
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                File imgFile = new  File(url);

                if(imgFile.exists()){
                    //lấy ảnh từ đường dẫn
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //set image cho imageview
                    imageView.setImageBitmap(myBitmap);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    //hiển thị thêm message khi cuộn màn hình
    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //đợi quá trình load database
            @Override
            public void run() {
                //messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }

    //đặt format cho dữ liệu ngày tháng trên view
    private MessageListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessageListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
}
