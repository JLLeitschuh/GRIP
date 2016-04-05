package edu.wpi.grip.core.operations.network;


import com.google.common.reflect.TypeToken;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @param <S> The type of the socket that will be published
 * @param <P> The type of the publisher that will be used to publish values
 */
public abstract class PublishOperation<S, P extends NetworkPublisher> implements Operation {
    private final TypeToken<? super S> socketType;
    private final SocketHint<? super S> publishHint;
    private final SocketHint<String> nameHint;

    private final InputSocket<? super S> publishSocket;
    private final InputSocket<String> nameSocket;

    protected PublishOperation(InputSocket.Factory inputSocketFactory) {
        this.socketType = new TypeToken<S>(getClass()) {};
        this.publishHint = new SocketHint.Builder<>(socketType.getRawType()).identifier("Value").build();
        this.nameHint = SocketHints.Inputs.createTextSocketHint(getSocketHintStringPrompt(), "my" + socketType.getRawType().getSimpleName());

        this.publishSocket = inputSocketFactory.create(publishHint);
        this.nameSocket = inputSocketFactory.create(nameHint);
    }

    public final String getName() {
        return getNetworkProtocolNameAcronym() + "Publish " + socketType.getRawType().getSimpleName();
    }

//    @Override
//    public final String getDescription() {
//        return "Publish a " + socketType.getRawType().getSimpleName() + " to " + getNetworkProtocolName();
//    }


    @Override
    public final InputSocket<?>[] createInputSockets() {
        final List<InputSocket<?>> customSockets = provideRemainingInputSockets();
        final InputSocket<?>[] sockets = new InputSocket[2 + customSockets.size()];
        int i = 0;
        // Create an input for the actual object being published
        sockets[i++] = publishSocket;

        // Create a string input for the key used by the network protocol
        sockets[i++] = nameSocket;
        for (InputSocket<?> socket : customSockets) {
            sockets[i++] = socket;
        }
        return sockets;
    }

    @Override
    public final OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket<?>[0];
    }

    /**
     * There should be a different instance of {@link NetworkPublisher} for each step.
     * The NetworkPublisher will be closed by the close function when this step is removed.
     *
     * @return The publisher that will be used for this step.
     */
    @Override
    public final Optional<?> createData() {
        return Optional.of(createPublisher());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void perform(Optional<?> data) {
        // Get the socket value that should be published
        final S socketValue = (S) socketType.getRawType().cast(publishSocket.getValue().get());
        // Get the subfield
        final String subField = nameSocket.getValue().get();

        if (subField.isEmpty()) {
            throw new IllegalArgumentException("Need key to publish to " + getNetworkProtocolName());
        }

        // The publisher that the data will be published with
        final P publisher = (P) data.get();
        final List<InputSocket<?>> remainingSockets = provideRemainingInputSockets();
        publisher.setName(subField);
        performPublish(socketValue, publisher, remainingSockets);
    }

    /**
     * Performs the publish action. Provides the implementer with the resolved type of the socket being published.
     * @param socketValue The resolved socket value that has been provided as an input.
     * @param publisher The publisher to be used to publish the socket value
     * @param restOfInputSockets The remainder of the input sockets that were provided by {@link #provideRemainingInputSockets}
     */
    protected abstract void performPublish(S socketValue, P publisher, List<InputSocket<?>> restOfInputSockets);

    @Override
    public final void cleanUp(Optional<?> data) {
        final NetworkPublisher networkPublisher = (NetworkPublisher) data.get();
        networkPublisher.close();
    }

    /**
     * Creates a new publisher to be used for publishing data to the network service
     *
     * @return The publisher to be used.
     */
    protected abstract P createPublisher();

    /**
     * Provide any additional input sockets to be used in addition to the default ones
     */
    protected List<InputSocket<?>> provideRemainingInputSockets() {
        return Collections.emptyList();
    }

    /**
     * @return The network protocol's acronym (eg. ROS for Robot Operating System)
     */
    protected abstract String getNetworkProtocolNameAcronym();

    /**
     * @return The network protocol's name (eg. Robot Operating System)
     */
    protected abstract String getNetworkProtocolName();

    /**
     * @return The hint to indicate what you will be publishing to (eg. ROS to a Topic, Network Tables to a Table)
     */
    protected abstract String getSocketHintStringPrompt();
}
