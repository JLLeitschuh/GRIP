package edu.wpi.grip.core.operations.composite;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * The output of {@link FindContourOperation}.  This stores a list of contours (which is basically a list of points) in
 * OpenCV objects, as well as the width and height of the image that the contours are from, to give context to the
 * points.
 */
public final class ContoursReport {
    protected int rows = -1, cols = -1;
    protected MatVector contours = new MatVector();

    public MatVector getContours() {
        return this.contours;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }
}
