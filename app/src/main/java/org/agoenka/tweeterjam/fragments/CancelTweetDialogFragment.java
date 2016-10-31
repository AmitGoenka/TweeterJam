package org.agoenka.tweeterjam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import org.agoenka.tweeterjam.utils.SharedPreferencesUtils;

import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_DRAFT_TWEET;
import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_IN_REPLY_TO_TWEET;
import static org.agoenka.tweeterjam.utils.SharedPreferencesUtils.KEY_IN_REPLY_TO_USER;

/**
 * Author: agoenka
 * Created At: 10/1/2016
 * Version: ${VERSION}
 */

public class CancelTweetDialogFragment extends DialogFragment {

    public static CancelTweetDialogFragment newInstance(String draftTweet, String inReplyToUser, long inReplyToTweet) {
        CancelTweetDialogFragment fragment = new CancelTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_DRAFT_TWEET, draftTweet);
        if (inReplyToUser != null)
            args.putString(KEY_IN_REPLY_TO_USER, inReplyToUser);
        if (inReplyToTweet > 0)
            args.putLong(KEY_IN_REPLY_TO_TWEET, inReplyToTweet);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String draftTweet = getArguments().getString(KEY_DRAFT_TWEET);
        final String inReplyToUser = getArguments().getString(KEY_IN_REPLY_TO_USER);
        final long inReplyToTweet = getArguments().getLong(KEY_IN_REPLY_TO_TWEET);
        return new AlertDialog.Builder(getTargetFragment().getContext())
                .setMessage("Would you like to save this tweet as a draft?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferencesUtils.putString(getActivity(), KEY_DRAFT_TWEET, draftTweet);
                    if (inReplyToUser != null)
                        SharedPreferencesUtils.putString(getActivity(), KEY_IN_REPLY_TO_USER, inReplyToUser);
                    if (inReplyToTweet > 0)
                        SharedPreferencesUtils.putLong(getActivity(), KEY_IN_REPLY_TO_TWEET, inReplyToTweet);
                    Toast.makeText(getActivity(), "Draft tweet saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    SharedPreferencesUtils.clear(getActivity(), KEY_DRAFT_TWEET, KEY_IN_REPLY_TO_USER, KEY_IN_REPLY_TO_TWEET);
                    dialog.cancel();
                })
                .create();
    }

}