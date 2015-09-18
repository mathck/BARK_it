package com.barkitapp.android.Messages;

import com.barkitapp.android.parse.enums.Order;
import com.barkitapp.android.parse.objects.Post;

public class UpdateListItemEvent extends EventMessage {

        public final Post post;
        public final Order order;

        public UpdateListItemEvent(Post post, Order order) {
                this.post = post;
                this.order = order;
        }
}
