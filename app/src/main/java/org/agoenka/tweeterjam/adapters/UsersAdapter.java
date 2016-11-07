package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ItemUserBinding;
import org.agoenka.tweeterjam.models.User;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private final List<User> mUsers;
    private final Context mContext;
    private View.OnClickListener mProfileListener;
    private View.OnClickListener mFollowListener;

    public UsersAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    public void setProfileListener(View.OnClickListener profileListener) {
        this.mProfileListener = profileListener;
    }

    public void setFollowListener(View.OnClickListener followListener) {
        this.mFollowListener = followListener;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.binding.setUser(user);
        holder.binding.executePendingBindings();

        holder.binding.ivProfileImage.setTag(user);
        holder.binding.ivProfileImage.setOnClickListener(mProfileListener);

        holder.binding.ibFollowing.setTag(user);
        holder.binding.ibFollowing.setOnClickListener(mFollowListener);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.binding.ivProfileImage);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemUserBinding binding;
        ViewHolder(View itemView) {
            super(itemView);
            binding = ItemUserBinding.bind(itemView);
        }
    }

    public void addAll(List<User> users) {
        int currentSize = getItemCount();
        mUsers.addAll(users);
        notifyItemRangeInserted(currentSize, mUsers.size() - currentSize);
    }
}