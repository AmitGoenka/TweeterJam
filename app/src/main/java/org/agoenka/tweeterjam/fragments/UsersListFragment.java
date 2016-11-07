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

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.adapters.EndlessRecyclerViewScrollListener;
import org.agoenka.tweeterjam.adapters.UsersAdapter;
import org.agoenka.tweeterjam.databinding.FragmentUsersListBinding;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.models.Users;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.network.TwitterClient.PAGE_SIZE;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_FOLLOWERS;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_FOLLOWING;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_MODE;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
import static org.agoenka.tweeterjam.utils.AppUtils.isEmpty;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */

public class UsersListFragment extends Fragment {

    private UsersAdapter mAdapter;
    private User mUser;
    private TwitterClient client;
    private String mode;
    private long nextCursor = -1;
    private OnProfileSelectedListener mProfileListener;

    public interface OnProfileSelectedListener {
        void onProfileSelected(User user);
    }

    public UsersListFragment setProfileListener(OnProfileSelectedListener profileListener) {
        this.mProfileListener = profileListener;
        return this;
    }

    public static UsersListFragment newInstance(User user, String mode) {
        UsersListFragment usersListFragment = new UsersListFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, Parcels.wrap(user));
        args.putString(KEY_MODE, mode);
        usersListFragment.setArguments(args);
        return usersListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new UsersAdapter(getActivity(), new ArrayList<>());
        client = TweeterJamApplication.getTwitterClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = Parcels.unwrap(getArguments().getParcelable(KEY_USER));
        mode = getArguments().getString(KEY_MODE);

        FragmentUsersListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false);
        binding.rvUsers.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvUsers.setLayoutManager(layoutManager);

        mAdapter.setProfileListener(mProfileListener);
        mAdapter.setFollowListener((user, position) -> {
            if (position != RecyclerView.NO_POSITION) {
                if(user.isFollowing() || user.isFollowRequestSent())
                    unfriend(user, position);
                else
                    friend(user, position);
            }
        });

        binding.rvUsers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount, RecyclerView view) {
                if (nextCursor > 0)
                    loadUsers();
            }
        });

        loadUsers();

        return binding.getRoot();
    }

    private void loadUsers() {
        switch (mode) {
            case KEY_FOLLOWERS:
                getFollowers();
                break;
            case KEY_FOLLOWING:
                getFriends();
                break;
        }
    }

    private void loadUsers(String json) {
        Log.d("DEBUG", json);
        Users users = getGson().fromJson(json, Users.class);
        if (users != null && !isEmpty(users.getUsers())) {
            nextCursor = users.getNextCursor();
            mAdapter.addAll(users.getUsers());
        }
    }

    private void updateUser(String json, int position, boolean unfriend) {
        Log.d("DEBUG", json);
        User updatedUser = getGson().fromJson(json, User.class);
        if (updatedUser != null) {
            if (unfriend) {
                updatedUser.setFollowing(false);
            }
            mAdapter.update(updatedUser, position);
        }
    }

    private void handleFailure(String response, Throwable t) {
        Log.d("DEBUG", response);
        Log.d("DEBUG", t.getLocalizedMessage());
    }

    private void getFollowers() {
        if (isConnected(getContext())) {
            client.getFollowers(mUser.getScreenName(), nextCursor, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    loadUsers(responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to retrieve followers at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getFriends() {
        if (isConnected(getContext())) {
            client.getFriends(mUser.getScreenName(), nextCursor, PAGE_SIZE, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    loadUsers(responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to retrieve friends at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void friend(final User user, final int position) {
        if(isConnected(getContext())) {
            client.createFriend(user.getScreenName(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateUser(responseString, position, false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to follow the user at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unfriend(final User user, final int position) {
        if(isConnected(getContext())) {
            client.removeFriend(user.getScreenName(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateUser(responseString, position, true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unfriend the user at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}