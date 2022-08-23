package com.soapstone.domain;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public final class SoapStoneTile {
  @Getter private final String message;
  @Getter private final String username;
  @Getter private final WorldPoint worldPoint;

  public SoapStoneTile(final String message, final String username, final WorldPoint worldPoint) {
    this.message = message;
    this.username = username;
    this.worldPoint = worldPoint;
  }

  @Override
  public String toString() {
    return "SoapStoneTile[" + this.username + ": " + this.message + "@{" + this.worldPoint.getX() + "," + this.worldPoint.getY() + "," + this.worldPoint.getPlane() + "}]";
  }

  public boolean isAtWorldPoint(final WorldPoint worldPoint) {
    return this.worldPoint.getX() == worldPoint.getX() && this.worldPoint.getY() == worldPoint.getY() && this.worldPoint.getPlane() == worldPoint.getPlane();
  }
}
