package com.deadk.halo.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deadk.halo.R;
import com.deadk.halo.data.DataProvider;
import com.deadk.halo.models.User;
import com.deadk.halo.ultilities.LocaleHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateProfileActivity extends AppCompatActivity {


    @BindView(R.id.spinner_gender)
    Spinner spinnerGender;
    @BindView(R.id.edittext_display_name)
    EditText etDisplayname;
    @BindView(R.id.edittext_phoneno)
    EditText etPhoneNo;
    @BindView(R.id.textview_dob)
    TextView tvDateofBirth;
    @BindView(R.id.btn_datepicker)
    ImageButton btnDatePicker;
    @BindView(R.id.btn_update_profile)
    Button btnUpdateProfile;
    @BindView(R.id.textview_errormessage)
    TextView tvErrorMessage;

    private User currentUser;

    FirebaseAuth mAuth = DataProvider.getInstance().getAuth();
    FirebaseDatabase database = DataProvider.getInstance().getDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        ButterKnife.bind(this);
        preSet();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    void preSet() {

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        //Set up toolbar
        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        appbar.setTitle(R.string.title_update_profile);
        setSupportActionBar(appbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set up for Combobox gender
        String array_spinner[];
        array_spinner = new String[2];
        array_spinner[0] = getResources().getString(R.string.male);
        array_spinner[1] = getResources().getString(R.string.female);
        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.spinner_item, array_spinner);
        spinnerGender.setAdapter(adapter);

        setUserInfo();
    }

    void setUserInfo(){


                etDisplayname.setText(currentUser.getDisplayName().toString());
                tvDateofBirth.setText(currentUser.getDateOfBirth().toString());
                etPhoneNo.setText(currentUser.getPhoneNo().toString());

                if(currentUser.getGender().equals("Male"))
                    spinnerGender.setSelection(0);
                else
                    spinnerGender.setSelection(1);

    }


    @OnClick(R.id.btn_update_profile)
    void setBtnUpdateProfile(){

        String displayName = etDisplayname.getText().toString().trim();
        String phoneNo = etPhoneNo.getText().toString().trim();
        String dateOfBirth = tvDateofBirth.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().trim();

        if(gender.equals("Nam") || gender.equals("Male"))
            gender = "Male";
        else
            gender = "Female";


        if(displayName.equals("") || phoneNo.equals("")){
            tvErrorMessage.setText(getString(R.string.register_blank_error));
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
        else if (etPhoneNo.getText().length() < 10 || etPhoneNo.getText().length() >11){
            tvErrorMessage.setText(getString(R.string.error_phone_length));
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
        else{

            currentUser.setDisplayName(displayName);
            currentUser.setDateOfBirth(dateOfBirth);
            currentUser.setPhoneNo(phoneNo);
            currentUser.setGender(gender);

            DatabaseReference usersRef = database.getReference("users/"+mAuth.getCurrentUser().getUid());

            usersRef.setValue(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UpdateProfileActivity.this.finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            tvErrorMessage.setText(getString(R.string.register_unhandled) + e.getMessage());
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
                    });

        }

    }


    @OnClick(R.id.btn_datepicker)
    void setDoBSpinner(){
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                tvDateofBirth.setText(
                        (dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=tvDateofBirth.getText()+"";
        String strArrtmp[]=s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                UpdateProfileActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle(getResources().getString(R.string.birthdaytitle));
        pic.show();
    }

}
