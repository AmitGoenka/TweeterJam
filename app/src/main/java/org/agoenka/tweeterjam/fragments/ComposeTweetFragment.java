package org.agoenka.tweeterjam.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.FragmentComposeTweetBinding;
import org.agoenka.tweeterjam.models.Tweet;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.agoenka.tweeterjam.utils.SharedPreferencesUtils;
import org.parceler.Parcels;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;
import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_DRAFT_TWEET;
import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_IN_REPLY_TO_TWEET;
import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_IN_REPLY_TO_USER;

public class ComposeTweetFragment extends DialogFragment {

    private FragmentComposeTweetBinding binding;
    private TwitterClient client;
    private Tweet replyToTweet;
    private long replyToTweetUid;

    private static final String KEY_TEXT = "text";
    public static final String KEY_LOGGED_IN_USER = "loggedInUser";
    private static final String KEY_IN_REPLY_TO = "inReplyTo";

    private static final int MAX_TWEET_LENGTH = 140;

    public interface ComposeTweetFragmentListener {
        void onTweet(Tweet tweet);
    }

    private ComposeTweetFragmentListener listener;

    public void setListener(ComposeTweetFragmentListener listener) {
        this.listener = listener;
    }

    public static ComposeTweetFragment newInstance(String text, User loggedInUser, Tweet inReplyTo) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        if (text != null)
            args.putString(KEY_TEXT, text);
        if (loggedInUser != null)
            args.putParcelable(KEY_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        if (inReplyTo != null)
            args.putParcelable(KEY_IN_REPLY_TO, Parcels.wrap(inReplyTo));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        User user = Parcels.unwrap(getArguments().getParcelable(KEY_LOGGED_IN_USER));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose_tweet, container, false);
        binding.setHandlers(new Handlers());
        binding.setUser(user);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setTextChangeListener();
        client = TweeterJamApplication.getTwitterClient();

        String text = getArguments().getString(KEY_TEXT);
        replyToTweet = Parcels.unwrap(getArguments().getParcelable(KEY_IN_REPLY_TO));
        String draftTweet = SharedPreferencesUtils.getString(getActivity(), KEY_DRAFT_TWEET);

        if (text != null)
            handleImplicit(text);
        else if (replyToTweet != null)
            handleReplyTo();
        else if (draftTweet != null)
            handleDraft(draftTweet);
    }

    private void handleImplicit(String text) {
        setupEditTweetView(text);
        SharedPreferencesUtils.clear(getActivity(), KEY_DRAFT_TWEET, KEY_IN_REPLY_TO_USER, KEY_IN_REPLY_TO_TWEET);
    }

    private void handleReplyTo() {
        replyToTweetUid = replyToTweet.getUid();
        setupEditTweetView(String.format("%s ", replyToTweet.getUser().getScreenName()));
        setupReplyToView(replyToTweet.getUser().getName());
        SharedPreferencesUtils.clear(getActivity(), KEY_DRAFT_TWEET, KEY_IN_REPLY_TO_USER, KEY_IN_REPLY_TO_TWEET);
    }

    private void handleDraft(String draftTweet) {
        setupEditTweetView(draftTweet);

        final String inReplyToUser = SharedPreferencesUtils.getString(getActivity(), KEY_IN_REPLY_TO_USER);
        final long inReplyToTweet = SharedPreferencesUtils.getLong(getActivity(), KEY_IN_REPLY_TO_TWEET);

        if (inReplyToTweet > 0 && inReplyToUser != null) {
            setupReplyToView(inReplyToUser);
            replyToTweetUid = inReplyToTweet;
        }
    }

    private void setupEditTweetView(String text) {
        binding.etTweet.setText(text);
        binding.etTweet.setSelection(binding.etTweet.getText().length());
    }

    private void setupReplyToView(String name) {
        binding.tvInReplyTo.setText(String.format("In reply to %s", name));
        binding.tvInReplyTo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Point size = new Point();
        Window window = getDialog().getWindow();
        assert window != null;

        // Store dimensions of the screen in `size`
        window.getWindowManager().getDefaultDisplay().getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout(size.x, (int) (size.y * 0.75));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
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
                    binding.tvCharCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAlertText));
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
            if (binding.etTweet.getText().length() > 0) {
                CancelTweetDialogFragment cancelDialog = CancelTweetDialogFragment.newInstance(
                        binding.etTweet.getText().toString(),
                        replyToTweet != null ? replyToTweet.getUser().getName() : null,
                        replyToTweet != null ? replyToTweet.getUid() : replyToTweetUid);
                cancelDialog.setTargetFragment(ComposeTweetFragment.this, 100);
                cancelDialog.show(getFragmentManager(), "Cancel Tweet");
            }
            dismiss();
        }

        public void onTweet(@SuppressWarnings("unused") View view) {
            if (replyToTweet != null)
                postTweet(binding.etTweet.getText().toString(), replyToTweet.getUid());
            else
                postTweet(binding.etTweet.getText().toString(), replyToTweetUid);
        }
    }

    private void postTweet(String status, long inReplyTo) {
        if (isConnected(getContext())) {
            client.postTweet(status, inReplyTo, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("DEBUG", responseString);
                    Tweet tweet = getGson().fromJson(responseString, Tweet.class);
                    if (listener != null)
                        listener.onTweet(tweet);
                    SharedPreferencesUtils.clear(getActivity(), KEY_DRAFT_TWEET, KEY_IN_REPLY_TO_USER, KEY_IN_REPLY_TO_TWEET);
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(getContext(), "Unable to post tweet at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}