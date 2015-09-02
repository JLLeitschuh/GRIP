package edu.wpi.grip.core.operations.opencv;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.Socket;
import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddOperationTest {
    EventBus eventBus = new EventBus();
    Operation addition = new AddOperation();

    private boolean isMatEqual(Mat mat1, Mat mat2){
        // treat two empty mat as identical as well
        if (mat1.empty() && mat2.empty()) {
            return true;
        }
        // if dimensionality of two mat is not identical, these two mat is not identical
        if (mat1.cols() != mat2.cols() || mat1.rows() != mat2.rows() || mat1.dims() != mat2.dims()) {
            return false;
        }
        Mat diff = new Mat();
        opencv_core.compare(mat1, mat2, diff, opencv_core.CMP_NE);
        int nz = opencv_core.countNonZero(diff);
        return nz==0;
    }

    @Test
    public void testOperation() throws Exception {
        // Given
        Socket[] inputs = addition.createInputSockets(eventBus);
        Socket[] outputs = addition.createOutputSockets(eventBus);
        Socket<Mat> a = inputs[0], b = inputs[1];
        Socket<MatExpr> c = outputs[0];

        int sz[] = {100, 100, 100};

        a.setValue(new Mat (3, sz, CV_8U, Scalar.all(1)));
        b.setValue(new Mat (3, sz, CV_8U, Scalar.all(2)));

        //When
        addition.perform(inputs, outputs);

        //Then
        Mat expectedResult = new Mat (3, sz, CV_8U, Scalar.all(3));
        assertTrue(isMatEqual(c.getValue().asMat(), expectedResult));
    }
}