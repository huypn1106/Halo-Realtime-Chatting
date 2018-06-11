package com.deadk.halo.ultilities;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deadk.halo.R;

public class GeneralUltilities {

    public static boolean init=false;

    public static RequestOptions requestOptionsAvt = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_user_image);

    public static RequestOptions requestOptionsCover = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)
                .placeholder(R.drawable.color_blue_gradient);

}
