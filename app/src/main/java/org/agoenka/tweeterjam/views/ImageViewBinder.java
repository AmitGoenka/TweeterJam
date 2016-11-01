package org.agoenka.tweeterjam.views;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Author: agoenka
 * Created At: 10/29/2016
 * Version: ${VERSION}
 */

public class ImageViewBinder {

    private ImageViewBinder() {
        //no instance
    }

    @BindingAdapter({"bind:profileImageUrl"})
    public static void loadProfileImage(ImageView view, String url) {
        view.setImageResource(android.R.color.transparent);
        Glide.with(view.getContext())
                .load(url)
                .fitCenter()
                .into(view);
    }

    @BindingAdapter({"bind:mediaImageUrl"})
    public static void loadMediaImage(ImageView view, String url) {
        view.setImageResource(android.R.color.transparent);
        if (!TextUtils.isEmpty(url)) {
            Glide.with(view.getContext())
                    .load(url)
                    .into(view);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}