package com.barkitapp.android._main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barkitapp.android._core.utility.LastRefresh;
import com.barkitapp.android.events.MasterListUpdated;
import com.barkitapp.android.events.RequestUpdatePostsEvent;
import com.barkitapp.android.events.UpdateListItemEvent;
import com.barkitapp.android.R;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.Constants;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.my_stuff.MyBarksFragment;
import com.barkitapp.android.my_stuff.MyRepliesFragment;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.functions.UpdatePosts;
import com.barkitapp.android.parse_backend.functions.UpdatePostsLat;
import com.barkitapp.android.parse_backend.objects.Post;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
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
                R.layout.fragment_list_bark, container, false);

        mSwipeLayout.setColorSchemeResources(
                R.color.accent_dark,
                R.color.accent,
                R.color.primary);

        mSwipeLayout.setOnRefreshListener(this);

        // todo remove me when fix is here
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        24,
                        getResources().getDisplayMetrics()));

        //setRefreshing(true);

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
                    // Half of the list reached
                    if (dy > 0 && totalItemCount >= 20 && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        // todo show loading bark icon and start loading
                        LoadMoreBarks();
                    }
                }
            }
        });

        // set empty view
        //recyclerView.setEmptyView(getActivity().getLayoutInflater().inflate(R.layout.empty_info, null));
    }

    private void LoadMoreBarks() {
        Coordinates location = LocationService.getLocation(getActivity());

        if(location == null) {
            setRefreshing(false);
            //Toast.makeText(getActivity(), "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
            return;
        }

        setRefreshing(true);

        UpdatePostsLat.run(getActivity(),
                this,
                UserId.get(getActivity()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                Constants.GET_POSTS_COUNT,
                getOrder(),
                false);
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

        if(this instanceof MyBarksFragment || this instanceof MyRepliesFragment) {
            if(MasterList.GetMasterListPost(getOrder()).isEmpty())
                onRefresh();
        }

        if(InternalAppData.getBoolean(getActivity(), SharedPrefKeys.HAS_SET_MANUAL_LOCATION)) {
            Coordinates location = LocationService.getLocation(getActivity());

            if(location == null) {
                setRefreshing(false);
                //Toast.makeText(getActivity(), "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
                return;
            }

            MasterList.clearMasterListAllSlow();
            UpdateList();

            setRefreshing(true);

            UpdatePostsLat.run(getActivity(),
                    this,
                    UserId.get(getActivity()),
                    new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                    new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                    Constants.GET_POSTS_COUNT,
                    getOrder(),
                    true);
        }
        else {
            if(MasterList.GetMasterListPost(getOrder()).isEmpty())
                onRefresh();
        }

//        loading = false;
//
//        if(!Connectivity.isOnline(getActivity())) {
//            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_LONG).show();
//        }
//
//        if(LastRefresh.isAvailable(getActivity()) || MasterList.GetMasterList(getOrder()).isEmpty())
//        {
//            if(MasterList.GetMasterList(getOrder()).isEmpty()) {
//                UpdateList();
//            }
//
//            if(mSwipeLayout != null)
//                mSwipeLayout.setRefreshing(true);
//
//            onRefresh();
//        }
    }

    public void UpdateList() {
        int initial_barks = mAdapter.getItemCount();
        mAdapter.getValues().clear();
        //mAdapter.updateVotes();
        mAdapter.setValues(getList());
        int after_barks = mAdapter.getItemCount();

        // if no new posts allow reloading after some time
        if(initial_barks != after_barks)
            loading = false;

        NotifyAdapter();

        setRefreshing(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading = false;
            }
        }, 15000);
    }

    public void onEvent(UpdateListItemEvent event) {

        // Update ListItem in all other tabs but the one it was changed
        if(!event.order.equals(getOrder())) {
            if (mAdapter.getValues().contains(event.post)) {
                int index = mAdapter.getValues().indexOf(event.post);
                mAdapter.getValues().set(index, event.post);
            }

            setRefreshing(false);

            NotifyAdapter();
        }
    }

    public void onEvent(MasterListUpdated event) {

        if(event.getOrder().equals(Order.TIME)) {
            if(this instanceof NewFragment) {
                UpdateList();
            }
        }

        if(event.getOrder().equals(Order.UP_VOTES)) {
            if(this instanceof HotFragment) {
                UpdateList();
            }
        }

        if(event.getOrder().equals(Order.MY_BARKS)) {
            if(this instanceof MyBarksFragment) {
                UpdateList();
            }
        }

        if(event.getOrder().equals(Order.MY_REPLIES)) {
            if(this instanceof MyRepliesFragment) {
                UpdateList();
            }
        }

        if(MasterList.GetMasterListPost(getOrder()).isEmpty())
            onRefresh();
    }

    @UiThread
    public void NotifyAdapter() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public List<Post> getList() {
        List<Post> list = MasterList.GetMasterListPost(getOrder());
        sort(list);

        return list;
    }

    public abstract void sort(List<Post> masterList);

    public void onEvent(RequestUpdatePostsEvent event) {
        Coordinates location = LocationService.getLocation(getActivity());

        if(location == null) {
            setRefreshing(false);
            //Toast.makeText(getActivity(), "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
            return;
        }

        MasterList.clearMasterList(getOrder());
        UpdateList();

        setRefreshing(true);

        UpdatePostsLat.run(getActivity(),
                this,
                UserId.get(getActivity()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                Constants.GET_POSTS_COUNT,
                getOrder(),
                true);
    }

    @Override
    public void onRefresh() {

//        if(getList().isEmpty()) {
//            LoadMoreBarks();
//            return;
//        }

        Coordinates location = LocationService.getLocation(getActivity());

        if(location == null) {
            setRefreshing(false);
            //Toast.makeText(getActivity(), "No GPS data. Please enable GPS.", Toast.LENGTH_LONG).show();
            return;
        }

        MasterList.clearMasterList(getOrder());
        UpdateList();

        setRefreshing(true);

        UpdatePostsLat.run(getActivity(),
                this,
                UserId.get(getActivity()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                new ParseGeoPoint(location.getLatitude(), location.getLongitude()),
                Constants.GET_POSTS_COUNT,
                getOrder(),
                true);
    }

    @UiThread
    public void setRefreshingUi(boolean status) {
        if(mSwipeLayout != null)
            mSwipeLayout.setRefreshing(status);
    }

    public void setRefreshing(boolean status) {
        setRefreshingUi(status);

        if(status)
            new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshingUi(false);
            }
        }, 5000);
    }

    public abstract Order getOrder();

    public void onUpdatePostsCompleted(HashMap<String, ArrayList<ParseObject>> result) {
        MasterList.StoreMasterList(getActivity(), result, getOrder());
        //UpdateList();
    }

    public void onUpdatePostsFailed(String error) {
        //Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        setRefreshing(false);
    }
}