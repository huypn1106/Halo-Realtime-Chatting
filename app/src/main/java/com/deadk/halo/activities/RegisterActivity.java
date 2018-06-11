package com.deadk.halo.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deadk.halo.R;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.spinner_gender)
    Spinner spinner_gender;
    @BindView(R.id.btn_datepicker)
    ImageButton btn_datepicker;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.textview_dob)
    TextView textViewDateofBirth;
    @BindView(R.id.edittext_username)
    EditText etUsername;
    @BindView(R.id.edittext_displayname)
    EditText etDisplayname;
    @BindView(R.id.edittext_emailaddress)
    EditText etEmailAddress;
    @BindView(R.id.edittext_phone_number)
    EditText etPhoneNo;
    @BindView(R.id.edittext_password)
    EditText etPassword;
    @BindView(R.id.edittext_confirmpassword)
    EditText etConfirmPassword;
    @BindView(R.id.textview_errormessage)
    TextView tvErrorMessage;


    ProgressDialog dialog;

    //Firebase Authentication
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Firebase Realtime Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        preSet();

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    void preSet(){

        //Set up toolbar
        Toolbar appbar =(Toolbar)findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_register);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set up for Combobox gender
        String array_spinner[];
        array_spinner=new String[2];
        array_spinner[0]= getResources().getString(R.string.male);
        array_spinner[1]=getResources().getString(R.string.female);
        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.spinner_item, array_spinner);
        spinner_gender.setAdapter(adapter);




    }


    private void createAccount(final String username, final String displayname, final String emailAddress, final String phoneNo,
                               final String password, final String dateOfBirth, final String gender){

        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            Log.d("deadk", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            User newUser = new User(user.getUid(),username,displayname,emailAddress,phoneNo,dateOfBirth,gender);

                            DatabaseReference usersRef = database.getReference("users");
                            usersRef.child(user.getUid().toString()).setValue(newUser);

                            DatabaseReference usernamesRef = database.getReference("usernames/"+newUser.getUsername());
                            usernamesRef.child("uid").setValue(user.getUid().toString());
                            usernamesRef.child("email").setValue(user.getEmail().toString());

                            updateUI(user,newUser);
                        }else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                            //If email already registered.
                            tvErrorMessage.setText(getResources().getString(R.string.register_email_exist));
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            dialog.dismiss();

                        }
                        else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            //if password not 'stronger'
                            tvErrorMessage.setText(getResources().getString(R.string.register_password_week));
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //If email are in incorret  format
                            tvErrorMessage.setText(getResources().getString(R.string.register_email_incorrect));
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            dialog.dismiss();

                        }else
                        {
                            //OTHER THING
                            tvErrorMessage.setText(getResources().getString(R.string.register_unhandled));
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }

                    }
                });

    }


    void updateUI(FirebaseUser user,User newUser)
    {
        Intent intent = new Intent(RegisterActivity.this, VerifyEmail.class);
      //  intent.putExtra("user",newUser );
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_register)
    void setForRegister(){


        dialog = ProgressDialog.show(RegisterActivity.this, "",
                getResources().getString(R.string.signing_up), true);

        DatabaseReference usernamesRef = database.getReference("usernames");

        usernamesRef.child(etUsername.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){

                    String username = etUsername.getText().toString().trim();
                    String displayName = etDisplayname.getText().toString().trim();
                    String emailAddress = etEmailAddress.getText().toString().trim();
                    String phoneNo = etPhoneNo.getText().toString().trim();
                    String dateOfBirth = textViewDateofBirth.getText().toString().trim();
                    String gender = spinner_gender.getSelectedItem().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if(username.equals("") || displayName.equals("") || emailAddress.equals("") || phoneNo.equals("")
                            || password.equals("") || confirmPassword.equals("")){
                        tvErrorMessage.setText(getResources().getString(R.string.register_blank_error));
                        tvErrorMessage.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                    else{

                        if(gender.equals("Nam") || gender.equals("Male"))
                            gender = "Male";
                        else
                            gender = "Female";

                        if(!password.equals(confirmPassword)){
                            tvErrorMessage.setText(getResources().getString(R.string.register_password_not_match));
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                        else {
                            createAccount(username, displayName, emailAddress,phoneNo,password, dateOfBirth, gender);
                        }
                    }


                }
                else{
                    tvErrorMessage.setText(getResources().getString(R.string.register_username_exist));
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btn_datepicker)
    void setDoBSpinner(){
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                textViewDateofBirth.setText(
                        (dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=textViewDateofBirth.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                RegisterActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle(getResources().getString(R.string.birthdaytitle));
        pic.show();
    }

}
