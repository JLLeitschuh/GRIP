package edu.wpi.grip.core.operations.opencv;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.Socket;
import edu.wpi.grip.core.SocketHint;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * {@link blur(Mat, Mat, Size, Point, int)}
 */
public class BlurOperation implements Operation {
    final private SocketHint<Mat> srcHint = new SocketHint("src", Mat.class);
    final private SocketHint<Mat> destination = new SocketHint("dst", Mat.class);
    final private SocketHint<Size> sizeHint = new SocketHint("ksize", Size.class);
    final private SocketHint<Point> pointHint = new SocketHint("anchor", Point.class, null, null, new Point(-1, 1));
    final private SocketHint<Integer> borderTypeHint = new SocketHint("borderType", Integer.class, SocketHint.View.SELECT, null, opencv_core.BORDER_DEFAULT);



    @Override
    public Socket<?>[] createInputSockets(EventBus eventBus) {
        return new Socket[]{new Socket(eventBus, srcHint), new Socket(eventBus, sizeHint), new Socket(eventBus, pointHint), };
    }

    @Override
    public Socket<?>[] createOutputSockets(EventBus eventBus) {
        return new Socket[]{new Socket(eventBus, destination)};
    }

    @Override
    public void perform(Socket<?>[] inputs, Socket<?>[] outputs) {
        //Socket
        //opencv_imgproc.blur();
    }
}
