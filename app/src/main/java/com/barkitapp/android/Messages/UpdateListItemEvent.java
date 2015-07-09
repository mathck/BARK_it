package com.barkitapp.android.Messages;

import com.barkitapp.android.parse.objects.Post;

public class UpdateListItemEvent {

        public final Post post;

        public UpdateListItemEvent(Post post) {
                this.post = post;
        }
}
