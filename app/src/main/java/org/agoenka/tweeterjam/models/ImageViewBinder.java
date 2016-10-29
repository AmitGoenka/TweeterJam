package org.agoenka.tweeterjam.models;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
        Picasso.with(view.getContext()).load(url).into(view);
    }

}
