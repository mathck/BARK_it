package com.barkitapp.android._main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barkitapp.android.events.UpdateListItemEvent;
import com.barkitapp.android.R;
import com.barkitapp.android.bark_detail.BarkDetailActivity;
import com.barkitapp.android._core.services.MasterList;
import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android._core.utility.converter.DistanceConverter;
import com.barkitapp.android._core.utility.converter.TimeConverter;
import com.barkitapp.android.parse_backend.enums.ContentType;
import com.barkitapp.android.parse_backend.enums.MediaType;
import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.enums.VoteType;
import com.barkitapp.android.parse_backend.functions.PostVote;
import com.barkitapp.android.parse_backend.objects.Post;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private int mBackground;

    private ImageLoader imageLoader;

    public List<Post> getValues() {
        return mValues;
    }

    private HashMap<String, VoteType> mVotes;

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

        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public PostRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        PostRecyclerViewAdapter.ViewHolder viewHolder = null;

        switch (MediaType.values()[viewType]) {
            case WITHOUT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_bark, parent, false);
                view.setBackgroundResource(mBackground);

                viewHolder = new PostRecyclerViewAdapter.ViewHolder(view);
                break;
            case PICTURE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_bark_picture, parent, false);
                view.setBackgroundResource(mBackground);

                viewHolder = new PostRecyclerViewAdapter.ViewHolder(view);
                break;
            default:
                // todo dont show view
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getMedia_type();
    }

    private void configureViewWithout(final ViewHolder holder, int position) {
    
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
        String distanceString = DistanceConverter.GetDistanceInKm(mContext, holder.mBoundPost.getLatitude(), holder.mBoundPost.getLongitude());
        distance.setText(distanceString);

        if(distanceString.startsWith("6"))
            holder.mView.setVisibility(View.GONE);

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

        if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.UP_VOTE) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.accent));
            upvote.setColorFilter(mContext.getResources().getColor(R.color.accent));
            downvote.setColorFilter(null);
        }
        else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.DOWN_VOTE) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.primary));
            upvote.setColorFilter(null);
            downvote.setColorFilter(mContext.getResources().getColor(R.color.primary));
        }
        else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.NEUTRAL) {
            votes_count.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
            upvote.setColorFilter(null);
            downvote.setColorFilter(null);
        }

        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.UP_VOTE) {
                    // UPVOTE -> NEUTRAL
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.NEUTRAL, -1);
                }
                else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.NEUTRAL) {
                    // NEUTRAL -> UPVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.UP_VOTE, +1);
                }
                else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.DOWN_VOTE) {
                    // DOWNVOTE -> UPVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.UP_VOTE, +2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.DOWN_VOTE) {
                    // DOWNVOTE -> NEUTRAL
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.NEUTRAL, +1);
                }
                else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.NEUTRAL) {
                    // NEUTRAL -> DOWNVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -1);
                }
                else if(VoteType.values()[holder.mBoundPost.getMy_Vote()] == VoteType.UP_VOTE) {
                    // UPVOTE -> DOWNVOTE
                    performVoting(holder.mBoundPost, votes_count, upvote, downvote, VoteType.DOWN_VOTE, -2);
                }

                EventBus.getDefault().post(new UpdateListItemEvent(holder.mBoundPost, mOrder));
            }
        });
    }

    private void configureViewPicture(final ViewHolder holder, int position) {
        configureViewWithout(holder, position);

        final ImageView imageView = (ImageView) holder.mView.findViewById(R.id.image);

        if(imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        //imageLoader.displayImage(holder.mBoundPost.getImage_url(), imageView);

        final RelativeLayout loadingLayout = (RelativeLayout) holder.mView.findViewById(R.id.loadingLayout);
        final ImageView spinner = (ImageView) holder.mView.findViewById(R.id.progressBar);
        final RelativeLayout infoBar = (RelativeLayout) holder.mView.findViewById(R.id.infoBar);
        //final RelativeLayout voteBar = (RelativeLayout) holder.mView.findViewById(R.id.voteBar);

        final Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.shake);

        imageLoader.displayImage(holder.mBoundPost.getImage_url(), imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.startAnimation(animation);
                spinner.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);

                imageView.setVisibility(View.GONE);
                infoBar.setVisibility(View.GONE);
                //voteBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                spinner.clearAnimation();
                spinner.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);

                imageView.setVisibility(View.VISIBLE);
                infoBar.setVisibility(View.VISIBLE);
                //voteBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.clearAnimation();
                spinner.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);

                imageView.setVisibility(View.VISIBLE);
                infoBar.setVisibility(View.VISIBLE);
                //voteBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        switch (MediaType.values()[holder.getItemViewType()]) {
            case WITHOUT:
                configureViewWithout(holder, position);
                break;
            case PICTURE:
                configureViewPicture(holder, position);
                break;
            default:
                break;
        }
    }

    private void performVoting(Post boundPost, TextView votes_count, ImageView upvote, ImageView downvote, VoteType voteType, int valueChange) {
        // post to parse
        PostVote.run(mContext, UserId.get(mContext),
                boundPost.getObjectId(),
                ContentType.POST,
                new ParseGeoPoint(boundPost.getLatitude(), boundPost.getLongitude()),
                voteType);

        // store in master list
        ParseObject post = MasterList.GetPost(boundPost.getObjectId());
        if(post != null) {
            post.put("my_vote", voteType.ordinal());
            post.pinInBackground();
        }

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