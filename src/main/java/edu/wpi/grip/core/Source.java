package edu.wpi.grip.core;

public interface Source {
    /**
     * @return An array of {@link Socket}s that hold the outputs of this source
     */
    Socket<?>[] getOutputSockets();
}
