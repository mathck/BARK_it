package com.barkitapp.android.core.utility;

import com.barkitapp.android.parse.objects.Post;

import java.util.Comparator;

public class PostComperator {

    public static class Time implements Comparator<Post> {
        @Override
        public int compare(Post post1, Post post2) {
            return post2.getTime_created().compareTo(post1.getTime_created());
        }
    }

    public static class Vote implements Comparator<Post> {
        @Override
        public int compare(Post post1, Post post2) {
            int vote_counter_1 = post1.getVote_counter();
            int vote_counter_2 = post2.getVote_counter();

            if (vote_counter_1 == vote_counter_2)
                return 0;
            else if (vote_counter_1 > vote_counter_2)
                return 1;
            else
                return -1;
        }
    }
}
