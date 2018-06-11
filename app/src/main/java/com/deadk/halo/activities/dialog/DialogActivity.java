package com.deadk.halo.activities.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.deadk.halo.R;
import com.deadk.halo.activities.base.BaseDialogActivity;
import com.deadk.halo.activities.message.MessageActivity;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.dao.model.Dialog;
import com.deadk.halo.views.dialog.DialogList;
import com.deadk.halo.views.dialog.DialogListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogActivity extends BaseDialogActivity {

    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private String uid;
    private DialogList dialogsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list_view);

        dialogsList = findViewById(R.id.dialogsList);
        initAdapter();
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = new Intent(DialogActivity.this, MessageActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("dialogId", dialog.getDialogId());
        startActivity(intent);
    }

    private void initAdapter() {
        final ImageLoader imageLoader = super.imageLoader;

        uid = getIntent().getStringExtra("uid");
        //lấy data từ firebase
        DatabaseReference dialogRef = mRootRef.child("dialogs");
        dialogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Dialog> dialogList = new ArrayList<>();
                //lấy các hội thoại hiện có
                ArrayList<Object> key = (ArrayList<Object>) dataSnapshot.getValue();
                ArrayList<Integer> a = new ArrayList<>();
                //lấy thông tin của hội thoại
                for(int i=1; i < key.size(); i++){
                    HashMap<String, Object> value = (HashMap<String, Object>) key.get(i);

                    //lấy danh sách client
                    HashMap<String, Object> idClient = (HashMap<String, Object>) value.get("idClient");
                    boolean isInGroup = false;
                    int isGroup = Integer.parseInt(value.get("isGroup").toString());
                    //kiểm tra trong danh sách client
                    for(int j=0; j< isGroup; j++){
                        String index = String.valueOf(j+1);
                        String id = idClient.get(index).toString();
                        if(uid.equals(id)){
                            isInGroup = true;
                        }
                    }
                    //kiểm tra user là host hoặc thành viên trong group
                    if (uid.equals(value.get("uidHost").toString())
                            || isInGroup) {
                        final Dialog dialog = new Dialog();
                        dialog.setDialogId(value.get("dialogId").toString());
                        dialog.setIdClient(idClient);
                        dialog.setClientAvatar(idClient.get("clientAvatar").toString());
                        dialog.setIsGroup(isGroup);
                        dialog.setUidHost(value.get("uidHost").toString());
                        dialog.setUnreadCount(Integer.parseInt(value.get("unReadCount").toString()));
                        dialog.setLastMessage(value.get("lastMessage").toString());
                        Log.i("last", dialog.getLastMessage());
                        dialogList.add(dialog);
                    }
                }

                //set adapter cho dialog view
                DialogListAdapter adapter = new DialogListAdapter<>(
                        R.layout.fragment_chat, imageLoader);
                adapter.setContext(DialogActivity.this);
                adapter.setUid(uid);
                adapter.setItems(dialogList);
                adapter.setOnDialogClickListener(DialogActivity.this);
                dialogsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
