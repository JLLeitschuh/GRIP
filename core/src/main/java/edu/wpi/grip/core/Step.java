package edu.wpi.grip.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import edu.wpi.grip.core.events.SocketChangedEvent;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A step is an instance of an operation in a pipeline.  A step contains a list of input and output sockets, and it
 * runs the operation whenever one of the input sockets changes.
 */
@XStreamAlias(value = "grip:Step")
public class Step {

    private Operation operation;
    private InputSocket<?>[] inputSockets;
    private OutputSocket<?>[] outputSockets;
    private Optional<?> data;

    interface Factory {
        Step create(Operation operation);
    }

    /**
     * @param eventBus  The Guava {@link EventBus} used by the application.
     * @param operation The operation that is performed at this step.
     */
    @Inject
    public Step(EventBus eventBus, @Assisted Operation operation) {
        this.operation = operation;

        checkNotNull(eventBus);
        checkNotNull(operation);

        // Create the list of input and output sockets, and mark this step as their owner.
        inputSockets = operation.createInputSockets(eventBus);
        for (Socket<?> socket : inputSockets) {
            socket.setStep(Optional.of(this));
            eventBus.register(socket);
        }

        outputSockets = operation.createOutputSockets(eventBus);
        for (Socket<?> socket : outputSockets) {
            socket.setStep(Optional.of(this));
            eventBus.register(socket);
        }

        data = operation.createData();

        runPerformIfPossible();
    }

    /**
     * @return The underlying <code>Operation</code> that this step performs
     */
    public Operation getOperation() {
        return this.operation;
    }

    /**
     * @return An array of <code>Socket</code>s that hold the inputs to this step
     */
    public InputSocket<?>[] getInputSockets() {
        return inputSockets;
    }

    /**
     * @return An array of <code>Socket</code>s that hold the outputs of this step
     */
    public OutputSocket<?>[] getOutputSockets() {
        return outputSockets;
    }

    /**
     * Resets all {@link OutputSocket OutputSockets} to their initial value.
     * Should only be used by {@link Step#runPerformIfPossible()}
     */
    private void resetOutputSockets() {
        for (OutputSocket<?> outputSocket : outputSockets) {
            outputSocket.resetValueToInitial();
        }
    }

    /**
     * The {@link Operation#perform} method should only be called if all {@link InputSocket#getValue()} are not empty.
     * If one input is invalid then the perform method will not run and all output sockets will be assigned to their
     * default values.
     */
    private synchronized void runPerformIfPossible() {
        try {
            for (InputSocket<?> inputSocket : inputSockets) {
                inputSocket.getValue()
                        .orElseThrow(() -> new NoSuchElementException(
                                inputSocket.getSocketHint().getIdentifier() + " must have a value to run this step."
                        ));
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            resetOutputSockets();
            return; /* Only run the perform method if all of the input sockets are present. */
        }

        try {
            this.operation.perform(inputSockets, outputSockets, data);
        } catch (Exception e) {
            e.printStackTrace();
            resetOutputSockets();
        }
    }

    @Subscribe
    public void onInputSocketChanged(SocketChangedEvent e) {
        final Socket<?> socket = e.getSocket();

        // If this socket that changed is one of the inputs to this step, run the operation with the new value.
        if (socket.getStep().equals(Optional.of(this)) && socket.getDirection().equals(Socket.Direction.INPUT)) {
            runPerformIfPossible();
        }
    }
}
