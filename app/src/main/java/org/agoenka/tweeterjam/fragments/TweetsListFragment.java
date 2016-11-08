package org.agoenka.tweeterjam.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.ItemClickSupport;
import org.agoenka.tweeterjam.adapters.TweetsAdapter;
import org.agoenka.tweeterjam.databinding.FragmentTweetsListBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_FAVORITE;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_RETWEET;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_UNFAVORITE;
import static org.agoenka.tweeterjam.utils.AppUtils.ACTION_UNRETWEET;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */
public abstract class TweetsListFragment extends Fragment {

    private FragmentTweetsListBinding binding;
    private List<Tweet> mTweets;
    private TweetsAdapter mAdapter;
    TwitterClient client;

    private OnItemSelectedListener itemSelectedListener;
    private OnProfileSelectedListener profileSelectedListener;
    private OnReplyListener replyListener;
    OnLoadingListener loadingListener;
    private EndlessRecyclerViewScrollListener scrollListener;
    private long currMinId = 0;

    public interface OnItemSelectedListener {
        void onItemSelected(Tweet tweet);
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected(User user);
    }

    public interface OnReplyListener {
        void onReply(Tweet tweet);
    }

    public interface OnLoadingListener {
        void onLoad(boolean loading);
    }

    // Creation Lifecycle Event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweets = new ArrayList<>();
        mAdapter = new TweetsAdapter(getActivity(), mTweets);
        client = TweeterJamApplication.getTwitterClient();
    }

    // Inflation Logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tweets_list, container, false);
        binding.rvTweets.setAdapter(mAdapter);
        setupViews();

        if (!isConnected(getContext())) {
            List<Tweet> tweets = loadStaticTimeline(0);
            addAll(tweets, true);
        } else {
            clearStaticTimeline();
            populateTimeline(0, true);
        }

        return binding.getRoot();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener)
            itemSelectedListener = (OnItemSelectedListener) context;
        if (context instanceof OnProfileSelectedListener)
            profileSelectedListener = (OnProfileSelectedListener) context;
        if (context instanceof OnReplyListener)
            replyListener = (OnReplyListener) context;
        if (context instanceof OnLoadingListener)
            loadingListener = (OnLoadingListener) context;
    }

    private void setupViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvTweets.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(binding.rvTweets).setOnItemClickListener((recyclerView, position, v) -> itemSelectedListener.onItemSelected(mTweets.get(position)));
        mAdapter.setProfileListener(v -> profileSelectedListener.onProfileSelected((User) v.getTag()));
        mAdapter.setReplyListener(v -> replyListener.onReply((Tweet) v.getTag()));
        mAdapter.setRetweetListener((tweet, position) -> {
            if (position != RecyclerView.NO_POSITION) {
                if (tweet.isRetweeted())
                    unretweet(tweet, position);
                else
                    retweet(tweet, position);
            }
        });
        mAdapter.setFavoriteListener((tweet, position) -> {
            if (position != RecyclerView.NO_POSITION) {
                if (tweet.isFavorited())
                    unfavorite(tweet, position);
                else
                    favorite(tweet, position);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {
                if (loadingListener != null) loadingListener.onLoad(true);
                currMinId = Tweet.getMinId(mTweets);
                populateTimeline(currMinId > 0 ? currMinId - 1 : 0, false);
            }
        };
        binding.rvTweets.addOnScrollListener(scrollListener);

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            // once the network request has completed successfully swipeContainer.setRefreshing(false) must be called.
            mTweets.clear();
            scrollListener.resetState();
            populateTimeline(0, true);
            binding.swipeContainer.setRefreshing(false);
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void retweet(final Tweet tweet, final int position) {
        if (isConnected(getContext())) {
            client.retweet(tweet.getUid(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(tweet, position, ACTION_RETWEET);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to retweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unretweet(final Tweet tweet, final int position) {
        if (isConnected(getContext())) {
            client.unretweet(tweet.getUid(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(tweet, position, ACTION_UNRETWEET);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unretweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void favorite(final Tweet tweet, final int position) {
        if (isConnected(getContext())) {
            client.favorite(tweet.getUid(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(tweet, position, ACTION_FAVORITE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to favorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unfavorite(final Tweet tweet, final int position) {
        if (isConnected(getContext())) {
            client.unfavorite(tweet.getUid(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateTweet(tweet, position, ACTION_UNFAVORITE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unfavorite at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateTweet(Tweet tweet, int position, int action) {
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
        mAdapter.update(tweet, position);
    }

    private void handleFailure(String response, Throwable t) {
        Log.d("DEBUG", response);
        Log.d("DEBUG", t.getLocalizedMessage());
    }

    abstract void populateTimeline(final long maxId, final boolean refresh);

    abstract List<Tweet> loadStaticTimeline(final long maxId);

    abstract void clearStaticTimeline();

    void addAll(List<Tweet> tweets, boolean refresh) {
        mAdapter.addAll(tweets, refresh);
    }

    public void add(int index, Tweet tweet) {
        mAdapter.add(index, tweet);
    }
}