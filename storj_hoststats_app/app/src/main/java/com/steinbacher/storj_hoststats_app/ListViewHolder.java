package com.steinbacher.storj_hoststats_app;

import android.app.*;
import android.support.v7.internal.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

/**
 * Created by georg on 12.11.17.
 */

public class ListViewHolder extends Activity{
    private static ListView mListView;

    private ListViewHolder() {}

    private static class Holder {
        private static final ListViewHolder INSTANCE = new ListViewHolder();
    }

    public static ListViewHolder getInstance() {
        return Holder.INSTANCE;
    }

    public static void setListView(ListView listView) {
        mListView = listView;
    }

    public static StorjNodeAdapter getAdapter() {
        return (StorjNodeAdapter) mListView.getAdapter();
    }

    public static ListView getListView() {
        return mListView;
    }

    public void showLoadingBar(int position, final boolean visible) {
        final View parentView = mListView.getChildAt(position);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    parentView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    parentView.findViewById(R.id.responseTimeView).setVisibility(View.GONE);
                } else {
                    parentView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    parentView.findViewById(R.id.responseTimeView).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
