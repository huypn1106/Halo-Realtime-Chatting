package com.deadk.halo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.deadk.halo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ContactFragment extends Fragment implements View.OnClickListener{



    @BindView(R.id.tab_request)
    TextView tabRequest;
    @BindView(R.id.tab_friend)
    TextView tabFriend;
    @BindView(R.id.tab_group)
    TextView tabGroup;

    FriendFragment friendFragment;
    FriendRequestFragment friendRequestFragment;
    GroupFragment groupFragment;

    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(com.deadk.halo.R.layout.fragment_contact, container, false);

        ButterKnife.bind(this, V);

        addControls();

        return V;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void addControls() {

        friendFragment = new FriendFragment();
        friendRequestFragment = new FriendRequestFragment();

        fm = getFragmentManager();

        clickFriend();
    }


    @OnClick(R.id.tab_friend)
    void clickFriend(){
        ft = fm.beginTransaction();
        tabFriend.setTextColor(getResources().getColor(R.color.general));
        tabGroup.setTextColor(getResources().getColor(R.color.hintEdittext));
        tabRequest.setTextColor(getResources().getColor(R.color.hintEdittext));
        ft.replace(R.id.list_manager,friendFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @OnClick(R.id.tab_request)
    void clickRequest() {
        ft = fm.beginTransaction();
        tabRequest.setTextColor(getResources().getColor(R.color.general));
        tabGroup.setTextColor(getResources().getColor(R.color.hintEdittext));
        tabFriend.setTextColor(getResources().getColor(R.color.hintEdittext));
        ft.replace(R.id.list_manager,friendRequestFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tab_friend:{
                tabFriend.setTextColor(getResources().getColor(R.color.general));
                ft.replace(R.id.list_manager,friendFragment);
                ft.commit();
            }break;

            case R.id.tab_group:{
                tabGroup.setTextColor(getResources().getColor(R.color.general));
                ft.replace(R.id.list_manager,groupFragment);
                ft.commit();
            }break;

            case R.id.tab_request:{
                tabRequest.setTextColor(getResources().getColor(R.color.general));
                ft.replace(R.id.list_manager,friendRequestFragment);
                ft.commit();
            }break;
        }

    }
}