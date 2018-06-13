package com.deadk.halo.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingMethod;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {


    @BindView(R.id.edittext_new_password)
    EditText etNewPass;
    @BindView(R.id.edittext_confirmpassword)
    EditText etConfirmPass;
    @BindView(R.id.edittext_current_password)
    EditText etCurrentPass;
    @BindView(R.id.textview_errormessage)
    TextView tvErrorMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        appbar.setTitle(R.string.change_pass);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @OnClick(R.id.btn_change_pass)
    void changePass(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(!etConfirmPass.getText().toString().trim().equals(etNewPass.getText().toString().trim())){
            tvErrorMessage.setText(getString(R.string.register_password_not_match));
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
        else if(etNewPass.length() < 6){
            tvErrorMessage.setText(getString(R.string.register_password_week));
        }
        else {

            progressBar.setVisibility(View.VISIBLE);

            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), etCurrentPass.getText().toString().trim());


            // Prompt the user to re-provide their sign-in credentials
            if (credential != null) {
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    user.updatePassword(etNewPass.getText().toString().trim())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ChangePasswordActivity.this,
                                                            getString(R.string.change_pass_success),Toast.LENGTH_LONG ).show();

//                                                    final Handler handler = new Handler();
//                                                    handler.postDelayed(new Runnable() {
//                                                        @Override
//                                                        public void run() {
                                                            ChangePasswordActivity.this.finish();
                                                     //   }
                                                  //  }, 1000);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    tvErrorMessage.setText(getString(R.string.register_unhandled));
                                                    tvErrorMessage.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                } else {
                                    tvErrorMessage.setText(getString(R.string.register_unhandled));
                                    tvErrorMessage.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tvErrorMessage.setText(getString(R.string.current_pass_incorrect));
                                tvErrorMessage.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }

    }


}
