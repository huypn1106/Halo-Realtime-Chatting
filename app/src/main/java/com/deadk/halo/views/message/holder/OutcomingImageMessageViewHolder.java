package com.deadk.halo.views.message.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.dao.model.Message;
import com.deadk.halo.ultilities.RoundedImageView;
import com.deadk.halo.views.message.base.BaseMessageViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class OutcomingImageMessageViewHolder<MessageObj extends Message>
        extends BaseMessageViewHolder<MessageObj> {

    protected ImageView image;
    private Context context;
    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public OutcomingImageMessageViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);

        if (image != null && image instanceof RoundedImageView) {
            ((RoundedImageView) image).setCorners(
                    R.dimen.message_bubble_corners_radius,
                    R.dimen.message_bubble_corners_radius,
                    0,
                    R.dimen.message_bubble_corners_radius
            );
        }
    }

    @Override
    public void onBind(MessageObj message) {
        super.onBind(message);
        //set image cho tin nhắn
        String urlImage = "messageImage/" + message.getId();
        //gọi hàm load image
        loadImage(image, urlImage, "image.jpg");
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
        }
        else {
            //nếu chưa có ảnh thì tải từ FirebseStorage
            Glide.with(context).applyDefaultRequestOptions(requestOptionsAvt).load(avartarRef).into(imageView);
            avartarRef.getFile(imgFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
    }
}
