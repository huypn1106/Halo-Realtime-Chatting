package com.deadk.halo.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.deadk.halo.R;
import com.deadk.halo.data.DataProvider;
import com.deadk.halo.databinding.ActivityFindFriendsBinding;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.LocaleHelper;
import com.deadk.halo.ultilities.UserAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;



public class FindFriendsActivity extends AppCompatActivity {


    FirebaseDatabase database = DataProvider.getInstance().getDatabase();

    ArrayList<User> listUser;
    UserAdapter userAdapter;
    ActivityFindFriendsBinding activityFindFriendsBinding;
    private FirebaseUser firebaseUser = DataProvider.getInstance().getAuth().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityFindFriendsBinding = DataBindingUtil.setContentView(this, R.layout.activity_find_friends);
        setListView();
        setSearchBox();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    void setSearchBox(){
        activityFindFriendsBinding.searchBox.setActivated(true);
        activityFindFriendsBinding.searchBox.setQueryHint(getString(R.string.hint_search_friend));
        activityFindFriendsBinding.searchBox.onActionViewExpanded();
        activityFindFriendsBinding.searchBox.setIconified(false);
        activityFindFriendsBinding.searchBox.clearFocus();

        activityFindFriendsBinding.searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                userAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    void setListView(){

        listUser = new ArrayList<>();
        listUser.clear();

        final DatabaseReference usersRef = database.getReference("users");

        userAdapter = new UserAdapter(FindFriendsActivity.this, listUser);

        activityFindFriendsBinding.listviewFriends.setAdapter(userAdapter);
        userAdapter.getFilter().filter("");

        activityFindFriendsBinding.listviewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindFriendsActivity.this, UserInfoActivity.class);
                intent.putExtra("pickedUser", (User)userAdapter.getItem(position));
                startActivity(intent);
            }
        });


        usersRef.orderByChild("displayName").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User usr = (User) dataSnapshot.getValue(User.class);

                if(!usr.getUid().equals(firebaseUser.getUid())) {
                    listUser.add(usr);
                    Collections.sort(listUser);
                    userAdapter.notifyDataSetChanged();
                }

                Log.d("hihihi", "day la trong Find friend");

            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                User usr = (User) dataSnapshot.getValue(User.class);
//
//                int i = 0;
//                for(User olduser : listUser){
//                    if(olduser.getUsername().equals(usr.getUsername())){
//                        listUser.remove(olduser);
//                        listUser.add(i,usr);
//                        break;
//                    }
//                    i++;
//                }
//
//                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//                User usr = (User) dataSnapshot.getValue(User.class);
//                listUser.remove(usr);
//                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                Collections.sort(listUser);
//                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
