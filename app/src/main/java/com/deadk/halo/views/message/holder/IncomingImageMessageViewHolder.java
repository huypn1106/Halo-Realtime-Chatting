package com.deadk.halo.views.message.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.ultilities.RoundedImageView;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class IncomingImageMessageViewHolder<MessageObj extends com.deadk.halo.dao.model.Message>
        extends BaseMessageViewHolder<MessageObj> {

    protected ImageView image;
    protected ImageView ivAvatar;
    private View onlineIndicator;
    private Context context;
    public static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    public IncomingImageMessageViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        ivAvatar = itemView.findViewById(R.id.messageUserAvatar);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);

        if (image != null && image instanceof RoundedImageView) {
            ((RoundedImageView) image).setCorners(
                    R.dimen.message_bubble_corners_radius,
                    R.dimen.message_bubble_corners_radius,
                    R.dimen.message_bubble_corners_radius,
                    0
            );
        }
    }

    @Override
    public void onBind(MessageObj message) {
        super.onBind(message);
        //set image cho tin nhắn
        String urlImage = "messageImage/" + message.getId();
        loadImage(image, urlImage, "image.jpg");

        //Set avatar của tin nhắn
        String urlAvar = "userAvatar/" + message.getSenderId();
        loadImage(ivAvatar, urlAvar, "avatar.jpg");


        //set image chỉ định online
        DatabaseReference isOnlineRef = mRootRef.child("users").child(message.getSenderId()).child("isOnline");
        isOnlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onlineIndicator.setVisibility(View.VISIBLE);
                if (Integer.parseInt(dataSnapshot.getValue().toString()) == 1) {
                    onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
                } else {
                    onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void loadImage(ImageView imageView, String url, String fileName){

        final RequestOptions requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.loading); //truong hop load k co anh

        StorageReference avartarRef = storageRef.child(url + "/" + fileName);
        File imgFolder = new File(Environment.getExternalStorageDirectory(), "Halo/" + url);
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        //tạo file trong đường dẫn nếu có
        final File imgFile = new File(imgFolder, fileName);
        if(imgFile.exists()){
            //load ảnh từ filepath vào imageview
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            Log.i("load image", url);
        }
        else {
            //nếu chưa có ảnh thì tải từ FirebseStorage
            Glide.with(context).applyDefaultRequestOptions(requestOptionsAvt).load(avartarRef).into(imageView);
            avartarRef.getFile(imgFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.i("save image", "success");
                }
            });
        }
    }
}
