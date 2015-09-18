package com.barkitapp.android.bark_detail;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.Messages.CollapseLayoutEvent;
import com.barkitapp.android.Messages.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.UserId;
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
    private ImageView loadingBar;
    private TextView noRepliesText;
    private RecyclerView mRecyclerView;

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

        loadingBar = (ImageView) fragmentView.findViewById(R.id.progressBar1);

        noRepliesText = (TextView) fragmentView.findViewById(R.id.no_replies_text);

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

        noRepliesText.setVisibility(View.GONE);
        startLoadingBar();

        Coordinates loc = LocationService.getLocation(getActivity());

        BarkDetailActivity activity = (BarkDetailActivity) getActivity();

        if(activity == null || activity.mPostObjectId == null) {
            return;
        }

        UpdateReplies.run(getActivity(), this,
                UserId.get(getActivity()),
                activity.mPostObjectId,
                new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        List<Reply> mValues = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ReplyRecyclerViewAdapter adapter = new ReplyRecyclerViewAdapter((BarkDetailActivity) getActivity(), mValues);

        recyclerView.setAdapter(mAdapter = adapter);
    }

    @Override
    public void onUpdateRepliesCompleted(HashMap<String, Object> result) {
        mAdapter.getValues().clear();
        mAdapter.setValues(ReplyConverter.run(getActivity(), result));

        stopLoadingBar();

        if(mAdapter.getItemCount() == 0)
            noRepliesText.setVisibility(View.VISIBLE);
        else
            noRepliesText.setVisibility(View.GONE);

        NotifyAdapter();

        // scroll to bottom of list
        if(mRecyclerView != null && mAdapter.getItemCount() > 0) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);
            EventBus.getDefault().post(new CollapseLayoutEvent());
        }
    }

    @Override
    public void onUpdateRepliesFailed(String error) {
        stopLoadingBar();
    }

    @UiThread
    public void NotifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @UiThread
    private void stopLoadingBar() {
        loadingBar.clearAnimation();
        loadingBar.setVisibility(View.GONE);
    }

    @UiThread
    private void startLoadingBar() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        loadingBar.startAnimation(animation);
        loadingBar.setVisibility(View.VISIBLE);
    }
}
