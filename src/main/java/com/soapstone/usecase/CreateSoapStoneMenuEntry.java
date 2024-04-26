package com.soapstone.usecase;

import com.google.common.base.Strings;
import com.soapstone.SoapStonePlugin;
import com.soapstone.constants.CreateSoapStoneMenuEntryConstants;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import org.apache.commons.lang3.StringUtils;

@Singleton
@Slf4j
public final class CreateSoapStoneMenuEntry {

  private final Client client;
  private final SoapStonePlugin plugin;
  private final SaveSoapStone saveSoapStone;
  private final ChatMessageManager chatMessageManager;
  private final ChatboxPanelManager chatboxPanelManager;

  private static final String WALK_HERE = "Walk here";

  @Inject
  public CreateSoapStoneMenuEntry(
      final Client client,
      final SoapStonePlugin plugin,
      final SaveSoapStone saveSoapStone,
      final ChatMessageManager chatMessageManager,
      final ChatboxPanelManager chatboxPanelManager) {
    this.client = client;
    this.plugin = plugin;
    this.saveSoapStone = saveSoapStone;
    this.chatMessageManager = chatMessageManager;
    this.chatboxPanelManager = chatboxPanelManager;
  }

  public void execute(final MenuEntryAdded event) {
    if (this.hotkeyNotPressed() || !this.isWalkHereEvent(event) || this.menuEntryAlreadyExists()) return;

    final Tile target = this.client.getSelectedSceneTile();
    if (target == null) {
      return;
    }

    final WorldPoint worldPoint = WorldPoint.fromLocalInstance(this.client, target.getLocalLocation());

    if (!this.plugin.getSoapStoneTiles().hasASoapStoneAtWorldPoint(worldPoint)) {
      this.promptForSoapStoneCreation(event, target);
    }
    /* else {
      if (this.playerOwnsSoapStoneAtWorldLocation(worldPoint)) {
         this.promptForSoapStoneDeletion();
       }
    } */
  }

  private boolean menuEntryAlreadyExists() {
    return Arrays.stream(this.client.getMenuEntries())
        .anyMatch(menuEntry -> menuEntry.getOption().equals(CreateSoapStoneMenuEntryConstants.SOAP_STONE_MENU_ENTRY));
  }

  private void promptForSoapStoneMessage(final Tile tile) {
    this.chatboxPanelManager.openTextInput(CreateSoapStoneMenuEntryConstants.SOAP_STONE_PROMPT_TITLE)
        .value("")
        .onDone(input -> {
          if (StringUtils.isBlank(input)) return;
          if (input.length() > CreateSoapStoneMenuEntryConstants.MAX_INPUT_LENGTH) {
            this.sendMaxInputErrorChatMessage();
          } else {
            this.saveSoapStone.execute(tile, input);
          }
        })
        .build();
  }

  private void sendMaxInputErrorChatMessage() {
    final String error = "Soap Stone message is too long! Maximum " + CreateSoapStoneMenuEntryConstants.MAX_INPUT_LENGTH + " characters!";
    this.chatMessageManager.queue(
        QueuedMessage
            .builder()
            .type(ChatMessageType.GAMEMESSAGE)
            .value(error)
            .build()
    );
  }

  private boolean hotkeyNotPressed() {
    return !this.client.isKeyPressed(KeyCode.KC_SHIFT);
  }

  private boolean isWalkHereEvent(final MenuEntryAdded event) {
    return event.getOption().equals(WALK_HERE);
  }

  private void promptForSoapStoneCreation(final MenuEntryAdded event, final Tile target) {
    this.client.createMenuEntry(-10)
        .setOption(CreateSoapStoneMenuEntryConstants.SOAP_STONE_MENU_ENTRY)
        .setTarget(event.getTarget())
        .setType(MenuAction.RUNELITE)
        .onClick(e -> this.promptForSoapStoneMessage(target));
  }

  private boolean playerOwnsSoapStoneAtWorldLocation(final WorldPoint worldPoint) {
    final String playerName = this.client.getLocalPlayer().getName();
    return this.plugin
        .getSoapStoneTiles()
        .doesPlayerOwnSoapStoneAtWorldPoint(worldPoint, playerName);
  }
}
