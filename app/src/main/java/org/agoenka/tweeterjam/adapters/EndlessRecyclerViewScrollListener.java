package org.agoenka.tweeterjam.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Author: agoenka
 * Created At: 10/22/2016
 * Version: ${VERSION}
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // Minimum number of items to have below current scroll position before loading more.
    private int visibleThreshold = 5;
    // Current offset index of data loaded
    private int currentPage = 0;
    // True if we are still waiting for the last dataset to load.
    private boolean loading = true;
    // Total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // RecyclerView OnScrollListener needs the layout manager for current scroll position information
    private final RecyclerView.LayoutManager mLayoutManager;

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemCount, RecyclerView view);

    protected EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @SuppressWarnings("unused")
    protected EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    @SuppressWarnings("unused")
    protected EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    // This happens many times a second during a scroll, so the code in here needs to be light.
    // A few useful parameters are available to help work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = getLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();

        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + visibleThreshold >= totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    // Call this method whenever performing new searches
    @SuppressWarnings("unused")
    public void resetState() {
        this.currentPage = 0;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    private int getLastVisibleItemPosition() {
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            return getLastVisibleItem(lastVisibleItemPositions); // get maximum element within the list
        } else if (mLayoutManager instanceof GridLayoutManager) {
            return  ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            return  ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else {
            return 0;
        }
    }

    private static int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

}