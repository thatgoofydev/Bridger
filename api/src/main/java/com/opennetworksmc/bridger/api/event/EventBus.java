package com.opennetworksmc.bridger.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.function.Consumer;

public interface EventBus {
    @NonNull <T extends BridgerEvent> EventSubscription<T> subscribe(@NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler);
    @NonNull <T extends BridgerEvent> EventSubscription<T> subscribe(Object plugin, @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler);
    @NonNull <T extends BridgerEvent> Set<EventSubscription<T>> getSubscriptions(@NonNull Class<T> eventClass);
}
