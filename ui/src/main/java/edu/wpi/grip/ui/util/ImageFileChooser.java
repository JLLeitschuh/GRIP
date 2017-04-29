package edu.wpi.grip.ui.util;


import javafx.stage.FileChooser;

public class ImageFileChooser {
  /**
   * Create an image file chooser with all of the supported input file types configured.
   */
  public static FileChooser create() {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open an image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files",
            "*.bmp", "*.dib",           // Windows bitmaps
            "*.jpeg", "*.jpg", "*.jpe", // JPEG files
            "*.jp2",                    // JPEG 2000 files
            "*.png",                    // Portable Network Graphics
            "*.webp",                   // WebP
            "*.pbm", "*.pgm", "*.ppm",  // Portable image format
            "*.sr", "*.ras",            // Sun rasters
            "*.tiff", "*.tif"           // TIFF files
        ),
        new FileChooser.ExtensionFilter("All Files", "*.*"));
    return fileChooser;
  }
}
