package com.deadk.halo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deadk.halo.R;
import com.deadk.halo.activities.ChangePasswordActivity;
import com.deadk.halo.activities.MainActivity;
import com.deadk.halo.activities.MainScreen;
import com.deadk.halo.data.DataProvider;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.GeneralUltilities;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import static com.deadk.halo.R.layout.spinner_item;


public class SettingFragment extends Fragment {

    @BindView(R.id.layout_profile)
    ConstraintLayout layoutProfile;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.textview_title)
    TextView tvDisplayname;
    @BindView(R.id.layout_language)
    LinearLayout layoutLanguage;
    @BindView(R.id.layout_logout)
    LinearLayout layoutLogout;
    @BindView(R.id.spinner_language)
    Spinner spinnerLanguage;
    @BindView(R.id.sw_notification)
    Switch swNotification;
    @BindView(R.id.sw_receive_request)
    Switch swReceiveRequest;

    @BindView(R.id.tv_changepass)
    TextView tvChangePass;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_logout)
    TextView tvLogout;


    FirebaseDatabase database = DataProvider.getInstance().getDatabase();
    FirebaseUser user = DataProvider.getInstance().getAuth().getCurrentUser();

    boolean firstReach = false;

    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(com.deadk.halo.R.layout.fragment_setting, container, false);

        ButterKnife.bind(this,V);

        setControls();

        return V;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @OnClick(R.id.layout_logout)
    void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.layout_changepass)
    void openChangepass(){
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_profile)
    void clickProfile(){
        ((MainScreen)getActivity()).tabHost.setCurrentTab(2);
    }

    private void setControls() {

        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);

                tvDisplayname.setText(currentUser.getDisplayName().toString());


                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("images/" + user.getUid() + "/avatar");

                Glide.with(SettingFragment.this)
                        .load(imageRef)
                        .apply(GeneralUltilities.requestOptionsAvt)
                        .into(imgAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String array_spinner[];
        array_spinner = new String[2];
        array_spinner[0] = "English";
        array_spinner[1] = "Tiếng Việt";
        ArrayAdapter adapter = new ArrayAdapter(getActivity().getBaseContext(), R.layout.spinner_item_language, array_spinner);

        spinnerLanguage.setAdapter(adapter);

        String locale = LocaleHelper.getLanguage(getActivity().getBaseContext());
        if(locale.equals("English")) spinnerLanguage.setSelection(0);
        else    spinnerLanguage.setSelection(1);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spinnerLanguage.getSelectedItem().toString().equals("English") &&
                        LocaleHelper.getLanguage(getActivity().getBaseContext()).equals("vi")){
                    updateViews("en");
                }
                else if(spinnerLanguage.getSelectedItem().toString().equals("Tiếng Việt") &&
                        LocaleHelper.getLanguage(getActivity().getBaseContext()).equals("en"))
                    updateViews("vi");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(getActivity(), languageCode);
        getActivity().recreate();
    }


}
