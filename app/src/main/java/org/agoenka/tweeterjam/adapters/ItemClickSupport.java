package org.agoenka.tweeterjam.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.agoenka.tweeterjam.R;

/**
  Sources:
      1. http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
      2. https://gist.github.com/nesquena/231e356f372f214c4fe6

  Usage:

      ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
          @Override
          public void onItemClicked(RecyclerView recyclerView, int position, View v) {
              // do it
          }
      });
*/

/**
 * Author: agoenka
 * Created At: 10/23/2016
 * Version: ${VERSION}
 */
public class ItemClickSupport {
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private final View.OnClickListener mOnClickListener = getViewOnClickListener();
    private final View.OnLongClickListener mOnLongClickListener = getViewOnLongClickListener();
    private final RecyclerView.OnChildAttachStateChangeListener mAttachListener = getOnChildAttachStateChangeListener();

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    @SuppressWarnings("unused")
    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }


    public interface OnItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    interface OnItemLongClickListener {
        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    @SuppressWarnings("unused")
    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private View.OnClickListener getViewOnClickListener() {
        return v -> {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        };
    }

    private View.OnLongClickListener getViewOnLongClickListener() {
        return v -> {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        };
    }

    private RecyclerView.OnChildAttachStateChangeListener getOnChildAttachStateChangeListener() {
        return new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
            }
        };
    }
}