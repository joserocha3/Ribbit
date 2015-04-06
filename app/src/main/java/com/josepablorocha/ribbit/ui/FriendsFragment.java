package com.josepablorocha.ribbit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.josepablorocha.ribbit.R;
import com.josepablorocha.ribbit.adapters.UserAdapter;
import com.josepablorocha.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Pablo on 3/30/2015.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ProgressBar mProgressBar;
    protected GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
        mGridView.setEmptyView(emptyTextView);

        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null) {
                    mFriends = friends;
                    String[] userNames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        userNames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
//                    builder.setMessage(e.getMessage())
//                            .setTitle(R.string.error_title)
//                            .setPositiveButton(android.R.string.ok, null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
                }
            }
        });
    }
}
