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
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.parse.converter.ReplyConverter;
import com.barkitapp.android.parse.functions.UpdateReplies;
import com.barkitapp.android.parse.objects.Reply;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarkReplyListFragment extends Fragment implements UpdateReplies.OnUpdateRepliesCompleted {

    private ReplyRecyclerViewAdapter mAdapter;
    private List<Reply> mValues;

    public void addNewItem(Reply item) {
        mValues.add(0, item);

        NotifyAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_reply_list, container, false);
        setupRecyclerView(rv);

        Coordinates loc = LocationService.getLocation(getActivity());

        UpdateReplies.run(this,
                "kHoG2ihhvD",
                "AAbvD5hbPF",
                new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));

        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mValues = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ReplyRecyclerViewAdapter adapter = new ReplyRecyclerViewAdapter(getActivity(), mValues);

        recyclerView.setAdapter(mAdapter = adapter);
    }

    @Override
    public void onUpdateRepliesCompleted(HashMap<String, Object> result) {
        mAdapter.getValues().clear();
        mAdapter.setValues(ReplyConverter.run(getActivity(), result));

        //mSwipeLayout.setRefreshing(false);

        NotifyAdapter();
    }

    @Override
    public void onUpdateRepliesFailed(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @UiThread
    public void NotifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}
