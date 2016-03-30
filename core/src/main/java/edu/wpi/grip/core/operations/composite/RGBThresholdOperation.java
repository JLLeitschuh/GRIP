package edu.wpi.grip.core.operations.composite;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.sockets.*;

import java.util.List;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * An {@link Operation} that converts a color image into a binary image based on threshold ranges for each channel
 */
public class RGBThresholdOperation extends ThresholdOperation {
    private final SocketHint<Mat> inputHint = SocketHints.Inputs.createMatSocketHint("Input", false);
    private final SocketHint<List<Number>> redHint = SocketHints.Inputs.createNumberListRangeSocketHint("Red", 0.0, 255.0);
    private final SocketHint<List<Number>> greenHint = SocketHints.Inputs.createNumberListRangeSocketHint("Green", 0.0, 255.0);
    private final SocketHint<List<Number>> blueHint = SocketHints.Inputs.createNumberListRangeSocketHint("Blue", 0.0, 255.0);

    private final SocketHint<Mat> outputHint = SocketHints.Outputs.createMatSocketHint("Output");

    @Override
    public String getName() {
        return "RGB Threshold";
    }

    @Override
    public String getDescription() {
        return "Segment an image based on color ranges.";
    }

    @Override
    public InputSocket<?>[] createInputSockets(EventBus eventBus) {
        return new InputSocket<?>[]{
                new InputSocket<>(eventBus, inputHint),
                new InputSocket<>(eventBus, redHint),
                new InputSocket<>(eventBus, greenHint),
                new InputSocket<>(eventBus, blueHint),
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets(EventBus eventBus) {
        return new OutputSocket<?>[]{
                new OutputSocket<>(eventBus, outputHint)
        };
    }

    @Override
    public Optional<Mat[]> createData() {
        return Optional.of(new Mat[]{new Mat(), new Mat()});
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform(InputSocket<?>[] inputs, OutputSocket<?>[] outputs, Optional<?> data) {
        final Mat[] dataArray = (Mat[]) data.orElseThrow(() -> new IllegalStateException("Data was not provided"));

        final Mat input = inputHint.retrieveValue(inputs[0]);
        final List<Number> channel1 = redHint.retrieveValue(inputs[1]);
        final List<Number> channel2 = greenHint.retrieveValue(inputs[2]);
        final List<Number> channel3 = blueHint.retrieveValue(inputs[3]);

        if (input.channels() != 3) {
            throw new IllegalArgumentException("RGB Threshold needs a 3-channel input");
        }

        final Socket<Mat> outputSocket = outputHint.safeCastSocket(outputs[0]);
        final Mat output = outputHint.retrieveValue(outputSocket);


        final Scalar lowScalar = new Scalar(
                channel3.get(0).doubleValue(),
                channel2.get(0).doubleValue(),
                channel1.get(0).doubleValue(), 0);

        final Scalar highScalar = new Scalar(
                channel3.get(1).doubleValue(),
                channel2.get(1).doubleValue(),
                channel1.get(1).doubleValue(), 0);

        final Mat low = reallocateMatIfInputSizeOrWidthChanged(dataArray, 0, lowScalar, input);
        final Mat high = reallocateMatIfInputSizeOrWidthChanged(dataArray, 1, highScalar, input);


        inRange(input, low, high, output);

        outputSocket.setValue(output);
    }
}
