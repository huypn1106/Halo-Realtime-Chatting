package com.deadk.halo.activities.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.activities.base.BaseMessagesActivity;
import com.deadk.halo.activities.dialog.DialogActivity;
import com.deadk.halo.activities.message.attachment.AttachmentActivity;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.MediaPlayer;
import com.deadk.halo.common.listener.AttachmentsListener;
import com.deadk.halo.common.listener.InputListener;
import com.deadk.halo.common.models.Voice;
import com.deadk.halo.dao.firebaseModel.ImageMessage;
import com.deadk.halo.dao.firebaseModel.TextMessage;
import com.deadk.halo.dao.firebaseModel.VoiceMessage;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.views.input.MessageInput;
import com.deadk.halo.views.message.HolderManager;
import com.deadk.halo.views.message.MessageList;
import com.deadk.halo.views.message.MessageListAdapter;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends BaseMessagesActivity
        implements InputListener, AttachmentsListener {

    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    AttachmentActivity attachmentActivity;
    private String uid;
    private String dialogId;
    private ImageLoader imageLoader;
    private MediaPlayer mediaPlayer;
    private MessageListAdapter adapter;
    private MessageList messagesList;
    private List<Message> allMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list_view);

        //lấy các extra
        uid = getIntent().getStringExtra("uid");
        dialogId = getIntent().getStringExtra("dialogId");

        HolderManager holdersConfig = new HolderManager();
        holdersConfig.setContext(MessageActivity.this);
        holdersConfig.setDialogId(dialogId);

        //khởi tạo adapter
        allMessage = new ArrayList<>();
        adapter = new MessageListAdapter<>(uid, holdersConfig,
                imageLoader, mediaPlayer, allMessage);
        adapter.setLoadMoreListener(MessageActivity.this);
        //khởi tạo list item

        messagesList = (MessageList) findViewById(R.id.messagesList);
        messagesList.setAdapter(adapter);


        imageLoader = super.imageLoader;
        mediaPlayer = super.mediaPlayer;

        //sự kiện thêm mới tin nhắn
        final DatabaseReference messageRef = mRootRef.child("dialogs").child(dialogId).child("messages");
        messageRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //cập nhật tin nhắn cuối cùng
                Log.i("message 1111", dataSnapshot.child("messageId").getValue(String.class));

                DatabaseReference lastMessRef = mRootRef.child("dialogs").child(dialogId).child("lastMessage");
                lastMessRef.setValue(dataSnapshot.getKey());


                //kiểm tra tin nhắn của ai
                HashMap<String, Object> messageInfo = (HashMap<String, Object>) dataSnapshot.getValue();
                String senderID = messageInfo.get("senderId").toString();

                //lưu message vào adapter
                Message message = new Message();
                message.setId(messageInfo.get("messageId").toString());
                message.setSenderId(messageInfo.get("senderId").toString());
                int type = Integer.parseInt(messageInfo.get("contentType").toString());
                if (type == 2) {
                    message.setText(messageInfo.get("content").toString());
                } else if (type == 3) {
                    message.setImage(new Message.Image(message.getId()));
                } else if (type == 4) {
                    message.setVoice(new Voice(messageInfo.get("messageId").toString(),
                            Integer.parseInt(messageInfo.get("duration").toString())));
                }
                //tạo ngày từ dạng chuỗi đã lưu
                String dateInString = messageInfo.get("createAt").toString();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
                try {
                    Date parsedDate = formatter.parse(dateInString);
                    message.setCreatedAt(parsedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //allMessage.add(message);
//                Collections.sort(allMessage, new Comparator<Message>() {
//                    @Override
//                    public int compare(Message o1, Message o2) {
//                        return o1.getId().compareTo(o2.getId());
//                    }
//                });
                adapter.addToStart(message, true);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //lấy message input
        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        String dateInString = new SimpleDateFormat("EEEE, dd/MM/yyyy/hh:mm:ss")
                .format(new Date());
        String date = dateInString.trim().split("\\s+")[1];


        DatabaseReference newMess = mRootRef.child("dialogs").child(dialogId).child("messages");

        newMess.push().setValue(new TextMessage("2b", uid, date,
                "2", input.toString()));
        return true;
    }

    @Override
    public void onAddAttachments() {
        attachmentActivity = new AttachmentActivity();
        Intent intent = new Intent(MessageActivity.this, AttachmentActivity.class);
        intent.putExtra("dialogId", dialogId);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }


    //quay lại từ màn hình chọn ảnh
    @Override
    protected void onResume() {
        super.onResume();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final Intent intent = getIntent();
        ArrayList<String> img = intent.getStringArrayListExtra("selectedImg");
        String fileName = intent.getStringExtra("fileName");

        if(img != null){
            for (int i=0; i< img.size(); i++){
                File imgFile = new  File(img.get(i));
                if(imgFile.exists()){
                    //thêm tin nhắn hình ảnh vào database
                    String dateInString = new SimpleDateFormat("EEEE, dd/MM/yyyy/hh:mm:ss")
                            .format(new Date());
                    final String date = dateInString.trim().split("\\s+")[1];

                    final DatabaseReference newMess = mRootRef.child("dialogs").child(dialogId).child("messages");
                    final String id = newMess.push().getKey();
                    //upload ảnh lên
                    StorageReference imageRef = storageRef.child("messageImage/" + id + "/image.jpg");
                    Uri file = Uri.fromFile(new File(img.get(i)));
                    UploadTask uploadTask = imageRef.putFile(file);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            newMess.child(id).setValue(new ImageMessage(id, uid, date, "3"));
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(MessageActivity.this, "Upload failed!", Toast.LENGTH_LONG);
                        }
                    });

                }
            }
        }

        if(fileName != null){
            Log.i("oke ", fileName);
            //thêm tin nhắn âm thanh vào database
            String dateInString = new SimpleDateFormat("EEEE, dd/MM/yyyy/hh:mm:ss")
                    .format(new Date());
            final String date = dateInString.trim().split("\\s+")[1];

            final DatabaseReference newMess = mRootRef.child("dialogs").child(dialogId).child("messages");
            final String id = newMess.push().getKey();
            //upload âm thanh lên
            //tạo thư mục với id
            StorageReference imageRef = storageRef.child("messageVoice/" + id + "/record.3gp");
            Uri file = Uri.fromFile(new File(fileName));
            UploadTask uploadTask = imageRef.putFile(file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("upload ", "oke");
                    int duration = Integer.parseInt(intent.getStringExtra("duration"));
                    newMess.child(id).setValue(
                            new VoiceMessage(id, uid, date,
                                    "4", duration));
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(MessageActivity.this, "Upload failed!", Toast.LENGTH_LONG);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //trở về activity trước
        Intent intent = new Intent(MessageActivity.this, DialogActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }
}
