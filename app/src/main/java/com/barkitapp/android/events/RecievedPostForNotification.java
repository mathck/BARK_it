package com.barkitapp.android.events;

import com.barkitapp.android.parse_backend.objects.Post;

public class RecievedPostForNotification extends EventMessage {

        Post post;
        public RecievedPostForNotification(Post post) {
                this.post = post;
        }

        public Post getPost() {
                return post;
        }
}
