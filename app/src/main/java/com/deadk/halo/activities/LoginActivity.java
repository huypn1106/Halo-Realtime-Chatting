package com.deadk.halo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.data.UserData;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.textview_forgetpass)
    TextView tvForgetPass;
    @BindView(R.id.edittext_username)
    EditText etUsername;
    @BindView(R.id.edittext_password)
    EditText etPassword;


    ProgressDialog dialog;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    String phoneNumber = "+841639657978";
    String smsCode = "123321";

    final FirebaseUser user = mAuth.getCurrentUser();

    String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private UserData userData = UserData.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_login);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @OnClick(R.id.textview_forgetpass)
    void setTvForgetPass() {
        Intent forgetPassIntent = new Intent(this, ForgetPassword.class);
        startActivity(forgetPassIntent);
    }


    void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("deadk", "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference usersRef = database.getReference("users");

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("deadk", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        //    updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("deadk", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("deadk", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



   void phoneVerify() {
// The test phone number and code should be whitelisted in the console.


        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("deadk", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("deadk", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("deadk", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);


    }


    void login(String username, final String password){

        dialog.show();

        if(etUsername.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {

            Toast.makeText(LoginActivity.this, getString(R.string.register_blank_error), Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }
        else {
            if (!username.contains("@")) {
                DatabaseReference usernamesref = database.getReference("usernames");

                Query phoneQuery = usernamesref.child(username).child("email");
                phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String email = dataSnapshot.getValue(String.class);

                        if (email != null)
                            signIn(email, password);
                        else {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(MainActivity.TAG, "onCancelled", databaseError.toException());
                        dialog.dismiss();
                    }
                });
            } else
                signIn(username, password);
        }

    }


    void updateUI(FirebaseUser user)
    {
        Intent intent;
        if(user.isEmailVerified()) {
            intent = new Intent(LoginActivity.this, MainScreen.class);
            intent.putExtra("uid", user.getUid());
        }
        else
            intent = new Intent(LoginActivity.this, VerifyEmail.class);

        dialog.dismiss();
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_login)
    void setForBtnLogin(){
        dialog = ProgressDialog.show(LoginActivity.this, "",
                getResources().getString(R.string.signing_in), true);
        login(etUsername.getText().toString().trim(), etPassword.getText().toString().trim());

    }


}
