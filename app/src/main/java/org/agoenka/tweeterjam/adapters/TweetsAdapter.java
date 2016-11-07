package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ItemTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;

import java.util.List;

import static org.agoenka.tweeterjam.views.ImageViewBinder.loadMediaImage;
import static org.agoenka.tweeterjam.views.VideoViewBinder.loadScalableVideo;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private final List<Tweet> mTweets;
    private final Context mContext;
    private View.OnClickListener mProfileListener;
    private View.OnClickListener mReplyListener;
    private OnRetweetListener mRetweetListener;
    private OnFavoriteListener mFavoriteListener;

    public interface OnRetweetListener {
        void onRetweet(Tweet tweet, int position);
    }

    public interface OnFavoriteListener {
        void onFavorite(Tweet tweet, int position);
    }

    public void setProfileListener(View.OnClickListener profileListener) {
        mProfileListener = profileListener;
    }

    public void setReplyListener(View.OnClickListener replyListener) {
        mReplyListener = replyListener;
    }

    public void setRetweetListener(OnRetweetListener retweetListener) {
        this.mRetweetListener = retweetListener;
    }

    public void setFavoriteListener(OnFavoriteListener favoriteListener) {
        this.mFavoriteListener = favoriteListener;
    }

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

        holder.binding.ivProfileImage.setTag(tweet.getUser());
        holder.binding.ivProfileImage.setOnClickListener(mProfileListener);

        holder.binding.ibReply.setTag(tweet);
        holder.binding.ibReply.setOnClickListener(mReplyListener);

        holder.binding.ibRetweet.setTag(tweet);
        holder.binding.ibRetweet.setOnClickListener(v -> mRetweetListener.onRetweet((Tweet) v.getTag(), holder.getAdapterPosition()));

        holder.binding.ibFavorite.setTag(tweet);
        holder.binding.ibFavorite.setOnClickListener(v -> mFavoriteListener.onFavorite((Tweet) v.getTag(), holder.getAdapterPosition()));

        if (tweet.hasVideo()) {
            loadScalableVideo(getContext(), holder.binding.vvVideo, tweet.getExtendedEntity().getVideoUrl());
        } else {
            loadMediaImage(holder.binding.ivImage, tweet.getEntity().getMediaUrl());
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.binding.ivImage);
        holder.binding.ivImage.setVisibility(View.GONE);
        holder.binding.vvVideo.setVisibility(View.GONE);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTweetBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
        }
    }

    public void addAll(List<Tweet> tweets, boolean refresh) {
        if (refresh) {
            mTweets.clear();
            mTweets.addAll(tweets);
            notifyDataSetChanged();
        } else {
            int currentSize = getItemCount();
            mTweets.addAll(tweets);
            notifyItemRangeInserted(currentSize, mTweets.size() - currentSize);
        }
    }

    public void add(int index, Tweet tweet) {
        mTweets.add(index, tweet);
        notifyItemRangeInserted(index, 1);
    }

    public void update(Tweet tweet, int position) {
        mTweets.set(position, tweet);
        notifyItemChanged(position, tweet);
    }
}