package com.deadk.halo.activities;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.ultilities.GeneralUltilities;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.textview_tiengviet)
    TextView txtTiengViet;
    @BindView(R.id.textview_english)
    TextView txtEnglish;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.prg_loading)
    ProgressBar progressBar;

    private Animation animAlpha;
    int width;
    int height;


    public static final String TAG = "deadk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
 //       getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha_0_to_1);

        if(!GeneralUltilities.init)
             setAnimation();

        preSet();

    }

    @OnClick(R.id.textview_tiengviet)
    public void switchVietnamese(){
        updateViews("vi");
        txtEnglish.setTextColor(getResources().getColor(android.R.color.black));
        txtTiengViet.setTextColor(getResources().getColor(R.color.general));
    }

    @OnClick(R.id.textview_english)
    public void switchEnglish()
    {
        updateViews("en");
        txtTiengViet.setTextColor(getResources().getColor(android.R.color.black));
        txtEnglish.setTextColor(getResources().getColor(R.color.general));
    }


    @OnClick(R.id.btn_login)
    public void doLogin()
    {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    @OnClick(R.id.btn_register)
    public void doRegister(){
        Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();

        btnLogin.setText(resources.getString(R.string.btn_login_text));
        btnRegister.setText(resources.getString(R.string.btn_register_text));
    }

    private void setAnimation(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        imgLogo.setY(imgLogo.getY() + height/10);

        imgLogo.animate()
                .translationY(imgLogo.getY() - height / 10)
                .setDuration(1500)
                .setInterpolator(new LinearOutSlowInInterpolator());

        btnRegister.setAnimation(animAlpha);
        btnLogin.setAnimation(animAlpha);
        txtEnglish.setAnimation(animAlpha);
        txtTiengViet.setAnimation(animAlpha);

        GeneralUltilities.init = true;
    }

    private void preSet(){
        if(Locale.getDefault().getLanguage().equals("en"))
            txtEnglish.setTextColor(getResources().getColor(R.color.general));
        else
            txtTiengViet.setTextColor(getResources().getColor(R.color.general));
    }
}
