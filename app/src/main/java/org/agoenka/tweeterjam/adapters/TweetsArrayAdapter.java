package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ItemTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

import static org.agoenka.tweeterjam.R.id.ivProfileImage;
import static org.agoenka.tweeterjam.R.id.tvBody;
import static org.agoenka.tweeterjam.R.id.tvDuration;
import static org.agoenka.tweeterjam.R.id.tvName;
import static org.agoenka.tweeterjam.R.id.tvUserName;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // 1. Get the tweet
        Tweet tweet = getItem(position);
        assert tweet != null;

        ViewHolder holder;

        // 2. Find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            holder = new ViewHolder(convertView);
            holder.tvName = (TextView) convertView.findViewById(tvName);
            holder.tvUserName = (TextView) convertView.findViewById(tvUserName);
            holder.tvDuration = (TextView) convertView.findViewById(tvDuration);
            holder.tvBody = (TextView) convertView.findViewById(tvBody);
            holder.ivProfileImage = (ImageView) convertView.findViewById(ivProfileImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 4. Populate the data into the subviews
        holder.binding.setTweet(tweet);

        // 5. Return the view to be inserted into the list
        return convertView;
    }

    private static class ViewHolder {
        final ItemTweetBinding binding;
        TextView tvName;
        TextView tvUserName;
        TextView tvDuration;
        TextView tvBody;
        ImageView ivProfileImage;

        ViewHolder(View root) {
            binding = ItemTweetBinding.bind(root);
        }
    }

    @BindingAdapter({"bind:profileImageUrl"})
    public static void loadProfileImage(ImageView view, String url) {
        view.setImageResource(android.R.color.transparent);
        Picasso.with(view.getContext()).load(url).into(view);
    }

}