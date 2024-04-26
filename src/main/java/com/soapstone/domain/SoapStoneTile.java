package com.soapstone.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
@ToString
public final class SoapStoneTile {

  private final String message;
  private final String username;
  private final WorldPoint worldPoint;

  public boolean isAtWorldPoint(final WorldPoint worldPoint) {
    return this.worldPoint.getX() == worldPoint.getX()
        && this.worldPoint.getY() == worldPoint.getY()
        && this.worldPoint.getPlane() == worldPoint.getPlane();
  }
}
