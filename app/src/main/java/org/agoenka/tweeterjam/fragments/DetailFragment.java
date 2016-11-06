package org.agoenka.tweeterjam.fragments;

import android.content.Intent;
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
import org.agoenka.tweeterjam.activities.ProfileActivity;
import org.agoenka.tweeterjam.databinding.FragmentTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_LOGGED_IN_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_TWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
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
    private User mLoggedInUser;
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TweeterJamApplication.getTwitterClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Tweet tweet = Parcels.unwrap(getArguments().getParcelable(KEY_TWEET));
        mLoggedInUser = Parcels.unwrap(getArguments().getParcelable(KEY_LOGGED_IN_USER));

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

    public static DetailFragment newInstance(Tweet tweet, User loggedInUser) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_TWEET, Parcels.wrap(tweet));
        args.putParcelable(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        detailFragment.setArguments(args);
        return detailFragment;
    }

    public class Handlers {

        public void onProfile(@SuppressWarnings("unused") View view) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra(KEY_LOGGED_IN_USER, Parcels.wrap(mLoggedInUser));
            intent.putExtra(KEY_USER, Parcels.wrap(binding.getTweet().getUser()));
            startActivity(intent);
        }

        public void onReply(@SuppressWarnings("unused") View view) {
            ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null, mLoggedInUser, binding.getTweet());
            composeDialog.setListener(tweet -> Toast.makeText(getContext(), "Replied Successfully!", Toast.LENGTH_SHORT).show());
            composeDialog.show(getFragmentManager(), "Compose Tweet");
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

        public void onShare(@SuppressWarnings("unused") View view) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, binding.getTweet().getBody());
            startActivity(Intent.createChooser(shareIntent, "Share using"));
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
