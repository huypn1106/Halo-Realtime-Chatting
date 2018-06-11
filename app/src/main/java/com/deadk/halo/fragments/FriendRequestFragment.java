package com.deadk.halo.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.deadk.halo.R;
import com.deadk.halo.activities.FindFriendsActivity;
import com.deadk.halo.activities.UserInfoActivity;
import com.deadk.halo.data.DataProvider;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.FriendAdapter;
import com.deadk.halo.ultilities.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {


    @BindView(R.id.listview_friend_request)
    ListView listViewFriend;
    ArrayList<User> listUser;

    UserAdapter friendAdapter;

    FirebaseDatabase database = DataProvider.getInstance().getDatabase();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View V = inflater.inflate(R.layout.fragment_friend_request, container, false);

        ButterKnife.bind(this,V);
        setListView();
        return V;
    }


    void setListView(){

        listUser = new ArrayList<>();
        listUser.clear();


        final DatabaseReference usersRef = database.getReference("users");
        final DatabaseReference friendReqRef = database.getReference("friendrequests");

        friendAdapter = new UserAdapter(getActivity(), listUser);
        //    listViewFriends.setAdapter(userAdapter);

        listViewFriend.setAdapter(friendAdapter);

        listViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getBaseContext(), UserInfoActivity.class);
                intent.putExtra("pickedUser", (User)friendAdapter.getItem(position));
                startActivity(intent);
            }
        });



        friendReqRef.child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String uid = dataSnapshot.child("uid").getValue(String.class);

                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User usr = (User) dataSnapshot.getValue(User.class);
                        listUser.add(usr);
                        Collections.sort(listUser);
                        friendAdapter.notifyDataSetChanged();

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                User usr = (User) dataSnapshot.getValue(User.class);
//
//                if(!usr.getUid().equals(firebaseUser.getUid())) {
//                    listUser.add(usr);
//                    Collections.sort(listUser);
//                    userAdapter.notifyDataSetChanged();
//                }
            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User usr = (User) dataSnapshot.getValue(User.class);

                int i = 0;
                for(User olduser : listUser){
                    if(olduser.getUsername().equals(usr.getUsername())){
                        listUser.remove(olduser);
                        listUser.add(i,usr);
                        break;
                    }
                    i++;
                }

                friendAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                User usr = (User) dataSnapshot.getValue(User.class);
                listUser.remove(usr);
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Collections.sort(listUser);
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
