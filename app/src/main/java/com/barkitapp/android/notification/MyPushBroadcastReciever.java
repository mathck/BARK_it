package com.barkitapp.android.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.barkitapp.android.R;
import com.barkitapp.android.bark_detail.BarkDetailActivity;
import com.barkitapp.android.parse.enums.Push;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MyPushBroadcastReciever extends ParsePushBroadcastReceiver {

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

    /*
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Toast.makeText(context, "@Beni, das beudetet ich habe den Push richtig erhalten.", Toast.LENGTH_LONG).show();
    }
    */

    @Override
    protected void onPushReceive(Context context, Intent intent) {

        if(replies == null)
            replies = new LinkedHashMap<>();

        String text = "";
        String post_id = "";
        Push type = Push.Reply;

        JSONObject data = getDataFromIntent(intent);
        if(data != null)
        {
            try {
                type = Push.values()[data.getInt("push_type")];
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(data != null && type == Push.Reply) {
            try {
                text = data.getString("alert");
                post_id = data.getString("post_id");

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
                    inboxStyle.setBigContentTitle(current.size() + " new replies");
                }

                builder.setContentTitle("Bark reply");
                builder.setContentText(text);
                builder.setSmallIcon(R.drawable.ic_bark);
                builder.setSound(notifySound);
                builder.setGroup(type.toString());
                builder.setGroupSummary(true);
                builder.setAutoCancel(true);

                resultIntent = new Intent(context, BarkDetailActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra(BarkDetailActivity.EXTRA_POST_ID, post_id);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setContentIntent(resultPendingIntent);
                builder.setVibrate(new long[]{0, 500});

                //LED
                builder.setLights(Color.RED, 2000, 500);

                // post_id -> int
                String t = "";
                for (int i = 0; i < post_id.length(); ++i) {
                    char ch = post_id.charAt(i);
                    int n = (int)ch - (int)'a' + 1;

                    if(n < 0)
                        n *= -1;

                    t += String.valueOf(n);
                }

                notificationManager.notify(Integer.parseInt(t.substring(0, t.length() / 3)), builder.build());

            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }
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
