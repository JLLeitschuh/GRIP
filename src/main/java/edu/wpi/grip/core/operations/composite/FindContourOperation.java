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
 * An {@link Operation} that, given a binary image, produces a list of contours of all of the shapes in the image
 */
public class FindContourOperation implements Operation {

    private final SocketHint<Mat> inputHint = new SocketHint<Mat>("Input", Mat.class, Mat::new);
    private final SocketHint<Boolean> externalHint = new SocketHint<>("External Only", Boolean.class, false, SocketHint.View.CHECKBOX);
    private final SocketHint<ContoursReport> contoursHint = new SocketHint<ContoursReport>("Contours", ContoursReport.class, ContoursReport::new);

    @Override
    public String getName() {
        return "Find Contours";
    }

    @Override
    public String getDescription() {
        return "Detect contours in a binary image.";
    }

    @Override
    public Optional<InputStream> getIcon() {
        return Optional.of(getClass().getResourceAsStream("/edu/wpi/grip/ui/icons/find-contours.png"));
    }

    @Override
    public InputSocket<?>[] createInputSockets(EventBus eventBus) {
        return new InputSocket<?>[]{
                new InputSocket<>(eventBus, inputHint),
                new InputSocket<>(eventBus, externalHint),
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets(EventBus eventBus) {
        return new OutputSocket<?>[]{new OutputSocket<>(eventBus, contoursHint)};
    }

    @Override
    public Optional<?> createData() {
        return Optional.of(new Object[]{new Mat(), new MatVector()});
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform(InputSocket<?>[] inputs, OutputSocket<?>[] outputs, Optional<?> data) {
        final Mat input = ((InputSocket<Mat>) inputs[0]).getValue();
        final Mat tmp = (Mat) ((Optional<Object[]>) data).get()[0];
        final MatVector outputContourVector = (MatVector) ((Optional<Object[]>) data).get()[1];
        final boolean externalOnly = ((InputSocket<Boolean>) inputs[1]).getValue();
        final OutputSocket<ContoursReport> contoursSocket = (OutputSocket<ContoursReport>) outputs[0];

        if (input.empty()) {
            return;
        }

        // findContours modifies its input, so we pass it  a temporary copy of the input image
        input.copyTo(tmp);
        findContours(tmp, outputContourVector, externalOnly ? CV_RETR_EXTERNAL : CV_RETR_LIST,
                CV_CHAIN_APPROX_TC89_KCOS);

        contoursSocket.setValue(new ContoursReport(outputContourVector, input.rows(), input.cols()));
    }
}
