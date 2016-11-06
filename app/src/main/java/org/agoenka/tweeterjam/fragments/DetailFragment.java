package org.agoenka.tweeterjam.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.FragmentTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

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
        if(context instanceof OnProfileSelectedListener)
            profileListener = (OnProfileSelectedListener) context;
        if(context instanceof OnShareListener)
            shareListener = (OnShareListener) context;
        if(context instanceof OnReplyListener)
            replyListener = (OnReplyListener) context;
    }

    public class Handlers {

        public void onProfile(@SuppressWarnings("unused") View view) {
            profileListener.onProfileSelected(binding.getTweet().getUser());
        }

        public void onReply(@SuppressWarnings("unused") View view) {
            replyListener.onReply(binding.getTweet());
        }

        public void onShare(@SuppressWarnings("unused") View view) {
            shareListener.onShare(binding.getTweet());
        }

        public void onRetweet(@SuppressWarnings("unused") View view) {
            if (!binding.getTweet().isRetweeted()) {
                retweet(binding.getTweet().getUid());
            }
        }

        public void onFavorite(@SuppressWarnings("unused") View view) {
            if (!binding.getTweet().isFavorited()) {
                favorite(binding.getTweet().getUid());
            }
        }
    }

    private void retweet(long uid) {
        if (isConnected(getContext())) {
            client.postRetweet(uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    binding.getTweet().setRetweeted(true);
                    binding.ibRetweet.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_retweet_green));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(getContext(), "Unable to retweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void favorite(long uid) {
        if (isConnected(getContext())) {
            client.createFavorite(uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    binding.getTweet().setFavorited(true);
                    binding.ibFavorite.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_favorite_green));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(getContext(), "Unable to favorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}