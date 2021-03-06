package com.barkitapp.android.bark_detail;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.LocationService;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.converter.DistanceConverter;
import com.barkitapp.android._core.utility.converter.TimeConverter;
import com.barkitapp.android.parse_backend.enums.ContentType;
import com.barkitapp.android.parse_backend.enums.VoteType;
import com.barkitapp.android.parse_backend.functions.Flag;
import com.barkitapp.android.parse_backend.functions.PostVote;
import com.barkitapp.android.parse_backend.objects.Post;
import com.barkitapp.android.parse_backend.objects.Reply;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.ParseGeoPoint;

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
    private BarkDetailActivity mContext;
    private Post mPost;
    private String myId = "";

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

    public ReplyRecyclerViewAdapter(BarkDetailActivity context, List<Reply> items) {
        mContext = context;
        TypedValue mTypedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;

        myId = UserId.get(mContext);

        mPost = MasterList.GetPostPost(context.mPostObjectId);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_reply, parent, false);
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
            holder.mView.findViewById(R.id.reply).setPadding(0, convertDpToPixel(8), 0, 0);
        else if(position == (getItemCount() - 1))
            holder.mView.findViewById(R.id.reply).setPadding(0, 0, 0, convertDpToPixel(140));

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

        final ImageView flagReply = (ImageView) holder.mView.findViewById(R.id.flagReply);

        // set OP
        //if(mPost != null && holder.mBoundReply.getUserId().equals(mPost.getUserId())) {
        //    final TextView op = (TextView) holder.mView.findViewById(R.id.op);
        //    op.setVisibility(View.VISIBLE);
        //}
        //else if(holder.mBoundReply.getUserId().equals(myId)) {
        //    final TextView op = (TextView) holder.mView.findViewById(R.id.op);
        //    op.setVisibility(View.VISIBLE);
        //}

        // set ME
        //if(holder.mBoundReply.getUserId().equals(myId)) {
        //    final TextView me = (TextView) holder.mView.findViewById(R.id.me);
        //    me.setVisibility(View.VISIBLE);
        //}

        /*
        if(holder.mBoundReply.getObjectId().equals(Constants.UNKNOWN))
        {
            upvote.setVisibility(View.GONE);
            downvote.setVisibility(View.GONE);
            votes_count.setVisibility(View.GONE);
            flagReply.setVisibility(View.GONE);

            return;
        }
        */

        // set the colors for already voted posts
        if(holder.mBoundReply.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.accent));
            upvote.setColorFilter(mContext.getResources().getColor(R.color.accent));
            downvote.setColorFilter(null);
        }
        else if(holder.mBoundReply.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
            upvote.setColorFilter(null);
            downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
        }
        else if(holder.mBoundReply.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
            upvote.setColorFilter(null);
            downvote.setColorFilter(null);
        }

        flagReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Coordinates location = LocationService.getLocation(mContext);

                if(location == null) {
                    Toast.makeText(mContext, mContext.getString(R.string.please_wait_try_again), Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.inappropriate_reply))
                        .setMessage(mContext.getString(R.string.are_you_sure_flag_reply))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Flag.run(mContext,
                                        UserId.get(mContext),
                                        holder.mBoundReply.getObjectId(),
                                        ContentType.REPLY,
                                        new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_flag)
                        .show();
            }
        });

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mBoundReply.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
                    // UPVOTE -> NEUTRAL
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.NEUTRAL, -1);
                }
                else if(holder.mBoundReply.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
                    // NEUTRAL -> UPVOTE
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.UP_VOTE, +1);
                }
                else if(holder.mBoundReply.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
                    // DOWNVOTE -> UPVOTE
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.UP_VOTE, +2);
                }
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mBoundReply.getMy_Vote() == VoteType.DOWN_VOTE.ordinal()) {
                    // DOWNVOTE -> NEUTRAL
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.NEUTRAL, +1);
                }
                else if(holder.mBoundReply.getMy_Vote() == VoteType.NEUTRAL.ordinal()) {
                    // NEUTRAL -> DOWNVOTE
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -1);
                }
                else if(holder.mBoundReply.getMy_Vote() == VoteType.UP_VOTE.ordinal()) {
                    // UPVOTE -> DOWNVOTE
                    performVoting(holder.mBoundReply, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -2);
                }
            }
        });
    }

    private void performVoting(Reply boundReply, TextView votes_count, ImageView upvote, ImageView downvote, VoteType voteType, int valueChange) {
        // post to parse
        PostVote.run(mContext, UserId.get(mContext),
                boundReply.getObjectId(),
                ContentType.REPLY,
                new ParseGeoPoint(boundReply.getLocation().getLatitude(), boundReply.getLocation().getLongitude()),
                voteType);

        // set this item ui
        boundReply.setMy_Vote(voteType.ordinal());

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
        boundReply.setVote_counter(boundReply.getVote_counter() + valueChange);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}