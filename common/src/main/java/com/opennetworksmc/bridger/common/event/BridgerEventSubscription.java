package com.opennetworksmc.bridger.common.event;

import com.opennetworksmc.bridger.api.event.BridgerEvent;
import com.opennetworksmc.bridger.api.event.EventSubscription;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BridgerEventSubscription<T extends BridgerEvent> implements EventSubscription<T>, EventSubscriber<T> {

    /**
     * The event bus which created this handler
     */
    private final AbstractEventBus eventBus;

    /**
     * The event class
     */
    private final Class<T> eventClass;

    /**
     * The delegate "event handler"
     */
    private final Consumer<? super T> consumer;

    /**
     * The plugin which "owns" this handler
     */
    private final @Nullable Object plugin;

    /**
     * If this handler is active
     */
    private final AtomicBoolean active = new AtomicBoolean(true);

    public BridgerEventSubscription(AbstractEventBus eventBus, Class<T> eventClass, Consumer<? super T> consumer, @Nullable Object plugin) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.consumer = consumer;
        this.plugin = plugin;
    }

    @Override
    public void close() {
        // already unregistered
        if (!this.active.getAndSet(false)) {
            return;
        }

        this.eventBus.unregisterHandler(this);
    }

    @Override
    public void invoke(@NonNull T event) throws Throwable {
        try {
            this.consumer.accept(event);
        } catch (Throwable t) {

            this.eventBus.getPlugin().getLogger().warn("Unable to pass event " + event.getClass().getSimpleName() + " to handler " + this.consumer.getClass().getName());
            t.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public @NonNull Class<T> getEventClass() {
        return this.eventClass;
    }

    @Override
    public @NonNull Consumer<? super T> getHandler() {
        return this.consumer;
    }

    public @Nullable Object getPlugin() {
        return this.plugin;
    }
}
