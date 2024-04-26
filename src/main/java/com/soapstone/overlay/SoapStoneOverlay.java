package com.soapstone.overlay;

import static java.util.Objects.isNull;
import static net.runelite.api.Perspective.getCanvasTextLocation;
import static net.runelite.api.Perspective.getCanvasTilePoly;
import static net.runelite.client.ui.overlay.OverlayUtil.renderTextLocation;

import com.soapstone.SoapStonePlugin;
import com.soapstone.constants.SoapStoneOverlayConstants;
import com.soapstone.domain.SoapStoneTile;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class SoapStoneOverlay extends Overlay {

  private final Client client;
  private final SoapStonePlugin plugin;

  @Inject
  public SoapStoneOverlay(final Client client, final SoapStonePlugin plugin) {
    this.client = client;
    this.plugin = plugin;
    setPosition(OverlayPosition.DYNAMIC);
    setPriority(Float.MIN_VALUE);
    setLayer(OverlayLayer.ABOVE_SCENE);
  }

  @Override
  public Dimension render(final Graphics2D graphics) {
    if (this.plugin.getSoapStoneTiles().isEmpty()) {
      return null;
    }

    final List<SoapStoneTile> tiles = this.plugin.getSoapStoneTiles().all();

    tiles.forEach((tile) -> this.renderTile(tile, graphics));

    return null;
  }

  private void renderTile(final SoapStoneTile soapStoneTile, final Graphics2D graphics) {
    final WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
    final WorldPoint soapStoneWorldPoint = soapStoneTile.getWorldPoint();

    if (this.messageIsBlank(soapStoneTile.getMessage())) {
      return;
    }

    final String message = soapStoneTile.getMessage() + " - " + soapStoneTile.getUsername();

    if (this.soapStoneIsOutsideDrawDistance(soapStoneWorldPoint, playerLocation)) {
      return;
    }

    if (this.messageIsTooLong(message)) {
      return;
    }

    final LocalPoint localPoint = LocalPoint.fromWorld(this.client, soapStoneWorldPoint);

    this.renderPolygon(graphics, localPoint);
    this.renderText(graphics, localPoint, message, soapStoneWorldPoint.getPlane());
  }

  private void renderPolygon(final Graphics2D graphics, final LocalPoint localPoint) {
    if (isNull(graphics) || isNull(localPoint)) {
      return;
    }

    final Polygon poly = getCanvasTilePoly(client, localPoint);

    if (isNull(poly)) {
      return;
    }

    OverlayUtil.renderPolygon(graphics, poly, SoapStoneOverlayConstants.ORANGE);
  }

  private void renderText(
      final Graphics2D graphics,
      final LocalPoint localPoint,
      final String message,
      final int plane
  ) {
    final Point canvasTextLocation = getCanvasTextLocation(
        client, graphics, localPoint, message, plane
    );
    renderTextLocation(
        graphics, canvasTextLocation, message, SoapStoneOverlayConstants.ORANGE
    );
  }

  private boolean messageIsBlank(final String message) {
    return StringUtils.isBlank(message);
  }

  private boolean soapStoneIsOutsideDrawDistance(final WorldPoint worldPoint,
      final WorldPoint playerLocation) {
    return worldPoint.distanceTo(playerLocation) > SoapStoneOverlayConstants.MAX_DRAW_DISTANCE;
  }

  private boolean messageIsTooLong(final String message) {
    return SoapStoneOverlayConstants.MAX_MESSAGE_LENGTH <= message.length();
  }
}
