package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

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

        // 2. Find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        // 3. Find the subviews to fill with data in the template
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);

        // 4. Populate the data into the subviews
        tvName.setText(tweet.getUser().getName());
        tvUserName.setText(tweet.getUser().getScreenName());
        tvDuration.setText(tweet.getDuration());
        tvBody.setText(tweet.getBody());
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        // 5. Return the view to be inserted into the list
        return convertView;
    }
}
