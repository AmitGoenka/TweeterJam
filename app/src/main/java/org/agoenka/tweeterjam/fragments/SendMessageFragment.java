package org.agoenka.tweeterjam.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
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
import org.agoenka.tweeterjam.databinding.FragmentSendMessageBinding;
import org.agoenka.tweeterjam.models.Message;
import org.agoenka.tweeterjam.network.TwitterClient;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_SCREEN_NAME;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

public class SendMessageFragment extends DialogFragment {

    private FragmentSendMessageBinding binding;
    private TwitterClient client;
    private SendMessageFragmentListener listener;

    public interface SendMessageFragmentListener {
        void onMessage(Message message);
    }

    public void setListener(SendMessageFragmentListener listener) {
        this.listener = listener;
    }

    public static SendMessageFragment newInstance(String screenName) {
        SendMessageFragment fragment = new SendMessageFragment();
        Bundle args = new Bundle();
        if (screenName != null)
            args.putString(KEY_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_message, container, false);
        client = TweeterJamApplication.getTwitterClient();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String screenName = getArguments().getString(KEY_SCREEN_NAME);

        if (!TextUtils.isEmpty(screenName)) {
            binding.etUserName.setText(String.format("%s ", screenName));
            binding.etUserName.setSelection(binding.etUserName.getText().length());
        }

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSend.setOnClickListener(v -> sendMessage(binding.etUserName.getText().toString(), binding.etMessage.getText().toString()));
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

    private void sendMessage(String screenName, String text) {
        if (isConnected(getContext())) {
            client.postDirectMessage(screenName, text, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("DEBUG", responseString);
                    Message message = getGson().fromJson(responseString, Message.class);
                    if(message != null) {
                        if (listener != null) listener.onMessage(message);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Message not sent.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    Toast.makeText(getContext(), "Unable to send message at this moment.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}