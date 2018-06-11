package com.deadk.halo.views.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.ViewHolder;
import com.deadk.halo.dao.model.Dialog;
import com.deadk.halo.ultilities.DateFormatter;
import com.deadk.halo.common.listener.OnDialogClickListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class DialogViewHolder<DialogObj extends Dialog> extends ViewHolder<DialogObj> {
    protected ImageLoader imageLoader;
    protected DateFormatter.Formatter datesFormatter;
    private OnDialogClickListener<DialogObj> onDialogClickListener;

    protected ViewGroup container;
    protected ViewGroup root;
    protected TextView tvName;
    protected TextView tvDate;
    protected ImageView ivAvatar;
    protected ImageView ivLastMessageUser;
    protected TextView tvLastMessage;
    protected TextView tvBubble;
    private View onlineIndicator;
    protected ViewGroup dividerContainer;
    protected View divider;
    public Context context;
    protected String uid;
    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    public static StorageReference storageRef = FirebaseStorage.getInstance().getReference();




    public DialogViewHolder(View itemView) {
        super(itemView);

        root = itemView.findViewById(R.id.dialogRootLayout);
        container = itemView.findViewById(R.id.dialogContainer);
        tvName = itemView.findViewById(R.id.dialogName);
        tvDate = itemView.findViewById(R.id.dialogDate);
        tvLastMessage = itemView.findViewById(R.id.dialogLastMessage);
        tvBubble = itemView.findViewById(R.id.dialogUnreadBubble);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        ivLastMessageUser = itemView.findViewById(R.id.dialogLastMessageUserAvatar);
        ivAvatar = itemView.findViewById(R.id.dialogAvatar);
        dividerContainer = itemView.findViewById(R.id.dialogDividerContainer);
        divider = itemView.findViewById(R.id.dialogDivider);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBind(final DialogObj dialog) {

        //Set avatar của dialog
        // Create a storage reference from our app
        String url = "";
        if(dialog.getIsGroup()==1){//kiểm tra là tin nhắn group hay không
            if(uid.contentEquals(dialog.getUidHost())
                    || uid.contentEquals(dialog.getIdClient().get("1").toString())) {//kiểm tra id để lấy avatar
                url = "userAvatar/" + dialog.getIdClient().get("1").toString();
                //set tên cho dialog
                DatabaseReference name = mRootRef.child("users")
                        .child(String.valueOf(dialog.getClientAvatar()))
                        .child("username");

                name.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                url = "userAvatar/" + dialog.getUidHost();

                //set tên cho dialog
                DatabaseReference name = mRootRef.child("users").child(uid).child("username");
                name.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tvName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        else {
            url = "groupAvatar/" + dialog.getClientAvatar();
            //set tên cho dialog
            DatabaseReference name = mRootRef.child("dialogs")
                    .child(dialog.getDialogId())
                    .child("idClient")
                    .child("name");
            name.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tvName.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //set avatar cho dialog
        loadImage(ivAvatar, url);


        ivLastMessageUser.setVisibility(dialog.getIsGroup() > 1 ? VISIBLE : GONE);

        //Set nội dung tin nhắn cuối cùng
        final DatabaseReference lastMessRef = mRootRef.child("dialogs")
                .child(dialog.getDialogId())
                .child("messages")
                .child(dialog.getLastMessage());

        lastMessRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> lastMessage = (HashMap<String, Object>) dataSnapshot.getValue();
                int type = Integer.parseInt(lastMessage.get("contentType").toString());
                if(type == 2){
                    tvLastMessage.setText(lastMessage.get("content").toString());
                }
                else if(type == 3){
                    tvLastMessage.setText("Image message!");
                }
                else
                    tvLastMessage.setText("Voice message!");

                //ngày giờ của hoạt động cuối cùng
                String dateInString = lastMessage.get("createAt").toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
                try {
                    Date lastMessageDate = formatter.parse(dateInString);
                    //Set nhãn thời gian
                    String formattedDate = null;
                    if (datesFormatter != null) formattedDate = datesFormatter.format(lastMessageDate);
                    tvDate.setText(formattedDate == null
                            ? getDateString(lastMessageDate)
                            : formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //lấy avatar của người tin nhắn cuối cùng
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                String url = "userAvatar/" + lastMessage.get("senderId");
                loadImage(ivLastMessageUser, url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //set số tin nhắn chưa đọc
        tvBubble.setText(String.valueOf(dialog.getUnreadCount()));
        tvBubble.setVisibility(dialog.getUnreadCount() > 0 ? VISIBLE : GONE);


        //set image chỉ định online
        if (dialog.getIsGroup() > 1) {
            onlineIndicator.setVisibility(View.GONE);
        } else {
            //set image chỉ định online
            DatabaseReference isOnlineRef = mRootRef.child("users").child(dialog.getIdClient().get("1").toString()).child("isOnline");
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
        }

        //set sự kiện click listener
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDialogClickListener != null) {
                    onDialogClickListener.onDialogClick(dialog);
                }
            }
        });
    }

    void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    protected void setOnDialogClickListener(OnDialogClickListener<DialogObj> onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    public void setDatesFormatter(DateFormatter.Formatter dateHeadersFormatter) {
        this.datesFormatter = dateHeadersFormatter;
    }


    protected String getDateString(Date date) {
        return DateFormatter.format(date, DateFormatter.Template.TIME);
    }

    public void setContext(Context context){
        this.context = context;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public void loadImage(ImageView imageView, String url){

        final RequestOptions requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.avatar_loading); //truong hop load k co anh
        StorageReference avartarRef = storageRef.child(url + "/avatar.jpg");
        File imgFolder = new File(Environment.getExternalStorageDirectory(), "Halo/" + url);
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        final File imgFile = new File(imgFolder, "avatar.jpg");
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        else {
            Glide.with(context).applyDefaultRequestOptions(requestOptionsAvt).load(avartarRef).into(imageView);
            avartarRef.getFile(imgFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
    }
}
