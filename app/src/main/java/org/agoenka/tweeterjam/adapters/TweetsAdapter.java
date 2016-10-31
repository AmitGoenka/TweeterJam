package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ItemTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.binding.setTweet(tweet);
        holder.binding.executePendingBindings();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTweetBinding binding;
        ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
        }
    }

}