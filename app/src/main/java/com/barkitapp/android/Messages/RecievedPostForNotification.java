package com.barkitapp.android.Messages;

import com.barkitapp.android.parse.objects.Post;

public class RecievedPostForNotification {

        Post post;
        public RecievedPostForNotification(Post post) {
                this.post = post;
        }

        public Post getPost() {
                return post;
        }
}
