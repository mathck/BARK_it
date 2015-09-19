package com.barkitapp.android.parse_backend.converter;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android.parse_backend.enums.VoteType;
import com.barkitapp.android.parse_backend.objects.Reply;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ReplyConverter {

    public static List<Reply> run(Context context, HashMap<String, ArrayList<ParseObject>> result) {
        try {

            List<Reply> replyList = new ArrayList<>();
            ArrayList<ParseObject> replies = result.get("replies");
            ArrayList<ParseObject> votes = result.get("votes");

            String myUserId = UserId.get(context);

            // todo measure and improve

            // remove irrelevant votes
            if(votes != null && votes.size() != 0) {
                Iterator<ParseObject> i = votes.iterator();
                while (i.hasNext()) {
                    ParseObject vote = i.next();

                    if (!myUserId.equals(vote.getString("user_id")))
                        i.remove();
                }
            }

            for (ParseObject reply : replies) {

                int my_vote = VoteType.NEUTRAL.ordinal();

                if(votes != null && votes.size() != 0) {
                    for(ParseObject vote : votes) {
                        if(reply.getObjectId().equals(vote.getString("content_id"))) {
                            my_vote = vote.getInt("vote_type");
                        }
                    }
                }

                replyList.add(new Reply(
                    reply.getObjectId(),
                    reply.getString("user_id"),
                    reply.getString("post_id"),

                    reply.getDate("time_created"),
                    reply.getString("text"),
                    reply.getInt("vote_counter"),
                    reply.getInt("badge"),

                    reply.getParseGeoPoint("location"),
                    my_vote));
            }

            return replyList;

        } catch (Exception e) {
            if(context != null)
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return new ArrayList<>();
    }

}
