package com.barkitapp.android.events;

import com.barkitapp.android.parse_backend.enums.Order;
import com.barkitapp.android.parse_backend.objects.Post;

public class UpdateListItemEvent extends EventMessage {

        public final Post post;
        public final Order order;

        public UpdateListItemEvent(Post post, Order order) {
                this.post = post;
                this.order = order;
        }
}
