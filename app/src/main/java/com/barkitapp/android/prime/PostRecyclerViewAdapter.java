package com.barkitapp.android.prime;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.Messages.UpdateListItemEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.bark_detail.BarkDetailActivity;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.utility.Constants;
import com.barkitapp.android.core.utility.DistanceConverter;
import com.barkitapp.android.core.utility.TimeConverter;
import com.barkitapp.android.parse.enums.ContentType;
import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.enums.VoteType;
import com.barkitapp.android.parse.functions.PostVote;
import com.barkitapp.android.parse.objects.Post;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.ParseGeoPoint;

import java.util.List;

import de.greenrobot.event.EventBus;

public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private int mBackground;

    public List<Post> getValues() {
        return mValues;
    }

    public void setValues(List<Post> mValues) {
        this.mValues = mValues;
    }

    private List<Post> mValues;
    private Context mContext;
    private Order mOrder;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Post mBoundPost;

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

    public Post getValueAt(int position) {
        return mValues.get(position);
    }

    public PostRecyclerViewAdapter(Context context, List<Post> items, Order order) {
        mContext = context;
        mOrder = order;
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundPost = mValues.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();

                if(Constants.UNKNOWN.equals(holder.mBoundPost.getObjectId())) {
                    Toast.makeText(context, "BARK is not online, please try again later", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(context, BarkDetailActivity.class);
                intent.putExtra(BarkDetailActivity.EXTRA_POST, holder.mBoundPost.getObjectId());
                context.startActivity(intent);
            }
        });

        final TextView bark_text = (TextView) holder.mView.findViewById(R.id.text1);
        bark_text.setText(holder.mBoundPost.getText());

        final ImageView upvote = (ImageView) holder.mView.findViewById(R.id.upvote);
        final ImageView downvote = (ImageView) holder.mView.findViewById(R.id.downvote);
        final TextView votes_count = (TextView) holder.mView.findViewById(R.id.votes_count);
        votes_count.setText(holder.mBoundPost.getVote_counter() + "");

        final TextView comments_count = (TextView) holder.mView.findViewById(R.id.comments_count);
        comments_count.setText(holder.mBoundPost.getReply_counter() + "");

        final TextView hours = (TextView) holder.mView.findViewById(R.id.hours);
        hours.setText(TimeConverter.getPostAge(holder.mBoundPost.getTime_created()));

        final TextView distance = (TextView) holder.mView.findViewById(R.id.distance);
        distance.setText(DistanceConverter.GetDistanceInKm(mContext, holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()));

        if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
            upvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
            downvote.setColorFilter(null);
        }
        else if(holder.mBoundPost.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
            upvote.setColorFilter(null);
            downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
        }
        else if(holder.mBoundPost.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
            upvote.setColorFilter(null);
            downvote.setColorFilter(null);
        }

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPVOTE -> NEUTRAL
                if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.NEUTRAL);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.NEUTRAL.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.NEUTRAL.ordinal());

                    YoYo.with(Techniques.BounceInDown)
                            .duration(400)
                            .playOn(votes_count);

                    votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
                    upvote.setColorFilter(null);
                    downvote.setColorFilter(null);
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) - 1));
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() -1);
                }
                // NEUTRAL -> UPVOTE
                else if(holder.mBoundPost.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.UP_VOTE);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.UP_VOTE.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.UP_VOTE.ordinal());

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
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() + 1);
                }
                // DOWNVOTE -> UPVOTE
                else if(holder.mBoundPost.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.UP_VOTE);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.UP_VOTE.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.UP_VOTE.ordinal());

                    int currentValue = Integer.parseInt(votes_count.getText().toString());
                    if((currentValue+2) % 10 == 0)
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
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) + 2));
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() + 2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // DOWNVOTE -> NEUTRAL
                if(holder.mBoundPost.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.NEUTRAL);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.NEUTRAL.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.NEUTRAL.ordinal());

                    YoYo.with(Techniques.BounceInUp)
                            .duration(400)
                            .playOn(votes_count);

                    votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
                    upvote.setColorFilter(null);
                    downvote.setColorFilter(null);
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) + 1));
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() + 1);
                }
                // NEUTRAL -> DOWNVOTE
                else if(holder.mBoundPost.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.DOWN_VOTE);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.DOWN_VOTE.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.DOWN_VOTE.ordinal());

                    int currentValue = Integer.parseInt(votes_count.getText().toString());
                    if((currentValue-1) % 10 == 0)
                    {
                        YoYo.with(Techniques.Flash)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    else {
                        YoYo.with(Techniques.BounceInDown)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
                    downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
                    upvote.setColorFilter(null);
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) - 1));
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() - 1);
                }
                // UPVOTE -> DOWNVOTE
                else if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {

                    PostVote.run(Constants.TEMP_USER_ID,
                            holder.mBoundPost.getObjectId(),
                            ContentType.POST,
                            new ParseGeoPoint(holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()),
                            VoteType.DOWN_VOTE);

                    Post cur = MasterList.GetPost(holder.mBoundPost.getObjectId());
                    cur.setMy_Vote(VoteType.DOWN_VOTE.ordinal());
                    cur.save();

                    holder.mBoundPost.setMy_Vote(VoteType.DOWN_VOTE.ordinal());

                    int currentValue = Integer.parseInt(votes_count.getText().toString());
                    if((currentValue-2) % 10 == 0)
                    {
                        YoYo.with(Techniques.Flash)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    else {
                        YoYo.with(Techniques.BounceInDown)
                                .duration(400)
                                .playOn(votes_count);
                    }
                    votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
                    downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
                    upvote.setColorFilter(null);
                    votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) - 2));
                    holder.mBoundPost.setVote_counter(holder.mBoundPost.getVote_counter() - 2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}