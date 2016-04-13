package edu.wpi.grip.core.operations.opencv.templated;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;

public class OneSourceOneDestinationOperation<T1, R> implements Operation {
    private final InputSocket<T1> input1;
    private final OutputSocket<R> output;
    private final Performer<T1, R> performer;

    @FunctionalInterface
    public interface Performer<T1, R> {
        void perform(T1 src1, R dst);
    }

    public OneSourceOneDestinationOperation(InputSocket.Factory inputSocketFactory,
                                            OutputSocket.Factory outputSocketFactory,
                                            Performer<T1, R> performer,
                                            SocketHint<T1> t1SocketHint,
                                            SocketHint<R> rSocketHint) {
        this.input1 = inputSocketFactory.create(t1SocketHint);
        this.output = outputSocketFactory.create(rSocketHint);
        this.performer = performer;
    }

    public OneSourceOneDestinationOperation(InputSocket.Factory inputSocketFactory,
                                            OutputSocket.Factory outputSocketFactory,
                                            Performer<T1, R> performer,
                                            Class<T1> t1,
                                            Class<R> r) {
        this(inputSocketFactory,
                outputSocketFactory,
                performer,
                new SocketHint.Builder<>(t1).identifier("src1").build(),
                new SocketHint.Builder<>(r).identifier("dst").build());
    }


    @Override
    public OperationDescription getDescription() {
        return null;
    }

    @Override
    public InputSocket<?>[] getInputSockets() {
        return new InputSocket<?>[] {
                input1
        };
    }

    @Override
    public OutputSocket<?>[] getOutputSockets() {
        return new OutputSocket<?>[] {
                output
        };
    }

    @Override
    public void perform() {
        performer.perform(input1.getValue().get(), output.getValue().get());
        output.setValue(output.getValue().get());
    }

}
