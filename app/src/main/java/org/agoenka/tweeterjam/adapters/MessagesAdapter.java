package org.agoenka.tweeterjam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.agoenka.tweeterjam.R;
import org.agoenka.tweeterjam.databinding.ItemMessageBinding;
import org.agoenka.tweeterjam.fragments.MessageListFragment;
import org.agoenka.tweeterjam.models.Message;
import org.agoenka.tweeterjam.models.User;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 11/6/2016
 * Version: ${VERSION}
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private final List<Message> mMessages;
    private final Context mContext;
    private MessageListFragment.OnProfileSelectedListener mProfileListener;
    private OnMessageListener mMessageListener;

    public interface OnMessageListener {
        void onMessage(User user, int position);
    }

    public void setProfileListener(MessageListFragment.OnProfileSelectedListener profileListener) {
        this.mProfileListener = profileListener;
    }

    public void setMessageListener(OnMessageListener messageListener) {
        this.mMessageListener = messageListener;
    }

    public MessagesAdapter(Context context, List<Message> messages) {
        mContext = context;
        mMessages = messages;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.binding.setMessage(message);
        holder.binding.executePendingBindings();

        holder.binding.ivProfileImage.setTag(message.getSender());
        holder.binding.ivProfileImage.setOnClickListener(v -> mProfileListener.onProfileSelected((User) v.getTag()));

        holder.binding.ibSendMessage.setTag(message.getSender());
        holder.binding.ibSendMessage.setOnClickListener(v -> mMessageListener.onMessage((User) v.getTag(), holder.getAdapterPosition()));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.binding.ivProfileImage);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemMessageBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = ItemMessageBinding.bind(itemView);
        }
    }

    public void addAll(List<Message> messages) {
        int currentSize = getItemCount();
        mMessages.addAll(messages);
        notifyItemRangeInserted(currentSize, mMessages.size() - currentSize);
    }

    public void add(int index, Message message) {
        mMessages.add(index, message);
        notifyItemRangeInserted(index, 1);
    }
}