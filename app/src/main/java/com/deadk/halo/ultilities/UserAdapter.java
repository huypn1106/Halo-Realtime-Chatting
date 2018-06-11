package com.deadk.halo.ultilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;
import com.deadk.halo.fragments.ProfileFragment;
import com.deadk.halo.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mDatasource;
    private ArrayList<User> mDatasourceFilter;
    ValueFilter valueFilter;

    public UserAdapter(Context context, ArrayList<User> items){
        mContext = context;
        mDatasource = items;
        mDatasourceFilter = items;
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

        View rowView = mInflater.inflate(R.layout.list_item_user, parent, false);

        CircleImageView imgAvatar = rowView.findViewById(R.id.img_avatar);
        TextView tvTitle = rowView.findViewById(R.id.textview_title);
        TextView tvSubtitle = rowView.findViewById(R.id.textview_subtitle);

        User user = (User) getItem(position);

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

        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<User> filterList = new ArrayList<>();
                for (int i = 0; i < mDatasourceFilter.size(); i++) {
                    if ((mDatasourceFilter.get(i).getDisplayName()).toLowerCase().contains(constraint.toString().toLowerCase())
                            || mDatasourceFilter.get(i).getUsername().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            mDatasourceFilter.get(i).getPhoneNo().toLowerCase().contains(constraint.toString().toLowerCase()) ) {
                        filterList.add(mDatasourceFilter.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            }
            else {
                List<User> filterList = new ArrayList<>();
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mDatasource = (ArrayList<User>) results.values;
            notifyDataSetChanged();
        }

    }

}
