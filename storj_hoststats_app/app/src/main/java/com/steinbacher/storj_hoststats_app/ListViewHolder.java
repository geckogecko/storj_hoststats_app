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
    private static String mCurrentLoadingNode;

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

    public void showLoadingBar(final String nodeID, final boolean visible) {
        final View nodeView = getNodeView(nodeID);

        if(nodeView != null) {
            runOnUIThread(visible, nodeView, nodeID);
        }
    }

    private void runOnUIThread(final boolean visible, final View nodeView, final String nodeID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    mCurrentLoadingNode = nodeID;
                    nodeView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    nodeView.findViewById(R.id.responseTimeView).setVisibility(View.GONE);
                } else {
                    mCurrentLoadingNode = null;
                    nodeView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    nodeView.findViewById(R.id.responseTimeView).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static String getCurrentLoadingNode() {
        return mCurrentLoadingNode;
    }

    private static View getNodeView(String nodeID) {
        StorjNodeAdapter adapter = (StorjNodeAdapter) mListView.getAdapter();
        for(int i=0; i<adapter.getCount(); i++) {
            String tempNodeID = adapter.getItem(i).getNodeID().getValue();
            if(tempNodeID.equals(nodeID)) {
                View nodeView = mListView.getChildAt(i);
                return nodeView;
            }
        }

        return null;
    }
}
