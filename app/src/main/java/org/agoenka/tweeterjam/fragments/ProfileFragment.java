package org.agoenka.tweeterjam.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.TweeterJamApplication;
import org.agoenka.tweeterjam.databinding.FragmentProfileBinding;
import org.agoenka.tweeterjam.models.User;
import org.agoenka.tweeterjam.network.TwitterClient;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static org.agoenka.tweeterjam.utils.AppUtils.FOLLOWERS;
import static org.agoenka.tweeterjam.utils.AppUtils.FOLLOWING;
import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;
import static org.agoenka.tweeterjam.utils.ConnectivityUtils.isConnected;
import static org.agoenka.tweeterjam.utils.GsonUtils.getGson;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TwitterClient client;
    private User mUser;
    private OnGetUsersListListener getUsersListListener;

    public interface OnGetUsersListListener {
        void onGetUsersList(User user, String mode);
    }

    public ProfileFragment setOnGetUsersListListener(OnGetUsersListListener listener) {
        this.getUsersListListener = listener;
        return this;
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, Parcels.wrap(user));
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TweeterJamApplication.getTwitterClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = Parcels.unwrap(getArguments().getParcelable(KEY_USER));
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.setHandlers(new Handlers());
        binding.setUser(mUser);
        return binding.getRoot();
    }

    public class Handlers {
        public void onFollowers(@SuppressWarnings("unused") View view) {
            getUsersListListener.onGetUsersList(mUser, FOLLOWERS);
        }

        public void onFollowing(@SuppressWarnings("unused") View view) {
            getUsersListListener.onGetUsersList(mUser, FOLLOWING);
        }

        public void onFollow(@SuppressWarnings("unused") View view) {
            if(mUser.isFollowing() || mUser.isFollowRequestSent())
                unfriend(mUser);
            else
                friend(mUser);
        }
    }

    private void friend(final User user) {
        if(isConnected(getContext())) {
            client.createFriend(user.getScreenName(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateUser(responseString, false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to follow the user at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unfriend(final User user) {
        if(isConnected(getContext())) {
            client.removeFriend(user.getScreenName(), new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    updateUser(responseString, true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(responseString, throwable);
                    Toast.makeText(getContext(), "Unable to unfriend the user at this moment.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUser(String json, boolean unfriend) {
        Log.d("DEBUG", json);
        User user = getGson().fromJson(json, User.class);
        if (user != null) {
            if (unfriend) {
                user.setFollowing(false);
            }
            mUser = user;
            binding.setUser(mUser);
            binding.notifyPropertyChanged(binding.ibFollowing.getId());
        }
    }

    private void handleFailure(String response, Throwable t) {
        Log.d("DEBUG", response);
        Log.d("DEBUG", t.getLocalizedMessage());
    }
}