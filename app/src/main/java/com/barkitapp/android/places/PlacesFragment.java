package com.barkitapp.android.places;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barkitapp.android.R;

import java.util.ArrayList;
import java.util.List;

public class PlacesFragment extends Fragment {

    public PlacesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.places_fragment, container, false);

        RecyclerView featuredList = (RecyclerView) fragmentView.findViewById(R.id.featuredList);

        setupFeaturedView(featuredList);

        return fragmentView;
    }

    private SimpleStringRecyclerViewAdapter mAdpater;
    private List<String> mPlaces;

    public void addNewItem(String item) {
        mPlaces.add(0, item);
        mAdpater.notifyDataSetChanged();
    }

    private void setupFeaturedView(RecyclerView recyclerView) {
        mPlaces = new ArrayList<>();
        mPlaces.add("Featured");
        mPlaces.add("Featured 1");
        mPlaces.add("Featured 2");
        mPlaces.add("Featured 3");
        mPlaces.add("Featured 4");
        mPlaces.add("Featured 5");

        mPlaces.add("My Places");
        mPlaces.add("Place 1");
        mPlaces.add("Place 2");
        mPlaces.add("Place 3");
        mPlaces.add("Place 4");
        mPlaces.add("Place 5");

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        SimpleStringRecyclerViewAdapter adapter = new SimpleStringRecyclerViewAdapter(getActivity(), mPlaces);

        recyclerView.setAdapter(mAdpater = adapter);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;
        private Context mContext;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

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

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            mContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.places_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position);
            //holder.mTextView.setText(mPlaces.get(position));

            if(position == 0 || position == 6) {
                ((ImageView) holder.mView.findViewById(R.id.icon)).setImageResource(android.R.color.transparent);
                ((TextView) holder.mView.findViewById(R.id.text1)).setPadding(0,0,0,0);
            }

            if(position > 6) {
                ((ImageView) holder.mView.findViewById(R.id.icon)).setImageResource(R.drawable.ic_map_black_48dp);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Context context = v.getContext();
                    //Intent intent = new Intent(context, BarkDetailActivity.class);
                    //intent.putExtra(BarkDetailActivity.EXTRA_NAME, holder.mBoundString);

                    //context.startActivity(intent);

                    // todo set location, navigate to main view
                }
            });

            final TextView text = (TextView) holder.mView.findViewById(R.id.text1);
            text.setText(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
