package org.agoenka.tweeterjam.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.FragmentProfileBinding;
import org.agoenka.tweeterjam.models.User;
import org.parceler.Parcels;

import static org.agoenka.tweeterjam.utils.AppUtils.KEY_USER;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */
public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        User user = Parcels.unwrap(getArguments().getParcelable(KEY_USER));
        FragmentProfileBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.setUser(user);
        return binding.getRoot();
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER, Parcels.wrap(user));
        profileFragment.setArguments(args);
        return profileFragment;
    }
}