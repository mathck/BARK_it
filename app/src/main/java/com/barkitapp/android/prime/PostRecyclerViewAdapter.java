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

import com.barkitapp.android.Messages.UpdateListItemEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.bark_detail.BarkDetailActivity;
import com.barkitapp.android.core.services.MasterList;
import com.barkitapp.android.core.services.UserId;
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
    private String myId;

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

        myId = UserId.get(mContext);
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

                // Bark local only?
                //if(Constants.UNKNOWN.equals(holder.mBoundPost.getObjectId())) {
                //    Toast.makeText(context, "BARK is not online, please try again later", Toast.LENGTH_LONG).show();
                //    return;
                //}

                // Open Detail View
                Intent intent = new Intent(context, BarkDetailActivity.class);
                intent.putExtra(BarkDetailActivity.EXTRA_POST, holder.mBoundPost.getObjectId());
                context.startActivity(intent);
            }
        });

        // set post text
        final TextView bark_text = (TextView) holder.mView.findViewById(R.id.text1);
        bark_text.setText(holder.mBoundPost.getText());

        // set voting stuff
        final ImageView upvote = (ImageView) holder.mView.findViewById(R.id.upvote);
        final ImageView downvote = (ImageView) holder.mView.findViewById(R.id.downvote);
        final TextView votes_count = (TextView) holder.mView.findViewById(R.id.votes_count);
        votes_count.setText(holder.mBoundPost.getVote_counter() + "");

        // set comment count
        final TextView comments_count = (TextView) holder.mView.findViewById(R.id.comments_count);
        comments_count.setText(holder.mBoundPost.getReply_counter() + "");

        // set age
        final TextView hours = (TextView) holder.mView.findViewById(R.id.hours);
        hours.setText(TimeConverter.getPostAge(holder.mBoundPost.getTime_created()));

        // set distance
        final TextView distance = (TextView) holder.mView.findViewById(R.id.distance);
        distance.setText(DistanceConverter.GetDistanceInKm(mContext, holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude()));

        // set ME
        //if(holder.mBoundPost.getUserId().equals(myId)) {
        //    final TextView me = (TextView) holder.mView.findViewById(R.id.me);
        //    me.setVisibility(View.VISIBLE);
        //}

        /*
        if(holder.mBoundPost.getObjectId().equals(Constants.UNKNOWN))
        {
            upvote.setVisibility(View.GONE);
            downvote.setVisibility(View.GONE);
            votes_count.setVisibility(View.GONE);

            return;
        }
        */

        // set the colors for already voted posts
        if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.accent));
            upvote.setColorFilter(mContext.getResources().getColor(R.color.accent));
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
                if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
                    // UPVOTE -> NEUTRAL
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.NEUTRAL, -1);
                }
                else if(holder.mBoundPost.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
                    // NEUTRAL -> UPVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.UP_VOTE, +1);
                }
                else if(holder.mBoundPost.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
                    // DOWNVOTE -> UPVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.UP_VOTE, +2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mBoundPost.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
                    // DOWNVOTE -> NEUTRAL
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.NEUTRAL, +1);
                }
                else if(holder.mBoundPost.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
                    // NEUTRAL -> DOWNVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -1);
                }
                else if(holder.mBoundPost.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
                    // UPVOTE -> DOWNVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });
    }

    private void performVoting(Post boundPost, TextView votes_count, ImageView upvote, ImageView downvote, VoteType voteType, int valueChange) {
        // post to parse
        PostVote.run(mContext, UserId.get(mContext),
                boundPost.getObjectId(),
                ContentType.POST,
                new ParseGeoPoint(boundPost.getLatitude(), boundPost.getLongitude()),
                voteType);

        // store in master list
        Post cur = MasterList.GetPost(boundPost.getObjectId());
        cur.setMy_Vote(voteType.ordinal());
        cur.save();

        // set this item ui
        boundPost.setMy_Vote(voteType.ordinal());

        // vote counter animation
        int currentValue = Integer.parseInt(votes_count.getText().toString());
        if((currentValue + valueChange) % 10 == 0)
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

        if(voteType.equals(VoteType.NEUTRAL)) {
            // vote counter -> grey
            votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
            upvote.setColorFilter(null);
            downvote.setColorFilter(null);
        }
        else {
            // vote counter -> red
            if(voteType.equals(VoteType.UP_VOTE)) {
                votes_count.setTextColor(mContext.getResources().getColor(R.color.accent));
                upvote.setColorFilter(mContext.getResources().getColor(R.color.accent));
                downvote.setColorFilter(null);
            }
            else if(voteType.equals(VoteType.DOWN_VOTE)) {
                votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
                upvote.setColorFilter(null);
                downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
            }
        }

        votes_count.setText(String.valueOf(Integer.parseInt(votes_count.getText().toString()) + valueChange));
        boundPost.setVote_counter(boundPost.getVote_counter() + valueChange);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}