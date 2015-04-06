package com.josepablorocha.ribbit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.josepablorocha.ribbit.R;
import com.josepablorocha.ribbit.adapters.MessageAdapter;
import com.josepablorocha.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Pablo on 3/30/2015.
 */
public class InboxFragment extends ListFragment {

    public static final String TAG = InboxFragment.class.getSimpleName();

    protected ProgressBar mProgressBar;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_refresh_1,
                R.color.swipe_refresh_2,
                R.color.swipe_refresh_3,
                R.color.swipe_refresh_4);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar = (ProgressBar)getView().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        retrieveMessages();
    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.orderByAscending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                if(mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                if (e == null) {
                    mMessages = messages;

                    String[] userNames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        userNames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(
                                getListView().getContext(),
                                mMessages);
                        setListAdapter(adapter);
                    }
                    else {
                        // refill adapter
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // Delete it!
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if (ids.size() == 1) {
            // Delete the whole thing
            message.deleteInBackground();
            return;
        }
        else {
            // Remove the recipient locally
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            // Remove the recipient on the back-end
            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };
}