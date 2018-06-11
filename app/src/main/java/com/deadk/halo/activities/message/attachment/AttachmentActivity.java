package com.deadk.halo.activities.message.attachment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.activities.message.MessageActivity;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.ultilities.DurationFormatter;
import com.deadk.halo.ultilities.ImageBrowser;
import com.deadk.halo.views.attachment.ImageGalleryAdapter;

import java.io.IOException;

public class AttachmentActivity extends AppCompatActivity {
    private TabHost tabHost;
    private GridView gridView;
    private Button btn;
    private ImageButton record_btn;
    private TextView tvDuration;

    private Handler handler = new Handler();
    private MediaRecorder mRecorder;
    String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";
    protected MediaPlayer mediaPlayer;
    private android.media.MediaPlayer player;
    private AudioManager mAudioManager;
    public static int duration = 0;
    public static int countdown;
    private ImageBrowser imageBrowser;
    private String dialogId;
    private String senderId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_view);
        dialogId = getIntent().getStringExtra("dialogId");
        senderId = getIntent().getStringExtra("uid");
        tabHost = findViewById(R.id.tab_host);
        tabHost.setup();
        duration = 0;

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab One");
        spec.setContent(R.id.image_tab);
        spec.setIndicator("Image");
        tabHost.addTab(spec);

        //set dữ liệu gridview
        requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                11111111);

        gridView = findViewById(R.id.image_tab);
        imageBrowser = new ImageBrowser(AttachmentActivity.this);
        final ImageGalleryAdapter adapter = new ImageGalleryAdapter(this, imageBrowser.browse());
        gridView.setAdapter(adapter);

        //Tab 2
        spec = tabHost.newTabSpec("Tab Two");
        spec.setContent(R.id.voice_tab);
        spec.setIndicator("Voice");
        tabHost.addTab(spec);

        //nút record
        record_btn = findViewById(R.id.record_button);
        tvDuration = findViewById(R.id.duration);

        // Get AudioManager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus
        mAudioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        record_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //hiển thị độ dài trong quá trình thu âm
                Runnable handlerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(record_btn.isPressed()) {
                            handler.postDelayed(this, 1000);
                            setText(tvDuration);
                        } else {
                            handler.removeCallbacks(this);
                            record_btn.setPressed(false);
                        }
                    }
                };

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeCallbacks(handlerRunnable);
                        record_btn.setPressed(true);
                        //bắt đầu thu âm
                        duration = 0;
                        onRecord(true);
                        //gọi hàm cập nhật độ dài
                        handler.postDelayed(handlerRunnable, 1000);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //kết thúc thu âm
                        record_btn.setPressed(false);
                        handler.postDelayed(handlerRunnable, 1000);
                        handler.removeCallbacks(handlerRunnable);
                        if(duration > 1){
                            onRecord(false);
                            //chạy thử âm thanh vừa thu
                            playAudio(mFileName);
                        }
                        if(mRecorder != null)
                            mRecorder.release();
                        return true;
                        default:
                            return false;
                }
            }
        });

        //nút oke
        btn = findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabHost.getCurrentTab() == 0) {
                    Intent intent = new Intent(AttachmentActivity.this, MessageActivity.class);
                    intent.putStringArrayListExtra("selectedImg", adapter.getSelectedImg());
                    intent.putExtra("dialogId", dialogId);
                    intent.putExtra("uid", senderId);
                    startActivity(intent);
                }
                else {
                    //dừng player nếu muốn gửi record đi
                    if(player.isPlaying()) {
                        player.stop();
                        player.release();
                    }
                    Intent intent = new Intent(AttachmentActivity.this, MessageActivity.class);
                    intent.putExtra("fileName", mFileName);
                    Log.i("value require", String.valueOf(duration));
                    intent.putExtra("duration", String.valueOf(duration-1));
                    intent.putExtra("dialogId", dialogId);
                    intent.putExtra("uid", senderId);
                    startActivity(intent);
                }

            }
        });
    }

    public void setText(TextView tv){
        tv.setText(DurationFormatter.getDurationString(duration));
        duration = duration + 1;
    }

    public void playAudio(String fileName){
        mediaPlayer = new MediaPlayer() {
            @Override
            public android.media.MediaPlayer playAudioRaw(int rawID) {
                return android.media.MediaPlayer.create(AttachmentActivity.this, rawID);
            }

            @Override
            public android.media.MediaPlayer playAudioUrl(String url) {
                android.media.MediaPlayer mp = new android.media.MediaPlayer();
                try {
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    Log.i("Play ", "prepare() failed");
                }
                return mp;
            }
        };
        player = mediaPlayer.playAudioUrl(fileName);
        player.start();

        //đếm ngược thời gian
        countdown = duration - 1;
        Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if(countdown >= 0) {
                    handler.postDelayed(this, 1000);
                    tvDuration.setText(DurationFormatter.getDurationString(countdown--));
                } else {
                    handler.removeCallbacks(this);
                    tvDuration.setText(DurationFormatter.getDurationString(duration - 1));
                }
            }
        };
        handler.postDelayed(handlerRunnable, 1000);
        Log.i("playing", "now");
    }





    // Listen for Audio Focus changes
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mAudioManager.abandonAudioFocus(afChangeListener);

                // Stop playback, if necessary
                if (null != player && player.isPlaying())
                    player.pause();
            }

        }

    };

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Record ", "prepare() failed");
        }
        mRecorder.start();
        Log.i("Record ", "started");
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
