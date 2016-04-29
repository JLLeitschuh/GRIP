package edu.wpi.grip.ui.util;

import edu.wpi.grip.core.Connection;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.Step;

/**
 * Creates CSS style classes and ID's for nodes.  This makes it possible to use CSS selectors to retrieve nodes in unit
 * tests.
 */
public final class StyleClassNameUtility {

    private StyleClassNameUtility() {
    }


    /**
     * Returns the CSS id name for an operation
     *
     * @param operation The operation to get the class name for
     * @return The CSS id for the operation. To use as a css selector then prepend the string with a '#'
     */
    public static String idNameFor(Operation operation) {
        return shortNameFor(operation).append("-operation").toString();
    }

    /**
     * Returns the CSS id name for an operation
     *
     * @param operationDescription the description of the operation
     * @return The CSS id for the operation. To use as a css selector then prepend the string with a '#'
     */
    public static String idNameFor(OperationDescription operationDescription) {
        return shortNameFor(operationDescription).append("-operation").toString();
    }

    /**
     * Return the CSS class name for a step
     *
     * @param step The step to get the class name for
     * @return The CSS class for the step in the pipeline. To use as a css selector then prepend the string with a '.'
     */
    public static String classNameFor(Step step) {
        return classNameForStepHolding(step.getOperation());
    }

    /**
     * Return the CSS Class name for a {@link Step} that holds this {@link Operation}
     *
     * @param operation The operation to get the step's class name for
     * @return The CSS class for this step. To use as a css selector then prepend the string with a '.'
     */
    public static String classNameForStepHolding(Operation operation) {
        return shortNameFor(operation).append("-step").toString();
    }

    public static String cssSelectorForOutputSocketHandleOnStepHolding(Operation operation) {
        return ".pipeline ." + classNameForStepHolding(operation) + " .socket-handle.output";
    }

    public static String cssSelectorForInputSocketHandleOnStepHolding(Operation operation) {
        return ".pipeline ." + classNameForStepHolding(operation) + " .socket-handle.input";
    }


    public static String cssSelectorForOutputSocketHandleOn(Step step) {
        return cssSelectorForOutputSocketHandleOnStepHolding(step.getOperation());
    }

    public static String cssSelectorForInputSocketHandleOn(Step step) {
        return cssSelectorForInputSocketHandleOnStepHolding(step.getOperation());
    }

    /**
     * Return the CSS class name for a connection.
     *
     * @param connection The connection to get the class name for.
     * @return The CSS class for the connection in the pipeline. To use as a css selector then prepend the string with a '.'
     */
    public static String classNameFor(Connection connection) {
        return "connection-" +
                connection.getOutputSocket().getSocketHint().getIdentifier()
                + "-to-" +
                connection.getInputSocket().getSocketHint().getIdentifier();
    }

    private static StringBuilder shortNameFor(OperationDescription operationDescription) {
        return new StringBuilder(operationDescription.getName().toLowerCase().replace(" ", "-"));
    }

    private static StringBuilder shortNameFor(Operation operation) {
        return new StringBuilder(operation.getDescription().getName().toLowerCase().replace(" ", "-"));
    }
}
