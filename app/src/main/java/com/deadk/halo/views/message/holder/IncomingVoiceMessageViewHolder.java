package com.deadk.halo.views.message.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.DurationFormatter;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class IncomingVoiceMessageViewHolder<MessageObj extends Message>
        extends BaseMessageViewHolder<MessageObj> {
    private ImageView ivAvatar;
    private View onlineIndicator;
    protected TextView duration;
    private ImageButton iconPlay;
    private boolean isPlayed;
    private MediaPlayer audioPlayer;
    private String dialogId;
    private Context context;
    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private int countdown;

    public IncomingVoiceMessageViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ivAvatar = itemView.findViewById(R.id.messageUserAvatar);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        duration = itemView.findViewById(R.id.duration);
        iconPlay = itemView.findViewById(R.id.ic_play);
        isPlayed = false;
    }

    @Override
    public void onBind(final MessageObj messageObj) {
        super.onBind(messageObj);

        DatabaseReference messageRef = mRootRef.child("dialog").child(dialogId).child("messages");
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Set avatar của tin nhắn
                String urlAvar = "userAvatar/" + messageObj.getSenderId();
                //gọi hàm load ảnh
                loadImage(ivAvatar, urlAvar, "avatar.jpg");

                //set image chỉ định online
                DatabaseReference isOnlineRef = mRootRef.child("users").child(messageObj.getSenderId()).child("isOnline");
                isOnlineRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        onlineIndicator.setVisibility(View.VISIBLE);
                        if (Integer.parseInt(dataSnapshot.getValue().toString()) == 1) {
                            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
                        } else {
                            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                //hiển thị độ dài của tin nhắn
                duration.setText(DurationFormatter.getDurationString(
                        messageObj.getVoice().getDuration()));

                //sự kiện nhấn vào icon
                StorageReference recordRef = storageRef.child("messageVoice").child(messageObj.getId() + "/record.3gp");

                recordRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        audioPlayer = mediaPlayer.playAudioUrl(uri.toString());
                        audioPlayer.pause();
                        iconPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //đổi icon
                                countdown = messageObj.getVoice().getDuration();
                                isPlayed = isPlayed == false ? true:false;
                                final Handler handler = new Handler();
                                if(isPlayed){
                                    iconPlay.setImageResource(R.drawable.ic_pause_black);
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
                                                iconPlay.setImageResource(R.drawable.ic_play_black);
                                            }
                                        }
                                    };
                                    handler.postDelayed(handlerRunnable, 1000);
                                }
                                else {
                                    iconPlay.setImageResource(R.drawable.ic_play_black);
                                    audioPlayer.pause();
                                    countdown = -1;
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setText(TextView textView){
        textView.setText(DurationFormatter.getDurationString(countdown));
        countdown--;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public void loadImage(ImageView imageView, String url, String fileName){

        final RequestOptions requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.avatar_loading); //truong hop load k co anh

        StorageReference avartarRef = storageRef.child(url + "/" + fileName);
        File imgFolder = new File(Environment.getExternalStorageDirectory(), "Halo/" + url);
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        //tạo file trong đường dẫn nếu có
        final File imgFile = new File(imgFolder, fileName);
        if(imgFile.exists()){
            //load ảnh từ filepath vào imageview
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            Log.i("load image", url);
        }
        else {
            //nếu chưa có ảnh thì tải từ FirebseStorage
            Glide.with(context).applyDefaultRequestOptions(requestOptionsAvt).load(avartarRef).into(imageView);
            avartarRef.getFile(imgFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.i("save image", "success");
                }
            });
        }
    }
}
