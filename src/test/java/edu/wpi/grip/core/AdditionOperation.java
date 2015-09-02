package edu.wpi.grip.core;

import com.google.common.eventbus.EventBus;

public class AdditionOperation extends Operation {
    private SocketHint<Double>
            aHint = new SocketHint<>("a", Double.class),
            bHint = new SocketHint<>("b", Double.class),
            cHint = new SocketHint<>("b", Double.class);

    @Override
    public Socket[] createInputSockets(EventBus eventBus) {
        return new Socket[]{new Socket<>(eventBus, aHint), new Socket<>(eventBus, bHint)};
    }

    @Override
    public Socket[] createOutputSockets(EventBus eventBus) {
        return new Socket[]{new Socket<>(eventBus, cHint)};
    }

    @Override
    public void perform(Socket[] inputs, Socket[] outputs) {
        Socket<Double> a = inputs[0], b = inputs[1], c = outputs[0];

        c.setValue(a.getValue() + b.getValue());
    }
}
