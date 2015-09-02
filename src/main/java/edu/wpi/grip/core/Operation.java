package edu.wpi.grip.core;

import com.google.common.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * The common interface used by <code>Step</code>s in a pipeline to call various operations.  There is usually only one
 * instance of any class that implements <code>Operation</code>, which is called whenever that operation is used.
 */
public abstract class Operation {

    /**
     * @param eventBus The Guava {@link EventBus} used by the application.
     * @return An array of sockets for the inputs that the operation expects.
     */
    abstract public Socket<?>[] createInputSockets(EventBus eventBus);

    /**
     * @param eventBus The Guava {@link EventBus} used by the application.
     * @return An array of sockets for the outputs that the operation produces.
     */
    abstract public Socket<?>[] createOutputSockets(EventBus eventBus);

    /**
     * Perform the operation on the specified inputs, storing the results in the specified outputs.
     *
     * @param inputs  An array obtained from {@link #createInputSockets(EventBus)}. The caller can set the value of
     *                each socket to an actual parameter for the operation.
     * @param outputs An array obtained from {@link #createOutputSockets(EventBus)}. The outputs of the operation will
     *                be stored in these sockets.
     */
    abstract public void perform(Socket<?>[] inputs, Socket<?>[] outputs);
}
