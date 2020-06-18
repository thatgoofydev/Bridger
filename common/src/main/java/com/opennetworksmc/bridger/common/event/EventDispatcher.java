package com.opennetworksmc.bridger.common.event;

import com.opennetworksmc.bridger.api.event.BridgerEvent;
import com.opennetworksmc.bridger.api.event.user.UserJoinEvent;
import com.opennetworksmc.bridger.api.event.user.UserLeaveEvent;
import com.opennetworksmc.bridger.api.model.user.User;

public class EventDispatcher {

    private final AbstractEventBus eventBus;

    public EventDispatcher(AbstractEventBus eventBus) {
        this.eventBus = eventBus;

    }

    private boolean shouldPost(Class<? extends BridgerEvent> eventClass) {
        return this.eventBus.shouldPost(eventClass);
    }

    private void post(BridgerEvent event) {
        if (this.shouldPost(event.getClass())) {
            this.eventBus.post(event);
        }
    }

    public void dispatchUserJoin(User user) {
        post(new UserJoinEvent(user));
    }

    public void dispatchUserLeave(User user) {
        post(new UserLeaveEvent(user));
    }

}
