package edu.wpi.grip.core.operations.composite;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.InputSocket;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OutputSocket;
import edu.wpi.grip.core.SocketHint;

import java.io.InputStream;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * An {@link Operation} that takes in a list of contours and outputs a list of any contours in the input that match
 * all of several criteria.  Right now, the user can specify a minimum area, minimum perimeter, and ranges for width
 * and height.
 * <p>
 * This is useful because running a Find Contours on a real-life image typically leads to many small undesirable
 * contours from noise and small objects, as well as contours that do not meet the expected characteristics of the
 * feature we're actually looking for.  So, this operation can help narrow them down.
 */
public class FilterContoursOperation implements Operation {

    private final SocketHint<ContoursReport> contoursHint = new SocketHint<ContoursReport>("Contours", ContoursReport.class, ContoursReport::new);
    private final SocketHint<Number> minAreaHint = new SocketHint<>("Min Area", Number.class, 0, SocketHint.View.SPINNER);
    private final SocketHint<Number> minPerimeterHint = new SocketHint<>("Min Perimeter", Number.class, 0,
            SocketHint.View.SPINNER);
    private final SocketHint<Number> minWidthHint = new SocketHint<>("Min Width", Number.class, 0,
            SocketHint.View.SPINNER);
    private final SocketHint<Number> maxWidthHint = new SocketHint<>("Max Width", Number.class, Integer.MAX_VALUE,
            SocketHint.View.SPINNER);
    private final SocketHint<Number> minHeightHint = new SocketHint<>("Min Height", Number.class, 0,
            SocketHint.View.SPINNER);
    private final SocketHint<Number> maxHeightHint = new SocketHint<>("Max Height", Number.class, Integer.MAX_VALUE,
            SocketHint.View.SPINNER);

    @Override
    public String getName() {
        return "Filter Contours";
    }

    @Override
    public String getDescription() {
        return "Find contours matching certain criteria.";
    }

    @Override
    public Optional<InputStream> getIcon() {
        return Optional.of(getClass().getResourceAsStream("/edu/wpi/grip/ui/icons/find-contours.png"));
    }

    @Override
    public InputSocket<?>[] createInputSockets(EventBus eventBus) {
        return new InputSocket<?>[]{
                new InputSocket<>(eventBus, contoursHint),
                new InputSocket<>(eventBus, minAreaHint),
                new InputSocket<>(eventBus, minPerimeterHint),
                new InputSocket<>(eventBus, minWidthHint),
                new InputSocket<>(eventBus, maxWidthHint),
                new InputSocket<>(eventBus, minHeightHint),
                new InputSocket<>(eventBus, maxHeightHint),
        };
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
        final double minArea = ((Number) inputs[1].getValue()).doubleValue();
        final double minPerimeter = ((Number) inputs[2].getValue()).doubleValue();
        final double minWidth = ((Number) inputs[3].getValue()).doubleValue();
        final double maxWidth = ((Number) inputs[4].getValue()).doubleValue();
        final double minHeight = ((Number) inputs[5].getValue()).doubleValue();
        final double maxHeight = ((Number) inputs[6].getValue()).doubleValue();

        final MatVector inputContours = inputSocket.getValue().getContours();
        final MatVector outputContours = (MatVector) data.get();
        outputContours.resize(inputContours.size());

        // Add contours from the input vector to the output vector only if they pass all of the criteria (minimum
        // area, minimum perimeter, width, and height)
        int filteredContourCount = 0;
        for (int i = 0; i < inputContours.size(); i++) {
            final Mat contour = inputContours.get(i);
            final Rect bb = boundingRect(contour);

            if (contourArea(contour) < minArea) continue;
            if (arcLength(contour, true) < minPerimeter) continue;
            if (bb.width() < minWidth || bb.width() > maxWidth) continue;
            if (bb.height() < minHeight || bb.height() > maxHeight) continue;

            outputContours.put(filteredContourCount++, contour);
        }

        outputContours.resize(filteredContourCount);

        final OutputSocket<ContoursReport> outputSocket = (OutputSocket<ContoursReport>) outputs[0];
        outputSocket.setValue(new ContoursReport(outputContours, inputSocket.getValue().getRows(), inputSocket.getValue().getCols()));
    }
}
