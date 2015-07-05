package com.barkitapp.android.bark_detail;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android.main.Barks;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

public class BarkReplyListFragment extends Fragment {

    private AlphaInAnimationAdapter mAdpater;
    private List<String> mValues;

    public void addNewItem(String item) {
        mValues.add(0, item);
        mAdpater.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_reply_list, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        mValues = getRandomSublist(Barks.sCheeseStrings, 30);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        SimpleStringRecyclerViewAdapter adapter = new SimpleStringRecyclerViewAdapter(getActivity(), mValues);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setStartPosition(4);
        alphaAdapter.setDuration(150);
        recyclerView.setAdapter(mAdpater = alphaAdapter);
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
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
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
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
                    .inflate(R.layout.bark_detail_reply_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        /**
         * This method converts device specific pixels to density independent pixels.
         *
         * @param dp A value in dp unit. Which we need to convert into db
         * @return A float value to represent dp equivalent to px value
         */
        public static int convertDpToPixel(float dp) {
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            return (int) (dp * (metrics.densityDpi / 160f));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position);

            holder.mView.findViewById(R.id.reply).setPadding(0, 0, 0, 0);

            if(position == 0)
                holder.mView.findViewById(R.id.reply).setPadding(0, convertDpToPixel(36), 0, 0);

            final TextView bark_text = (TextView) holder.mView.findViewById(R.id.text1);
            bark_text.setText(mValues.get(position));

            final ImageView upvote = (ImageView) holder.mView.findViewById(R.id.upvote);
            final ImageView downvote = (ImageView) holder.mView.findViewById(R.id.downvote);
            final TextView votes_count = (TextView) holder.mView.findViewById(R.id.votes_count);

            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentValue = Integer.parseInt(votes_count.getText().toString());
                    if((currentValue+1) % 10 == 0)
                    {
                        YoYo.with(Techniques.Flash)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    else {
                        YoYo.with(Techniques.BounceInUp)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
                    upvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
                    downvote.setColorFilter(null);
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) + 1));
                }
            });

            downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.BounceInDown)
                            .duration(400)
                            .playOn(votes_count);
                    votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
                    upvote.setColorFilter(null);
                    downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) - 1));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
