package com.soapstone;

import com.google.inject.Provides;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.soapstone.config.SoapStoneConfig;
import com.soapstone.domain.SoapStoneTiles;
import com.soapstone.overlay.SoapStoneOverlay;
import com.soapstone.usecase.CreateSoapStoneMenuEntry;
import com.soapstone.usecase.GetSoapStones;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Soapstones",
    description = "Leave messages for other players",
    tags = {"social"}
)
@Slf4j
public class SoapStonePlugin extends Plugin {
  final SoapStoneTiles soapStoneTiles = new SoapStoneTiles();

  private final SoapStoneConfig config;
  private final GetSoapStones getSoapStones;
  private final ConfigManager configManager;
  private final OverlayManager overlayManager;
  private final ScheduledExecutorService executor;
  private final SoapStoneOverlay soapStoneOverlay;
  private final CreateSoapStoneMenuEntry createSoapStoneMenuEntry;

  @Inject
  public SoapStonePlugin(
      final OverlayManager overlayManager,
      final ConfigManager configManager,
      final GetSoapStones getSoapStones,
      final SoapStoneOverlay soapStoneOverlay,
      final CreateSoapStoneMenuEntry createSoapStoneMenuEntry,
      final ScheduledExecutorService executor,
      final SoapStoneConfig config) {
    this.overlayManager = overlayManager;
    this.configManager = configManager;
    this.getSoapStones = getSoapStones;
    this.soapStoneOverlay = soapStoneOverlay;
    this.createSoapStoneMenuEntry = createSoapStoneMenuEntry;
    this.executor = executor;
    this.config = config;
  }

  @Subscribe
  public void onGameStateChanged(final GameStateChanged gameStateChanged) {
    if (gameStateChanged.getGameState() != GameState.LOGGED_IN) return;
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

  public SoapStoneTiles getSoapStoneTiles() {
    return this.soapStoneTiles;
  }
}
