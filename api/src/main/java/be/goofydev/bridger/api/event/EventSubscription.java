package be.goofydev.bridger.api.event;

import java.util.function.Consumer;

public interface EventSubscription<T extends BridgerEvent> extends AutoCloseable {

    Class<T> getEventClass();

    boolean isActive();

    void close();

    Consumer<? super T> getHandler();

}
