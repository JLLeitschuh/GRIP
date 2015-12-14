package edu.wpi.grip.core.sources;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import edu.wpi.grip.core.OutputSocket;
import edu.wpi.grip.core.SocketHint;
import edu.wpi.grip.core.SocketHints;
import edu.wpi.grip.core.util.ImageLoadingUtility;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Provides a way to generate a {@link Mat} from an image on the filesystem.
 */
@XStreamAlias(value = "grip:ImageFile")
public final class ImageFileSource extends LoadableSource {

    private static final String PATH_PROPERTY = "path";

    private final String name;
    private final String path;
    private final SocketHint<Mat> imageOutputHint = SocketHints.Inputs.createMatSocketHint("Image", true);
    private final OutputSocket<Mat> outputSocket;
    private final EventBus eventBus;
    private boolean loaded = false;

    public interface Factory {
        ImageFileSource create(File file);
        ImageFileSource create(Properties properties);
    }

    /**
     * @param eventBus The event bus for the pipeline.
     * @param file     The location on the file system where the image exists.
     */
    @AssistedInject
    ImageFileSource(EventBus eventBus, @Assisted File file) {
        this(eventBus, URLDecoder.decode(Paths.get(file.toURI()).toString()));
    }


    @AssistedInject
    ImageFileSource(EventBus eventBus, @Assisted Properties properties) {
        this(eventBus, properties.getProperty(PATH_PROPERTY));
    }

    private ImageFileSource(EventBus eventBus, String path) {
        this.eventBus = checkNotNull(eventBus, "Event Bus was null.");
        this.path = checkNotNull(path, "Path can not be null");
        this.name = Files.getNameWithoutExtension(this.path);
        this.outputSocket = new OutputSocket<>(eventBus, imageOutputHint);
    }

    /**
     * Performs the loading of the image from the file system
     * @throws IOException If the image fails to load from the filesystem
     */
    @Override
    public void load() throws IOException {
        this.loadImage(path);
        this.loaded = true;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public OutputSocket[] createOutputSockets() {
        if(!loaded) {
            throw new IllegalStateException("The images were never loaded from the filesystem. Must call `loadImage` first.");
        }
        return new OutputSocket[]{this.outputSocket};
    }

    @Override
    public Properties getProperties() {
        final Properties properties = new Properties();
        properties.setProperty(PATH_PROPERTY, this.path);
        return properties;
    }

    /**
     * Loads the image and posts an update to the {@link EventBus}
     *
     * @param path The location on the file system where the image exists.
     */
    private void loadImage(String path) throws IOException {
        this.loadImage(path, opencv_imgcodecs.IMREAD_COLOR);
    }


    private void loadImage(String path, final int flags) throws IOException {
        ImageLoadingUtility.loadImage(path, flags, this.outputSocket.getValue().get());
        this.outputSocket.setValue(this.outputSocket.getValue().get());
    }
}
