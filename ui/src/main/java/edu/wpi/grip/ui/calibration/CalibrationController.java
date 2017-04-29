package edu.wpi.grip.ui.calibration;

import edu.wpi.grip.core.util.ImageLoadingUtility;
import edu.wpi.grip.ui.util.ImageConverter;
import edu.wpi.grip.ui.util.ImageFileChooser;

import com.google.common.eventbus.EventBus;

import org.apache.commons.lang.math.IntRange;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.TermCriteria;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bytedeco.javacpp.opencv_calib3d.drawChessboardCorners;
import static org.bytedeco.javacpp.opencv_calib3d.findChessboardCorners;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_EPS;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cornerSubPix;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

public class CalibrationController {
  @Inject
  private EventBus eventBus;
  @FXML
  private Parent root;
  @FXML
  private HBox calibrationOutput;


  @FXML
  private void openImageAction() throws IOException {
    final FileChooser fileChooser = ImageFileChooser.create();
    final List<File> imageFiles = fileChooser.showOpenMultipleDialog(root.getScene().getWindow());
    if (imageFiles != null) {
      processFiles(imageFiles.stream().map(File::getPath).collect(Collectors.toSet()));
    }
  }


  @FXML
  private void openExampleImages() throws IOException {
    final Collection<String> imagePaths = Arrays.stream(new IntRange(1, 14).toArray())
        .filter(numb -> numb != 10)
        .mapToObj(Integer::toString)
        .map(numString -> numString.length() == 1 ? "0" + numString : numString)
        .map(numString -> "/opencv/left" + numString + ".jpg")
        .map(numString -> {
          final URL url = CalibrationController.class.getResource(numString);
          return checkNotNull(url, "Path: " + numString + " does not exits.");
        })
        .map(URL::getPath)
        .collect(Collectors.toSet());
    try {
      processFiles(imagePaths);
    } catch (RuntimeException ex) {
      ex.printStackTrace();
    }

  }

  private void processFiles(final Collection<String> imagePaths) throws IOException {
    final Mat corners = new Mat();
    final Size patternSize = new Size(7, 6);
    for (final String imagePath : imagePaths) {
      final Mat imageMat = new Mat();
      ImageLoadingUtility.loadImage(imagePath, imageMat);
      final Mat greyImageMat = new Mat();
      cvtColor(imageMat, greyImageMat, COLOR_BGR2GRAY);
      boolean patternFound = findChessboardCorners(greyImageMat, patternSize, corners);
      if (patternFound) {
        cornerSubPix(greyImageMat, corners, new Size(11, 11), new Size(-1, -1),
            new TermCriteria(CV_TERMCRIT_EPS + CV_TERMCRIT_ITER, 30, 0.1));
        drawChessboardCorners(imageMat, patternSize, corners, true);
        final Image image = new ImageConverter().convert(imageMat, imageMat.rows());
        calibrationOutput.getChildren().add(new ImageView(image));
      }
    }
  }
}
