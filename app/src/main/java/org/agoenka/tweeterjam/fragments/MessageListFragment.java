package org.agoenka.tweeterjam.fragments;

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

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.MessagesAdapter;
import org.agoenka.tweeterjam.databinding.FragmentMessagesListBinding;
import org.agoenka.tweeterjam.models.Message;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/7/2016
 * Version: ${VERSION}
 */
public class MessageListFragment extends Fragment {

    private List<Message> mMessages;
    private MessagesAdapter mAdapter;
    private TwitterClient client;
    private MessageListFragment.OnProfileSelectedListener mProfileListener;
    private MessageListFragment.OnLoadingListener mLoadingListener;
    private OnMessageSendListener mMessageSendListener;
    private long currMinId = 0;

    public interface OnMessageSendListener {
        void onSend(User user);
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected(User user);
    }

    public interface OnLoadingListener {
        void onLoad(boolean loading);
    }

    public MessageListFragment setProfileListener(MessageListFragment.OnProfileSelectedListener profileListener) {
        this.mProfileListener = profileListener;
        return this;
    }

    public MessageListFragment setOnLoadingListener(MessageListFragment.OnLoadingListener loadingListener) {
        this.mLoadingListener = loadingListener;
        return this;
    }

    public MessageListFragment setMessageSendListener(OnMessageSendListener messageSendListener) {
        this.mMessageSendListener = messageSendListener;
        return this;
    }

    public static MessageListFragment newInstance() {
        MessageListFragment messageListFragment = new MessageListFragment();
        messageListFragment.setArguments(new Bundle());
        return messageListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessages = new ArrayList<>();
        mAdapter = new MessagesAdapter(getActivity(), mMessages);
        client = TweeterJamApplication.getTwitterClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMessagesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages_list, container, false);
        binding.rvMessages.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvMessages.setLayoutManager(layoutManager);

        mAdapter.setProfileListener(mProfileListener);
        mAdapter.setMessageListener((user, position) -> {
            if (position != RecyclerView.NO_POSITION && mMessageSendListener != null) {
                mMessageSendListener.onSend(user);
            }
        });

        binding.rvMessages.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {
                if (mLoadingListener != null) mLoadingListener.onLoad(true);
                currMinId = Message.getMinId(mMessages);
                getDirectMessages(currMinId > 0 ? currMinId - 1 : 0);
            }
        });

        getDirectMessages(0);

        return binding.getRoot();
    }

    private void getDirectMessages(final long maxId) {
        if (isConnected(getContext())) {
            client.getDirectMessages(maxId, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String json) {
                    Log.d("DEBUG", json);
                    List<Message> messages = getGson().fromJson(json, new TypeToken<List<Message>>() {
                    }.getType());
                    mAdapter.addAll(messages);
                    if (mLoadingListener != null) mLoadingListener.onLoad(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("DEBUG", responseString);
                    Log.d("DEBUG", throwable.getLocalizedMessage());
                    if (mLoadingListener != null) mLoadingListener.onLoad(false);
                    Toast.makeText(getContext(), "Unable to get messages at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void add(int index, Message message) {
        mAdapter.add(index, message);
    }
}