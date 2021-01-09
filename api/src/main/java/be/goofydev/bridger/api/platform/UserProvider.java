package be.goofydev.bridger.api.platform;

import be.goofydev.bridger.api.model.user.User;

import java.util.Set;
import java.util.UUID;

public interface UserProvider<I, T extends User> {

    T get(UUID uniqueId);

    boolean has(UUID uniqueId);

    T add(I player);

    T remove(UUID uniqueId);

    Set<T> getAll();
}
