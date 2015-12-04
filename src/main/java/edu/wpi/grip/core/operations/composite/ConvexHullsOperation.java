package edu.wpi.grip.core.operations.composite;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.InputSocket;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OutputSocket;
import edu.wpi.grip.core.SocketHint;
import org.bytedeco.javacpp.opencv_core.MatVector;

import java.io.InputStream;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_imgproc.convexHull;

/**
 * An {@link Operation} that finds the convex hull of each of a list of contours.
 * <p>
 * This can help remove holes in detected shapes, making them easier to analyze.
 */
public class ConvexHullsOperation implements Operation {
    private final SocketHint<ContoursReport> contoursHint = new SocketHint<ContoursReport>("Contours", ContoursReport.class, ContoursReport::new);

    @Override
    public String getName() {
        return "Convex Hulls";
    }

    @Override
    public String getDescription() {
        return "Compute the convex hulls of contours.";
    }

    @Override
    public Optional<InputStream> getIcon() {
        return Optional.of(getClass().getResourceAsStream("/edu/wpi/grip/ui/icons/convex-hulls.png"));
    }

    @Override
    public InputSocket<?>[] createInputSockets(EventBus eventBus) {
        return new InputSocket<?>[]{new InputSocket<>(eventBus, contoursHint)};
    }

    @Override
    public OutputSocket<?>[] createOutputSockets(EventBus eventBus) {
        return new OutputSocket<?>[]{new OutputSocket<>(eventBus, contoursHint)};
    }

    @Override
    public Optional<MatVector> createData(){
        return Optional.of(new MatVector());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform(InputSocket<?>[] inputs, OutputSocket<?>[] outputs, Optional<?> data) {
        final InputSocket<ContoursReport> inputSocket = (InputSocket<ContoursReport>) inputs[0];
        final OutputSocket<ContoursReport> outputSocket = (OutputSocket<ContoursReport>) outputs[0];

        final ContoursReport inputContours = inputSocket.getValue();
        final MatVector outputContourVector = (MatVector) data.get();
        outputContourVector.resize(inputContours.getContours().size());

        for (int i = 0; i < inputContours.getContours().size(); i++) {
            convexHull(inputContours.getContours().get(i), outputContourVector.get(i));
        }

        outputSocket.setValue(new ContoursReport(outputContourVector, inputContours.getRows(), inputContours.getCols()));
    }
}
