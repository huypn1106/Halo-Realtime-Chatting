package com.deadk.halo.views.message.holder;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.DurationFormatter;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OutcomingVoiceMessageViewHolder<MessageObj extends Message>
        extends BaseMessageViewHolder<MessageObj> {

    protected TextView duration;
    protected ImageButton iconPlay;
    boolean isPlayed;
    private MediaPlayer audioPlayer;
    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    public int countdown;


    public OutcomingVoiceMessageViewHolder(View itemView) {
        super(itemView);
        duration = itemView.findViewById(R.id.duration);
        iconPlay = itemView.findViewById(R.id.ic_play);
        isPlayed = false;
    }

    @Override
    public void onBind(final MessageObj messageObj) {
        super.onBind(messageObj);
        //hiển thị độ dài của tin nhắn
        duration.setText(DurationFormatter.getDurationString(
                messageObj.getVoice().getDuration()));

        //sự kiện nhấn vào icon

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference recordRef = storageRef.child("messageVoice").child(messageObj.getId() + "/record.3gp");
        recordRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                iconPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //đổi icon
                        audioPlayer = mediaPlayer.playAudioUrl(uri.toString());
                        countdown = messageObj.getVoice().getDuration();
                        final Handler handler = new Handler();
                        isPlayed = isPlayed == false ? true:false;
                        if(isPlayed){
                            iconPlay.setImageResource(R.drawable.ic_pause_white);
                            audioPlayer.start();
                            Runnable handlerRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    if(countdown >= 0) {
                                        handler.postDelayed(this, 1000);
                                        setText(duration);
                                    } else {
                                        handler.removeCallbacks(this);
                                        duration.setText(DurationFormatter.getDurationString(
                                                messageObj.getVoice().getDuration()));
                                        iconPlay.setImageResource(R.drawable.ic_play_white);
                                    }
                                }
                            };
                            handler.postDelayed(handlerRunnable, 1000);
                        }
                        else {
                            iconPlay.setImageResource(R.drawable.ic_play_white);
                            audioPlayer.pause();
                            countdown = -1;
                        }
                    }
                });
            }
        });
    }

    public void setText(TextView textView){
        textView.setText(DurationFormatter.getDurationString(countdown));
        countdown--;
    }
}
