package be.goofydev.bridger.api.event.user;

import be.goofydev.bridger.api.event.BridgerEvent;
import be.goofydev.bridger.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserJoinEvent implements BridgerEvent {

    private final User user;

    public UserJoinEvent(@NonNull User user) {
        this.user = user;
    }

    public @NonNull User getUser() {
        return user;
    }
}
