package org.agoenka.tweeterjam.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.ItemClickSupport;
import org.agoenka.tweeterjam.adapters.TweetsAdapter;
import org.agoenka.tweeterjam.databinding.FragmentTweetsListBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;

import java.util.ArrayList;
import java.util.List;

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

    private OnItemSelectedListener itemSelectedListener;
    private OnProfileSelectedListener profileSelectedListener;
    private OnReplyListener replyListener;
    protected OnLoadingListener loadingListener;
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
        if(context instanceof OnItemSelectedListener)
            itemSelectedListener = (OnItemSelectedListener) context;
        if(context instanceof OnProfileSelectedListener)
            profileSelectedListener = (OnProfileSelectedListener) context;
        if(context instanceof OnReplyListener)
            replyListener = (OnReplyListener) context;
        if(context instanceof OnLoadingListener)
            loadingListener = (OnLoadingListener) context;
    }

    private void setupViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvTweets.setLayoutManager(layoutManager);

        ItemClickSupport.addTo(binding.rvTweets).setOnItemClickListener((recyclerView, position, v) -> itemSelectedListener.onItemSelected(mTweets.get(position)));
        mAdapter.setProfileListener(v -> profileSelectedListener.onProfileSelected((User) v.getTag()));
        mAdapter.setReplyListener(v -> replyListener.onReply((Tweet) v.getTag()));

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {
                if(loadingListener != null) loadingListener.onLoad(true);
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

    abstract void populateTimeline(final long maxId, final boolean refresh);

    abstract List<Tweet> loadStaticTimeline(final long maxId);

    abstract void clearStaticTimeline();

    public void addAll(List<Tweet> tweets, boolean refresh) {
        mAdapter.addAll(tweets, refresh);
    }

    public void add(int index, Tweet tweet) {
        mAdapter.add(index, tweet);
    }
}