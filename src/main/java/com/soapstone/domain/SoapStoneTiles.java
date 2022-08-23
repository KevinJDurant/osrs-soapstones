package com.soapstone.domain;

import java.util.ArrayList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

public final class SoapStoneTiles {
  private final List<SoapStoneTile> tiles = new ArrayList<>();

  public void add(final SoapStoneTile tile) {
    this.tiles.add(tile);
  }

  public void addAll(final List<SoapStoneTile> tiles) {
    this.tiles.addAll(tiles);
  }

  public List<SoapStoneTile> all() {
    return new ArrayList<>(this.tiles);
  }

  public boolean isEmpty() {
    return this.tiles.isEmpty();
  }

  public boolean hasASoapStoneAtWorldPoint(final WorldPoint worldPoint) {
    return this.tiles.stream()
        .anyMatch(tile -> tile.isAtWorldPoint(worldPoint));
  }

  public boolean doesPlayerOwnSoapStoneAtWorldPoint(final WorldPoint worldPoint, final String playerName) {
    return this.tiles.stream()
        .anyMatch(tile -> tile.isAtWorldPoint(worldPoint) && tile.getUsername().equals(playerName));
  }

  public void clear() {
    this.tiles.clear();
  }
}
