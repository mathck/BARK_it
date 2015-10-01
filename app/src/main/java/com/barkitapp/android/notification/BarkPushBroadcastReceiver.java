package com.barkitapp.android.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.activities.MyBarksActivity;
import com.barkitapp.android.bark_detail.BarkDetailActivity;
import com.barkitapp.android._core.utility.Constants;
import com.barkitapp.android._core.utility.Settings;
import com.barkitapp.android.parse_backend.converter.NotificationConverter;
import com.barkitapp.android.parse_backend.enums.ContentType;
import com.barkitapp.android.parse_backend.enums.Push;
import com.barkitapp.android._main.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BarkPushBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";
    Uri notifySound;
    Intent resultIntent;
    private static LinkedHashMap<String, ArrayList<String>> replies;

    /*
    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        return null;
    }
    */

    @Override
    protected void onPushOpen(Context context, Intent intent) {

    }


    @Override
    protected void onPushReceive(Context context, Intent intent) {

        if(replies == null)
            replies = new LinkedHashMap<>();

        Push type = Push.Reply;

        JSONObject data = getDataFromIntent(intent);
        if(data != null)
        {
            try {
                type = Push.values()[data.getInt("push_type")];
            } catch (Exception e) {
                return;
            }
        }

        if(data != null) {
            switch (type) {
                case Reply:
                    onReplyNotification(context, data, type);
                    break;
                case Vote:
                    onVoteNotification(context, data, type);
                    break;
                case PostOfTheDay:
                    onBarkOfTheDayNotification(context, data, type);
                    break;
            }
        }

        if(type.equals(Push.FriendInvited)) {
            onFriendInvitedNotification(context);
        }
    }

    private void onFriendInvitedNotification(Context context) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(Constants.FRIEND_INVITED_NOTIFICATION_ID);

        notifySound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("Friend Invited");
        builder.setContentText("You have successfully invited a friend");
        builder.setSmallIcon(R.drawable.ic_bark);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_friend_notification);
        builder.setLargeIcon(largeIcon);

        if(Settings.isNotificationSoundEnabled(context))
            builder.setSound(notifySound);

//            builder.setGroup(type.toString());
//            builder.setGroupSummary(true);
        builder.setAutoCancel(true);

        resultIntent = new Intent(context, MyBarksActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.setAction(Long.toString(System.currentTimeMillis()));

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setVibrate(new long[]{0, 500});

        //LED
        builder.setLights(Color.RED, 2000, 500);

        notificationManager.notify(Constants.FRIEND_INVITED_NOTIFICATION_ID, builder.build());
    }

    private void onBarkOfTheDayNotification(Context context, JSONObject data, Push type) {
        try {
            if(!Settings.isBarkOfTheDayNotificationEnabled(context))
                return;

            String post_id = data.getString("post_id");
            String text = data.getString("alert");

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(Constants.BARK_OF_THE_DAY_NOTIFICATION_ID);

            notifySound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setContentTitle(context.getString(R.string.bark_of_the_day));
            builder.setContentText(text);
            builder.setSmallIcon(R.drawable.ic_bark);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_hot_notification);
            builder.setLargeIcon(largeIcon);

            if(Settings.isNotificationSoundEnabled(context))
                builder.setSound(notifySound);

//            builder.setGroup(type.toString());
//            builder.setGroupSummary(true);
            builder.setAutoCancel(true);

            resultIntent = new Intent(context, BarkDetailActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_ID, post_id);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_CAME_FROM_NOTIFICATION, true);
            resultIntent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);
            builder.setVibrate(new long[]{0, 500});

            //LED
            builder.setLights(Color.RED, 2000, 500);

            notificationManager.notify(Constants.BARK_OF_THE_DAY_NOTIFICATION_ID, builder.build());

        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onVoteNotification(Context context, JSONObject data, Push type) {
        try {
            if(!Settings.isVoteNotificationEnabled(context))
                return;

            String content_type = data.getString("content_type");
            String post_id;
            String text;
            String vote_count = data.getString("vote_count");

            try {
                if(ContentType.REPLY.ordinal() == Integer.parseInt(content_type)) {
                    post_id = data.getString("parent_id");
                    text = context.getString(R.string.notification_your_reply_has) + " " + vote_count + " " + context.getString(R.string.points);
                }
                else if (ContentType.POST.ordinal() == Integer.parseInt(content_type)) {
                    post_id = data.getString("content_id");
                    text = context.getString(R.string.notification_your_bark_has) + " " + vote_count + " " + context.getString(R.string.points);
                }
                else {
                    return;
                }
            }
            catch (Exception e) {
                return;
            }

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel(NotificationConverter.getIdFromPostId(post_id) + Constants.UPVOTE_NOTIFICATION_VALUE);

            notifySound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setContentTitle(vote_count + " " + context.getString(R.string.points));
            builder.setContentText(text);
            builder.setSmallIcon(R.drawable.ic_bark);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_up_notification);
            builder.setLargeIcon(largeIcon);

            if(Settings.isNotificationSoundEnabled(context))
                builder.setSound(notifySound);

//            builder.setGroup(type.toString());
//            builder.setGroupSummary(true);
            builder.setAutoCancel(true);

            resultIntent = new Intent(context, BarkDetailActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_ID, post_id);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_CAME_FROM_NOTIFICATION, true);
            resultIntent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);
            builder.setVibrate(new long[]{0, 500});

            //LED
            builder.setLights(Color.RED, 2000, 500);

            notificationManager.notify(NotificationConverter.getIdFromPostId(post_id) + Constants.UPVOTE_NOTIFICATION_VALUE, builder.build());

        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onReplyNotification(Context context, JSONObject data, Push type) {
        try {
            if(!Settings.isReplyNotificationEnabled(context))
                return;

            String text = data.getString("alert");
            String post_id = data.getString("post_id");

            ArrayList<String> current = replies.get(post_id);
            if(current == null)
                current = new ArrayList<>();

            current.add(text);
            replies.remove(post_id);
            replies.put(post_id, current);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notifySound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            if(current.size() != 1) {
                builder.setNumber(current.size());
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                // Moves events into the big view
                for (int i=0; i < current.size(); i++) {
                    inboxStyle.addLine(current.get(i));
                }

                builder.setStyle(inboxStyle);

                // Sets a title for the Inbox style big view
                inboxStyle.setBigContentTitle(current.size() + " " + context.getString(R.string.new_replies));
            }

            builder.setContentTitle(context.getString(R.string.bark_reply));
            builder.setContentText(text);
            builder.setSmallIcon(R.drawable.ic_bark);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_chat_notification);
            builder.setLargeIcon(largeIcon);

            if(Settings.isNotificationSoundEnabled(context))
                builder.setSound(notifySound);

            builder.setAutoCancel(true);

            resultIntent = new Intent(context, BarkDetailActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_ID, post_id);
            resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_CAME_FROM_NOTIFICATION, true);
            resultIntent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);
            builder.setVibrate(new long[]{0, 500});

            //LED
            builder.setLights(Color.RED, 2000, 500);

            notificationManager.notify(NotificationConverter.getIdFromPostId(post_id), builder.build());

        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNotificationVisible(Context context, int id) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}
