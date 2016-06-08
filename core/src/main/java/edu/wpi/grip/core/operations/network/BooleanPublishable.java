package edu.wpi.grip.core.operations.network;

import java.util.function.Function;

import javax.annotation.concurrent.Immutable;

/**
 * An adapter to allow booleans to be published from GRIP sockets into a {@link NetworkPublisher}
 *
 * @see PublishAnnotatedOperation#PublishAnnotatedOperation(MapNetworkPublisherFactory, Function)
 */
@Immutable
public final class BooleanPublishable implements Publishable {
    private final boolean bool;

    public BooleanPublishable(Boolean bool) {
        this.bool = bool.booleanValue();
    }

    @PublishValue(weight = 1)
    public boolean getValue() {
        return bool;
    }
}
