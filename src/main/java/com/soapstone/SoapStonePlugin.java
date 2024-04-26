package com.soapstone;

import com.google.inject.Provides;
import com.soapstone.config.SoapStoneConfig;
import com.soapstone.domain.SoapStoneTiles;
import com.soapstone.overlay.SoapStoneOverlay;
import com.soapstone.usecase.CreateSoapStoneMenuEntry;
import com.soapstone.usecase.GetSoapStones;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "SoapStones",
    description = "Leave cross-world messages for other players.",
    tags = {"social"}
)
@Slf4j
public class SoapStonePlugin extends Plugin {

  @Getter
  final SoapStoneTiles soapStoneTiles = new SoapStoneTiles();

  @Inject private GetSoapStones getSoapStones;
  @Inject private OverlayManager overlayManager;
  @Inject private ScheduledExecutorService executor;
  @Inject private SoapStoneOverlay soapStoneOverlay;
  @Inject private CreateSoapStoneMenuEntry createSoapStoneMenuEntry;

  public SoapStonePlugin() {}

  @Subscribe
  public void onGameStateChanged(final GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
      return;
    }
    this.fetchSoapStones();
  }

  @Override
  protected void shutDown() throws Exception {
    this.soapStoneTiles.clear();
  }

  @Override
  protected void startUp() throws Exception {
    this.soapStoneTiles.clear();
    overlayManager.add(this.soapStoneOverlay);
    executor.scheduleAtFixedRate(this::fetchSoapStones, 1L, 1L, TimeUnit.MINUTES);
  }

  @Subscribe
  public void onMenuEntryAdded(final MenuEntryAdded event) {
    this.createSoapStoneMenuEntry.execute(event);
  }

  @Provides
  final SoapStoneConfig provideSoapStoneConfig(final ConfigManager configManager) {
    return configManager.getConfig(SoapStoneConfig.class);
  }

  private void fetchSoapStones() {
    this.soapStoneTiles.clear();
    this.getSoapStones.execute();
  }
}
