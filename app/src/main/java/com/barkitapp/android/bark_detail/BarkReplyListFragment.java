package com.barkitapp.android.bark_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.barkitapp.android.Messages.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.parse.converter.ReplyConverter;
import com.barkitapp.android.parse.functions.UpdateReplies;
import com.barkitapp.android.parse.objects.Reply;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class BarkReplyListFragment extends Fragment implements UpdateReplies.OnUpdateRepliesCompleted {

    private ReplyRecyclerViewAdapter mAdapter;
    private ProgressBar loadingBar;

    public void addNewItem(Reply item) {
        mAdapter.getValues().add(0, item);

        NotifyAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout fragmentView = (RelativeLayout) inflater.inflate(
                R.layout.fragment_reply_list, container, false);

        setupRecyclerView((RecyclerView) fragmentView.findViewById(R.id.recyclerview));

        loadingBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar1);

        getRepliesFromParse();

        return fragmentView;
    }

    public void onEvent(RequestUpdateRepliesEvent event) {
        getRepliesFromParse();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void getRepliesFromParse() {

        mAdapter.getValues().clear();
        NotifyAdapter();

        startLoadingBar();

        Coordinates loc = LocationService.getLocation(getActivity());

        BarkDetailActivity activity = (BarkDetailActivity) getActivity();

        if(activity == null || activity.ObjectId == null) {
            return;
        }

        UpdateReplies.run(this,
                Constants.TEMP_USER_ID,
                activity.ObjectId,
                new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<Reply> mValues = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ReplyRecyclerViewAdapter adapter = new ReplyRecyclerViewAdapter(getActivity(), mValues);

        recyclerView.setAdapter(mAdapter = adapter);
    }

    @Override
    public void onUpdateRepliesCompleted(HashMap<String, Object> result) {
        mAdapter.getValues().clear();
        mAdapter.setValues(ReplyConverter.run(getActivity(), result));

        stopLoadingBar();

        NotifyAdapter();
    }

    @Override
    public void onUpdateRepliesFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        stopLoadingBar();
    }

    @UiThread
    public void NotifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @UiThread
    private void stopLoadingBar() {
        loadingBar.setVisibility(View.GONE);
    }

    @UiThread
    private void startLoadingBar() {
        loadingBar.setVisibility(View.VISIBLE);
    }
}
