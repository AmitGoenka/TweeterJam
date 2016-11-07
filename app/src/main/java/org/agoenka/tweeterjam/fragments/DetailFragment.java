package org.agoenka.tweeterjam.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.FragmentTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_FAVORITE;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_RETWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_UNFAVORITE;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_UNRETWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.views.ImageViewBinder.loadMediaImage;
import static org.agoenka.tweeterjam.views.VideoViewBinder.loadFensterVideo;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */

public class DetailFragment extends Fragment {

    private FragmentTweetBinding binding;
    private TwitterClient client;

    private OnProfileSelectedListener profileListener;
    private OnShareListener shareListener;
    private OnReplyListener replyListener;

    public interface OnProfileSelectedListener {
        void onProfileSelected(User user);
    }

    public interface OnShareListener {
        void onShare(Tweet tweet);
    }

    public interface OnReplyListener {
        void onReply(Tweet tweet);
    }

    public static DetailFragment newInstance(Tweet tweet) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_TWEET, Parcels.wrap(tweet));
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TweeterJamApplication.getTwitterClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Tweet tweet = Parcels.unwrap(getArguments().getParcelable(KEY_TWEET));

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweet, container, false);
        binding.setHandlers(new Handlers());
        binding.setTweet(tweet);

        if (tweet.hasVideo()) {
            loadFensterVideo(getActivity().getWindow(),
                    binding.rlVideo,
                    binding.vvFensterVideo,
                    binding.vvFensterController,
                    tweet.getExtendedEntity().getVideoUrl());
        } else {
            loadMediaImage(binding.ivImage, binding.getTweet().getEntity().getMediaUrl());
        }

        return binding.getRoot();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileSelectedListener)
            profileListener = (OnProfileSelectedListener) context;
        if (context instanceof OnShareListener)
            shareListener = (OnShareListener) context;
        if (context instanceof OnReplyListener)
            replyListener = (OnReplyListener) context;
    }

    public class Handlers {

        public void onProfile(@SuppressWarnings("unused") View view) {
            profileListener.onProfileSelected(binding.getTweet().getUser());
        }

        public void onShare(@SuppressWarnings("unused") View view) {
            shareListener.onShare(binding.getTweet());
        }

        public void onRetweet(@SuppressWarnings("unused") View view) {
            if (binding.getTweet().isRetweeted())
                unretweet(binding.getTweet().getUid());
            else
                retweet(binding.getTweet().getUid());
        }

        public void onFavorite(@SuppressWarnings("unused") View view) {
            if (binding.getTweet().isFavorited())
                unfavorite(binding.getTweet().getUid());
            else
                favorite(binding.getTweet().getUid());
        }

        public void onReply(@SuppressWarnings("unused") View view) {
            replyListener.onReply(binding.getTweet());
        }
    }

    private void retweet(long uid) {
        if (isConnected(getContext())) {
            client.retweet(uid, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(responseString, ACTION_RETWEET);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to retweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unretweet(long uid) {
        if (isConnected(getContext())) {
            client.unretweet(uid, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(responseString, ACTION_UNRETWEET);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unretweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void favorite(long uid) {
        if (isConnected(getContext())) {
            client.favorite(uid, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(responseString, ACTION_FAVORITE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to favorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unfavorite(long uid) {
        if (isConnected(getContext())) {
            client.unfavorite(uid, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(responseString, ACTION_UNFAVORITE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unfavorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateTweet(String json, int action) {
        Log.d("DEBUG", json);
        Tweet tweet = binding.getTweet();
        switch (action) {
            case ACTION_RETWEET:
                tweet.setRetweeted(true);
                tweet.incRetweetCount(1);
                break;
            case ACTION_UNRETWEET:
                tweet.setRetweeted(false);
                tweet.incRetweetCount(-1);
                break;
            case ACTION_FAVORITE:
                tweet.setFavorited(true);
                tweet.incFavoriteCount(1);
                break;
            case ACTION_UNFAVORITE:
                tweet.setFavorited(false);
                tweet.incFavoriteCount(-1);
                break;
        }
        binding.setTweet(tweet);
    }

    private void handleFailure(String response, Throwable t) {
        Log.d("DEBUG", response);
        Log.d("DEBUG", t.getLocalizedMessage());
    }
}