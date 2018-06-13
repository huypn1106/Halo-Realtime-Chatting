package com.deadk.halo.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.activities.MainActivity;
import com.deadk.halo.activities.MainScreen;
import com.deadk.halo.activities.SplashScreen;
import com.deadk.halo.activities.UpdateProfileActivity;
import com.deadk.halo.data.DataProvider;
import com.deadk.halo.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    @BindView(R.id.textview_name)
    TextView tvName;
    @BindView(R.id.img_cover_photo)
    ImageView imgCover;
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.textview_display_name)
    TextView tvDisplayname;
    @BindView(R.id.textview_username)
    TextView tvUsername;
    @BindView(R.id.textview_email)
    TextView tvEmail;
    @BindView(R.id.textview_phoneno)
    TextView tvPhoneNo;
    @BindView(R.id.textview_dob)
    TextView tvDateOfBirth;
    @BindView(R.id.textview_gender)
    TextView tvGender;
    @BindView(R.id.btn_update_profile)
    Button btnUpdateProfile;
    @BindView(R.id.btn_change_avatar)
    ImageButton btnChangeAvatar;
    @BindView(R.id.btn_change_cover)
    ImageButton btnChangeCover;


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = DataProvider.getInstance().getDatabase();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    private User currentUser;


    RequestOptions requestOptionsAvt;
    RequestOptions requestOptionsCover;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private boolean cropimage; // false:cover, true: avatar

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View V = inflater.inflate(com.deadk.halo.R.layout.fragment_profile, container, false);

        ButterKnife.bind(this,V);
        setUserInfo();
        preSet();

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    void preSet(){

        requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_user_image);

        requestOptionsCover = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.color_blue_gradient);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void setUserInfo(){

        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("hihihi", "day la trong Check set user info 1");

                currentUser = dataSnapshot.getValue(User.class);

                tvName.setText(currentUser.getDisplayName().toString());
                tvDateOfBirth.setText(currentUser.getDateOfBirth().toString());
                tvDisplayname.setText(currentUser.getDisplayName().toString());
                tvUsername.setText(currentUser.getUsername().toString());
                tvEmail.setText(currentUser.getEmailAddress().toString());
                tvPhoneNo.setText(currentUser.getPhoneNo().toString());

                String gender = currentUser.getGender().equals("Male")?getResources().getString(R.string.male):getResources().getString(R.string.female);

                tvGender.setText(gender);
                imgAvatar.setImageURI(user.getPhotoUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgCover.setScaleType(ImageView.ScaleType.FIT_XY);
        storageReference = storage.getReference();

        loadingImage.execute();

    }


    @OnClick(R.id.btn_change_cover)
    void chooseCoverImage() {
        cropimage=false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @OnClick(R.id.btn_change_avatar)
    void chooseAvatarImage() {
        cropimage=true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @OnClick(R.id.btn_update_profile)
    void setBtnUpdateProfile(){

        Intent intent = new Intent(getActivity().getBaseContext(), UpdateProfileActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;


            if(!cropimage) {
                CropImage.activity(filePath)
                        .setAspectRatio(width, height / 5)
                        .start(getContext(), this);
            }
            else {
                CropImage.activity(filePath)
                        .setAspectRatio(1, 1)
                        .start(getContext(), this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();



                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(!cropimage) {

                    Glide.with(ProfileFragment.this)
                            .load(bitmap)
                            .apply(requestOptionsCover)
                            .into(imgCover);
                    uploadImage(resultUri,"cover");
                }
                else{
                    Glide.with(ProfileFragment.this)
                            .load(bitmap)
                            .apply(requestOptionsAvt)
                            .into(imgAvatar);
                    uploadImage(resultUri,"avatar");
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void uploadImage(Uri path, String name) {

        if(path != null)
        {


            final StorageReference ref = storageReference.child("images/"+user.getUid()+ "/" + name);

            UploadTask uploadTask = ref.putFile(path);


            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        Log.d("deadk", "onComplete: " + downloadUri);

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });




        }
    }




    private AsyncTask<Void,Integer,Void> loadingImage = new AsyncTask<Void, Integer, Void>() {
        @Override
        protected Void doInBackground(Void... urls) {

            Glide.get(getActivity().getBaseContext()).clearDiskCache();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            StorageReference imageRef = storageReference.child("images/"+user.getUid());


            Glide.with(ProfileFragment.this)
                    .load(imageRef.child("cover"))
                    .apply(requestOptionsCover)
                    .into(imgCover);

            Glide.with(ProfileFragment.this)
                    .load(imageRef.child("avatar"))
                    .apply(requestOptionsAvt)
                    .into(imgAvatar);

        }
    };



}
