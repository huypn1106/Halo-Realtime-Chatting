package com.deadk.halo.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyEmail extends AppCompatActivity {

    @BindView(R.id.btn_send_verify)
    Button btnSendVerify;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.textview_status)
    TextView tvStatus;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        ButterKnife.bind(this);

        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_email_verify);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    void autoRefreshStatus(){

        final Timer repeatTask = new Timer();
        repeatTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAuth.getCurrentUser().reload();
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            imgStatus.setImageResource(R.drawable.ic_done_blue_120dp);
                            tvStatus.setText(getResources().getString(R.string.email_verified));
                            tvStatus.setTextColor(getResources().getColor(R.color.success));
                            progressBar.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 300ms
                                    Intent intent = new Intent(VerifyEmail.this,MainScreen.class);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 3000);

                            repeatTask.cancel();

                        }
                    }
                });
            }
        }, 0, 3000);



    }

    @OnClick(R.id.btn_send_verify)
    void setBtnSendVerify(){

        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button

                        if (task.isSuccessful()) {
                            Toast.makeText(VerifyEmail.this,
                                    "Verification email sent to " + mAuth.getCurrentUser().getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("deadk", "sendEmailVerification", task.getException());
                            Toast.makeText(VerifyEmail.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        autoRefreshStatus();
    }

}
