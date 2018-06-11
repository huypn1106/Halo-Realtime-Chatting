package com.deadk.halo.data;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.deadk.halo.activities.MainActivity;
import com.deadk.halo.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserData {

    private static final UserData ourInstance = new UserData();

    private FirebaseDatabase database = DataProvider.getInstance().getDatabase();

    public static UserData getInstance() {
        return ourInstance;
    }

    private final User userFromUid[] = new User[1];
    private String emailByUsername[] = new String[1];

    private UserData() {
    }


    public String getEmailByUsername(String username){

        emailByUsername[0] = "";

        DatabaseReference usernamesref = database.getReference("usernames");

        Query phoneQuery = usernamesref.child(username).child("email");
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                emailByUsername[0] = dataSnapshot.getValue(String.class);

                Log.d(MainActivity.TAG, "chan doi " + emailByUsername);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(MainActivity.TAG, "onCancelled", databaseError.toException());
            }
        });

        SystemClock.sleep(300);

        return emailByUsername[0];
    }

    public User getUserFromUID(String uid){

    //    userFromUid = null;

        DatabaseReference usersRef = database.getReference("users");

        Query query = usersRef.child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       //         userFromUid = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

     //   return userFromUid;
        return  new User();
    }

}
