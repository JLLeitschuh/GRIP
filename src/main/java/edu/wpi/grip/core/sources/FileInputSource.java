package edu.wpi.grip.core.sources;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.Socket;
import edu.wpi.grip.core.SocketHint;
import edu.wpi.grip.core.Source;
import org.bytedeco.javacpp.opencv_core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;


public class FileInputSource implements Source {
    private final SocketHint imageOutputHint = new SocketHint("Image", Mat.class);
    private final EventBus eventBus;

    public FileInputSource(EventBus eventBus){
        this.eventBus = eventBus;
    }

    @Override
    public Socket<Mat>[] getOutputSockets() {
        return new Socket[]{new Socket<>(this.eventBus, imageOutputHint)};
    }

    public void loadImage(URL imageURL){
        checkNotNull(imageURL);
        try {
            BufferedImage imageBuffer = ImageIO.read(imageURL);
            float [] imageFloatBuffer = imageBuffer.getData().getPixels(0, 0, imageBuffer.getWidth(), imageBuffer.getHeight(), (float [])null );
            //eventBus.
        } catch (IOException e) {
            //TODO Fix this so it tells the GUI there is a problem
            e.printStackTrace();
        }
    }
}
