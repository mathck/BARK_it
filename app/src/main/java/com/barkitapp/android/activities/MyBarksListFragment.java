package com.barkitapp.android.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
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

import com.barkitapp.android._main.PostRecyclerViewAdapter;
import com.barkitapp.android.events.RequestUpdateRepliesEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.functions.UpdateReplies;
import com.barkitapp.android.parse_backend.objects.Post;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MyBarksListFragment extends Fragment implements UpdateReplies.OnUpdateRepliesCompleted {

    private PostRecyclerViewAdapter mAdapter;
    private ImageView loadingBar;
    private TextView noRepliesText;
    private RecyclerView mRecyclerView;

    public void addNewItem(Post item) {
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

        // todo run parse operation
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        List<Post> mValues = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(getActivity(), mValues, Order.TIME);

        recyclerView.setAdapter(mAdapter = adapter);
    }

    @Override
    public void onUpdateRepliesCompleted(HashMap<String, ArrayList<ParseObject>> result) {
        mAdapter.getValues().clear();
        //mAdapter.setValues(ReplyConverter.run(getActivity(), result)); todo

        stopLoadingBar();

        if(mAdapter.getItemCount() == 0)
            noRepliesText.setVisibility(View.VISIBLE);
        else
            noRepliesText.setVisibility(View.GONE);

        NotifyAdapter();
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
