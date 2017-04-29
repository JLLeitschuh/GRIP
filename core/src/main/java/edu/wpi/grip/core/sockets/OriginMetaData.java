package edu.wpi.grip.core.sockets;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalLong;

public final class OriginMetaData {
  private static final OriginMetaData EMPTY = new OriginMetaData(OptionalLong.empty());
  private final OptionalLong creationTime;

  private OriginMetaData(OptionalLong creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * @return The time that the frame was originally retrieved from the camera.
   */
  public OptionalLong getCameraMatCreationTime() {
    return creationTime;
  }

  public static OriginMetaData of(long creationTime) {
    return new OriginMetaData(OptionalLong.of(creationTime));
  }

  public static OriginMetaData empty() {
    return EMPTY;
  }

  /**
   * @param datas The various data objects to calculate the survivor from.
   * @return The OriginMetaData that should survive and be passed on.
   */
  public static Optional<OriginMetaData> calculateSurvivor(Collection<OriginMetaData> datas) {
    for (final OriginMetaData originMetaData : datas) {
      if (originMetaData.creationTime.isPresent()) {
        return Optional.of(originMetaData);
      }
    }
    return Optional.empty();
  }
}
