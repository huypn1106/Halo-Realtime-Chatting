package com.deadk.halo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deadk.halo.R;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ForgetPassword extends AppCompatActivity {


    @BindView(R.id.btn_verify)
    Button btnVerify;
    @BindView(R.id.edittext_emailaddress)
    EditText etEmailAddress;
    @BindView(R.id.textview_notify)
    TextView tvNotify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ButterKnife.bind(this);

        Toolbar appbar =(Toolbar)findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_forgetpass);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @OnClick(R.id.btn_verify)
    void setBtnVerify(){

        final ProgressDialog dialog = ProgressDialog.show(this, "", getResources().getString(R.string.checking), true);
        String email = etEmailAddress.getText().toString().trim();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            tvNotify.setTextColor(getResources().getColor(R.color.success));
                            tvNotify.setText(R.string.received_email);
                            Log.d("deadk", "Email sent.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        tvNotify.setTextColor(getResources().getColor(R.color.error));
                        tvNotify.setText(R.string.email_error);

                        Log.d("deadk", e.getMessage());
                    }
                });

    }

}
