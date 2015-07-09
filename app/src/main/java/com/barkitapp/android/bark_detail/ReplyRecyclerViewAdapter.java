package com.barkitapp.android.bark_detail;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barkitapp.android.R;
import com.barkitapp.android.core.utility.DistanceConverter;
import com.barkitapp.android.core.utility.TimeConverter;
import com.barkitapp.android.parse.objects.Reply;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

public class ReplyRecyclerViewAdapter
        extends RecyclerView.Adapter<ReplyRecyclerViewAdapter.ViewHolder> {

    private int mBackground;

    public List<Reply> getValues() {
        return mValues;
    }

    public void setValues(List<Reply> mValues) {
        this.mValues = mValues;
    }

    private List<Reply> mValues;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Reply mBoundReply;

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

    public Reply getValueAt(int position) {
        return mValues.get(position);
    }

    public ReplyRecyclerViewAdapter(Context context, List<Reply> items) {
        mContext = context;
        TypedValue mTypedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_bark_list_item, parent, false);
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
        holder.mBoundReply = mValues.get(position);

        holder.mView.findViewById(R.id.reply).setPadding(0, 0, 0, 0);

        if(position == 0)
            holder.mView.findViewById(R.id.reply).setPadding(0, convertDpToPixel(36), 0, 0);

        final TextView bark_text = (TextView) holder.mView.findViewById(R.id.text1);
        bark_text.setText(holder.mBoundReply.getText());

        final ImageView upvote = (ImageView) holder.mView.findViewById(R.id.upvote);
        final ImageView downvote = (ImageView) holder.mView.findViewById(R.id.downvote);
        final TextView votes_count = (TextView) holder.mView.findViewById(R.id.votes_count);
        votes_count.setText(holder.mBoundReply.getVote_counter() + "");

        final TextView hours = (TextView) holder.mView.findViewById(R.id.hours);
        hours.setText(TimeConverter.getPostAge(holder.mBoundReply.getTime_created()));

        final TextView distance = (TextView) holder.mView.findViewById(R.id.distance);
        distance.setText(DistanceConverter.GetDistanceInKm(mContext, holder.mBoundReply.getLocation().getLatitude(), holder.mBoundReply.getLocation().getLongitude()));

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