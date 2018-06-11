package com.deadk.halo.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.deadk.halo.R;
import com.deadk.halo.activities.dialog.DialogActivity;
import com.deadk.halo.activities.message.MessageActivity;
import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.listener.OnDialogClickListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatFragment extends Fragment implements OnDialogClickListener<Dialog> {

    @BindView(R.id.dialogsList)
    DialogList dialogsList;
    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(com.deadk.halo.R.layout.fragment_chat, container, false);
        ButterKnife.bind(this,V);
    //    initAdapter(getActivity().getBaseContext());
        return V;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void initAdapter(final Context context) {
        final ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {

            }
        };

        uid = getActivity().getIntent().getStringExtra("uid");
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
                adapter.setContext(context);
                adapter.setUid(uid);
                adapter.setItems(dialogList);
                adapter.setOnDialogClickListener(ChatFragment.this);
                dialogsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = new Intent(getActivity().getBaseContext(), MessageActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("dialogId", dialog.getDialogId());
        startActivity(intent);
    }
}
