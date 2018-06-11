package com.deadk.halo.ultilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.activities.UserInfoActivity;
import com.deadk.halo.fragments.ProfileFragment;
import com.deadk.halo.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mDatasource;

    public FriendAdapter(Context context, ArrayList<User> items){
        mContext = context;
        mDatasource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDatasource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatasource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = mInflater.inflate(R.layout.list_item_friend, parent, false);

        CircleImageView imgAvatar = rowView.findViewById(R.id.img_avatar);
        TextView tvTitle = rowView.findViewById(R.id.textview_title);
        TextView tvSubtitle = rowView.findViewById(R.id.textview_subtitle);
        ImageButton btnMessage = rowView.findViewById(R.id.btn_message);


        final User user = (User) getItem(position);

        tvTitle.setText(user.getDisplayName());
        tvSubtitle.setText("@" + user.getUsername());

        StorageReference avtRef = FirebaseStorage.getInstance().getReference("images/" + user.getUid() + "/avatar");

        RequestOptions requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_user_image);

        Glide.with(mContext)
                .load(avtRef)
                .apply(requestOptionsAvt)
                .into(imgAvatar);

//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, UserInfoActivity.class);
//                intent.putExtra("pickedUser", user);
//                mContext.startActivity(intent);
//            }
//        });

        return rowView;
    }


}
