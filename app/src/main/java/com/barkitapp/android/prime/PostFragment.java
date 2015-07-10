package com.barkitapp.android.prime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barkitapp.android.Messages.InitialPostsReceivedEvent;
import com.barkitapp.android.Messages.MasterListUpdatedEvent;
import com.barkitapp.android.Messages.UpdateListItemEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.core.objects.Coordinates;
import com.barkitapp.android.core.services.LocationService;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.functions.UpdatePosts;
import com.barkitapp.android.parse.objects.Post;
import com.parse.ParseGeoPoint;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public abstract class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UpdatePosts.OnUpdatePostsCompleted {

    public PostRecyclerViewAdapter mAdapter;
    SwipeRefreshLayout mSwipeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipeLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.main_bark_list_fragment, container, false);

        mSwipeLayout.setColorSchemeResources(
                R.color.accent_dark,
                R.color.accent,
                R.color.primary);

        mSwipeLayout.setOnRefreshListener(this);

        setupRecyclerView((RecyclerView) mSwipeLayout.findViewById(R.id.recyclerview));
        return mSwipeLayout;
    }

    private boolean loading = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;


    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(getActivity(), getList(), getOrder());
        recyclerView.setAdapter(mAdapter = adapter);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (!loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        onRefresh();
                    }
                }
            }
        });
    }

    public void addItem(Post item) {
        if(mAdapter != null)
            mAdapter.getValues().add(0, item);

        NotifyAdapter();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void UpdateList() {
        mAdapter.getValues().clear();
        mAdapter.setValues(getList());

        NotifyAdapter();

        setRefreshing(false);
        loading = false;
    }

    public void onEvent(UpdateListItemEvent event) {

        // Update ListItem in all other tabs but the one it was changed
        if(!event.order.equals(getOrder())) {
            if (mAdapter.getValues().contains(event.post)) {
                int index = mAdapter.getValues().indexOf(event.post);
                mAdapter.getValues().set(index, event.post);
            }

            NotifyAdapter();
        }
    }

    public void onEvent(InitialPostsReceivedEvent event) {
        UpdateList();
    }

    public void onEvent(MasterListUpdatedEvent event) {
        UpdateList();
    }

    @UiThread
    public void NotifyAdapter() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public List<Post> getList() {
        List<Post> list = MasterList.GetMasterList();
        sort(list);

        return list;
    }

    public abstract void sort(List<Post> masterList);

    @Override
    public void onRefresh() {

        Coordinates location = LocationService.getLocation(getActivity());

        UpdatePosts.run(this,
                UserId.get(getActivity()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                Constants.DEFAULT_RADIUS,
                Constants.GET_POSTS_COUNT,
                getOrder(),
                false);

        setRefreshing(true);
    }

    @UiThread
    public void setRefreshing(boolean status) {
        if(mSwipeLayout != null)
            mSwipeLayout.setRefreshing(status);
    }

    public abstract Order getOrder();

    public void onUpdatePostsCompleted(HashMap<String, Object> result) {
        MasterList.StoreMasterList(getActivity(), result);
        UpdateList();
    }

    public void onUpdatePostsFailed(String error) {
        setRefreshing(false);
    }
}
