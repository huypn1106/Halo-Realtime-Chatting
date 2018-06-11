package com.deadk.halo.ultilities;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class ImageBrowser {
    private Context context;
    final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
    final String orderBy = MediaStore.Images.Media.DATE_MODIFIED;

    public ImageBrowser(Context context){
        this.context = context;
    }

    public String[] browse(){
        //Stores all the images from the gallery in Cursor
        Cursor cursorInner = context.getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        Cursor cursorExter = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);

        //Total number of images
        int countInner = cursorInner.getCount();
        int countExter = cursorExter.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[countInner + countExter];

        for (int i = 0; i < countInner; i++) {
            cursorInner.moveToPosition(i);
            int dataColumnIndex = cursorInner.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursorInner.getString(dataColumnIndex);
            Log.i("PATH", arrPath[i]);
        }
        for (int i = 0; i < countExter; i++) {
            cursorExter.moveToPosition(i);
            int dataColumnIndex = cursorExter.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursorExter.getString(dataColumnIndex);
            Log.i("PATH", arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursorInner.close();
        cursorExter.close();
        return arrPath;
    }
}
