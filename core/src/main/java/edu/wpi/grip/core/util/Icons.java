package edu.wpi.grip.core.util;

import java.io.InputStream;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for fetching icon streams.
 */
public final class Icons {

    private static final Logger logger = Logger.getLogger(Icons.class.getName());

    /**
     * Gets an image stream for an icon.
     *
     * @param path the directory where the icon is located
     * @param name the name of the icon
     * @param type the type of the icon (".png", ".jpg", etc.)
     * @return a stream for the given icon, or {@code null} if no image by that name exists
     */
    public static InputStream iconStream(String path, String name, String type) {
        checkNotNull(path);
        checkNotNull(name);
        checkNotNull(type);
        return Icons.class.getResourceAsStream(path + name + type);
    }


    /**
     * Gets an image stream for an icon.
     *
     * @param name the name of the icon
     * @param type the type of the icon (".png", ".jpg", etc.)
     * @return a stream for the given icon, or {@code null} if no image by that name exists
     */
    public static InputStream iconStream(String name, String type) {
        return iconStream("/edu/wpi/grip/ui/icons/", name, type);
    }

    /**
     * Gets an image stream for an icon.
     *
     * @param name the name of the icon
     * @return a stream for the given icon, or {@code null} if no image by that name exists
     */
    public static InputStream iconStream(String name) {
        return iconStream(name, ".png");
    }

}
