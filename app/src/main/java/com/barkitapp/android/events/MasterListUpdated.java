package com.barkitapp.android.events;

import com.barkitapp.android.parse_backend.enums.Order;

public class MasterListUpdated extends EventMessage {

    Order order;
    public MasterListUpdated(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
