package com.barkitapp.android.events;

public class UserIdRecievedEvent extends EventMessage {

        public final String userId;

        public UserIdRecievedEvent(String userId) {
                this.userId = userId;
        }
}
