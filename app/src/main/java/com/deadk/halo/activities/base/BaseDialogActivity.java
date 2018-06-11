package com.deadk.halo.activities.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.deadk.halo.common.ImageLoader;
import com.deadk.halo.common.listener.OnDialogClickListener;
import com.deadk.halo.dao.model.Dialog;
import com.deadk.halo.ultilities.AppUtils;
import com.deadk.halo.views.dialog.DialogListAdapter;

import java.io.File;

public class BaseDialogActivity extends AppCompatActivity implements OnDialogClickListener<Dialog> {
    protected ImageLoader imageLoader;
    protected DialogListAdapter<Dialog> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                File imgFile = new  File(url);

                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
        };
    }


    @Override
    public void onDialogClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                "dialog has been click",
                false);
    }
}
