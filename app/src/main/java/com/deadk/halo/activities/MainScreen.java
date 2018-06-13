package com.deadk.halo.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.fragments.ChatFragment;
import com.deadk.halo.fragments.ContactFragment;
import com.deadk.halo.fragments.ProfileFragment;
import com.deadk.halo.fragments.SettingFragment;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class MainScreen extends AppCompatActivity {

    @BindView(R.id.tabHost)
    public TabHost tabHost;

    Fragment chatFragment;
    Fragment contactFragment;
    Fragment profileFragment;
    Fragment settingFragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        ButterKnife.bind(this);

        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_message);
        setSupportActionBar(appbar);

        addControls();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tabHost.getCurrentTab() == 2){
            getSupportActionBar().hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.search){
            Intent intent = new Intent(MainScreen.this,FindFriendsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addControls() {

        tabHost.setup();

        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        profileFragment = new ProfileFragment();
        settingFragment = new SettingFragment();

//        fragmentManager = getFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//        transaction.replace(R.id.dynamic_content,new ChatFragment() );
//        transaction.commit();

        TabHost.TabSpec tabChat = tabHost.newTabSpec("tabChat");
        tabChat.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_chat));
        tabChat.setContent(R.id.chat_fragment);

        TabHost.TabSpec tabContact = tabHost.newTabSpec("tabContact");
        tabContact.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_contact));
        tabContact.setContent(R.id.contact_fragment);

        TabHost.TabSpec tabProfile = tabHost.newTabSpec("tabProfile");
        tabProfile.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_profile));
        tabProfile.setContent(R.id.profile_fragment);

        TabHost.TabSpec tabSetting = tabHost.newTabSpec("tabSetting");
        tabSetting.setIndicator("", getResources().getDrawable(R.drawable.ic_tab_setting));
        tabSetting.setContent(R.id.setting_fragment);


        tabHost.addTab(tabChat);
        tabHost.addTab(tabContact);
        tabHost.addTab(tabProfile);
        tabHost.addTab(tabSetting);


        tabHost.getTabWidget().setLeftStripDrawable(R.drawable.line_vertical);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("tabProfile"))
                    getSupportActionBar().hide();
                else
                    getSupportActionBar().show();

                if(tabId.equals("tabSetting")) {
                   // FirebaseAuth.getInstance().signOut();
                 //   Intent intent = new Intent(MainScreen.this, MainActivity.class);
//                    MainScreen.this.finish();
                }

                if(tabId.equals("tabChat")) getSupportActionBar().setTitle(R.string.title_message);
                else if (tabId.equals("tabContact")) getSupportActionBar().setTitle(R.string.title_contact);
                else if (tabId.equals("tabSetting")) getSupportActionBar().setTitle(R.string.title_settings);
            }
        });

//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                switch (tabId){
//                    case "tabChat": {
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.dynamic_content, chatFragment);
//                        transaction.commit();
//                    }break;
//                    case "tabContact": {
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.dynamic_content, contactFragment);
//                        transaction.commit();
//                    }break;
//                    case "tabProfile": {
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.dynamic_content, profileFragment);
//                        transaction.commit();
//                    }break;
//                    case "tabSetting": {
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.dynamic_content, settingFragment);
//                        transaction.commit();
//                    }break;
//                }
//            }
//        });

    }

}
