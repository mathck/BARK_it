package com.barkitapp.android.places;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.objects.FeaturedLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mPlaces = new ArrayList<>();

        View fragmentView = inflater.inflate(R.layout.fragment_places, container, false);

        final RecyclerView featuredList = (RecyclerView) fragmentView.findViewById(R.id.featuredList);

        featuredList.setLayoutManager(new LinearLayoutManager(featuredList.getContext()));
        SimpleStringRecyclerViewAdapter adapter = new SimpleStringRecyclerViewAdapter(getActivity(), mPlaces);
        featuredList.setAdapter(mAdapter = adapter);

        final RelativeLayout content = (RelativeLayout) fragmentView.findViewById(R.id.content);
        final ProgressWheel progress = (ProgressWheel) fragmentView.findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FeaturedLocation");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                setupFeaturedView(list);
                crossfade(content, progress);
            }
        });

        return fragmentView;
    }

    private void crossfade(View meInvisible, final View meVisible) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        meInvisible.setAlpha(0f);
        meInvisible.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        meInvisible.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        meVisible.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        meVisible.setVisibility(View.GONE);
                    }
                });
    }

    private SimpleStringRecyclerViewAdapter mAdapter;
    private List<FeaturedLocation> mPlaces;

    public void addNewItem(FeaturedLocation item) {
        mPlaces.add(0, item);
        mAdapter.notifyDataSetChanged();
    }

    private void setupFeaturedView(List<ParseObject> featuredPlaces) {

        for(ParseObject fPlace : featuredPlaces) {
            mPlaces.add(new FeaturedLocation(fPlace));
        }

        Collections.sort(mPlaces);

        mAdapter.notifyDataSetChanged();
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<FeaturedLocation> mValues;
        private Context mContext;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public FeaturedLocation mBoundLocation;

            public final View mView;
            //public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                //mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public FeaturedLocation getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<FeaturedLocation> items) {
            mContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_places_featured, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundLocation = mValues.get(position);
            //holder.mTextView.setText(mPlaces.get(position));

//            if(position == 0 || position == 6) {
//                ((ImageView) holder.mView.findViewById(R.id.icon)).setImageResource(android.R.color.transparent);
//                ((TextView) holder.mView.findViewById(R.id.text1)).setPadding(0,0,0,0);
//            }

//            if(position > 6) {
//                ((ImageView) holder.mView.findViewById(R.id.icon)).setImageResource(R.drawable.ic_map_black_48dp);
//            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Context context = v.getContext();
                    //Intent intent = new Intent(context, BarkDetailActivity.class);
                    //intent.putExtra(BarkDetailActivity.EXTRA_POST, holder.mBoundReply);

                    //context.startActivity(intent);

                    // todo set location, navigate to main view

                    InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LATITUDE_MANUAL, mValues.get(position).getLocation().getLatitude() + "");
                    InternalAppData.Store(mContext, SharedPrefKeys.LOCATION_LONGITUDE_MANUAL, mValues.get(position).getLocation().getLongitude() + "");
                    InternalAppData.Store(mContext, SharedPrefKeys.HAS_SET_MANUAL_LOCATION, true);
                    InternalAppData.Store(mContext, SharedPrefKeys.MANUAL_TITLE, mValues.get(position).getName());
                    InternalAppData.Store(mContext, SharedPrefKeys.RADIUS, (long) mValues.get(position).getRadius());

                    MasterList.clearMasterListAllSlow();

                    //EventBus.getDefault().post(new RequestUpdatePostsEvent());

                    ((FragmentActivity) mContext).finish();
                }
            });

            final TextView text = (TextView) holder.mView.findViewById(R.id.text1);
            text.setText(mValues.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
