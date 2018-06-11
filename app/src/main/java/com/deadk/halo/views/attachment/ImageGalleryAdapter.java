package com.deadk.halo.views.attachment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.deadk.halo.R;
import com.deadk.halo.common.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends BaseAdapter{
    Context context;
    ArrayList<String> selectedImg;
    private ImageLoader imageLoader;
    private static LayoutInflater inflater = null;
    String[] imagePath;
    public ImageGalleryAdapter(Context context, String[] imagePath) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.imagePath = imagePath;
        //danh sách image được chọn
        selectedImg = new ArrayList<String>();
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public int getCount() {
        // TODO Auto-generated method stub
        return imagePath.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imagePath[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.image_gallery_item, null);

        holder.img = rowView.findViewById(R.id.imgQueue);
        //holder.img.setImageResource(imageId[position]);
        imageLoader.loadImage(holder.img, imagePath[position]);
        holder.img.setTag(imagePath[position]);

        holder.img_check = rowView.findViewById(R.id.imgSelected);
        holder.img_check.setImageResource(R.drawable.checkbox_up);
        holder.img_check.setTag(R.drawable.checkbox_up);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Integer)holder.img_check.getTag() == R.drawable.checkbox_up) {
                    holder.img_check.setImageResource(R.drawable.checkbox_selected);
                    holder.img_check.setTag(R.drawable.checkbox_selected);
                    selectedImg.add(holder.img.getTag().toString());
                }
                else {
                    holder.img_check.setImageResource(R.drawable.checkbox_up);
                    holder.img_check.setTag(R.drawable.checkbox_up);
                    removeItem(selectedImg,holder.img.getTag().toString());
                }
            }
        });
        return rowView;
    }

    public class Holder
    {
        ImageView img_check;
        ImageView img;
    }

    private void removeItem(List<String> list, String value){
        for(int i=0; i<list.size(); i++){
            if(list.get(i) == value){
                list.remove(i);
                return;
            }
        }
    }

    public ArrayList<String> getSelectedImg(){
        return selectedImg;
    }
}
