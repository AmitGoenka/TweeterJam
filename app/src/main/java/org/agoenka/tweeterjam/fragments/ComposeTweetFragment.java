package org.agoenka.tweeterjam.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.FragmentComposeTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;

public class ComposeTweetFragment extends DialogFragment {

    private FragmentComposeTweetBinding binding;
    private TwitterClient client;
    private Tweet replyToTweet;

    private static final String KEY_TITLE = "title";
    public static final String LOGGED_IN_USER_KEY = "loggedInUser";
    public static final String IN_REPLY_TO_KEY = "inReplyTo";

    private static final int MAX_TWEET_LENGTH = 140;

    public interface ComposeTweetFragmentListener {
        void onTweet(Tweet tweet);
    }

    private ComposeTweetFragmentListener listener;

    public void setListener(ComposeTweetFragmentListener listener) {
        this.listener = listener;
    }

    public static ComposeTweetFragment newInstance(String title, User loggedInUser, Tweet inReplyTo) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        if (loggedInUser != null)
            args.putParcelable(LOGGED_IN_USER_KEY, Parcels.wrap(loggedInUser));
        if (inReplyTo != null)
            args.putParcelable(IN_REPLY_TO_KEY, Parcels.wrap(inReplyTo));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        User user = Parcels.unwrap(getArguments().getParcelable(LOGGED_IN_USER_KEY));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_tweet, container, false);
        binding.setHandlers(new Handlers());
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        replyToTweet = Parcels.unwrap(getArguments().getParcelable(IN_REPLY_TO_KEY));
        setTextChangeListener();

        if (replyToTweet != null) {
            binding.etTweet.setText(String.format("%s ", replyToTweet.getUser().getScreenName()));
            binding.etTweet.setSelection(binding.etTweet.getText().length());
            binding.tvInReplyTo.setText(String.format("In reply to %s", replyToTweet.getUser().getName()));
            binding.tvInReplyTo.setVisibility(View.VISIBLE);
        }

        client = TweeterJamApplication.getTwitterClient();
    }

    private void setTextChangeListener() {
        final int startColor = binding.tvCharCount.getCurrentTextColor();
        binding.tvCharCount.setText(String.format(Locale.getDefault(), "%d", MAX_TWEET_LENGTH));
        binding.etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                int len = MAX_TWEET_LENGTH - s.length();
                binding.tvCharCount.setText(String.format(Locale.getDefault(), "%d", len));
                if (len < 0) {
                    binding.tvCharCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    binding.btnTweet.setEnabled(false);
                } else if (!binding.btnTweet.isEnabled()) {
                    binding.tvCharCount.setTextColor(startColor);
                    binding.btnTweet.setEnabled(true);
                }
            }
        });
    }

    public class Handlers {
        public void onCancel(@SuppressWarnings("unused") View view) {
            Toast.makeText(getContext(), "Tweet Canceled", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        public void onTweet(@SuppressWarnings("unused") View view) {
            if (replyToTweet != null)
                postTweet(binding.etTweet.getText().toString(), replyToTweet.getUid());
            else
                postTweet(binding.etTweet.getText().toString(), 0);
        }
    }

    private void postTweet(String status, long inReplyTo) {
        if (isConnected(getContext())) {
            client.postTweet(status, inReplyTo, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    Tweet tweet = Tweet.fromJSON(response);
                    if (listener != null)
                        listener.onTweet(tweet);
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(getContext(), "Unable to post tweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
