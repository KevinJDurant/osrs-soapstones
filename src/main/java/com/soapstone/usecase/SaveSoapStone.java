package com.soapstone.usecase;

import com.google.gson.Gson;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import com.soapstone.SoapStonePlugin;
import com.soapstone.constants.SoapStoneServerConstants;
import com.soapstone.domain.SoapStoneTile;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Singleton
public final class SaveSoapStone {
  private final SoapStonePlugin soapStonePlugin;
  private final Client client;
  private final OkHttpClient httpClient;
  private final Gson gson;

  @Inject
  public SaveSoapStone(
      final SoapStonePlugin soapStonePlugin,
      final Client client,
      final OkHttpClient httpClient,
      final Gson gson) {
    this.soapStonePlugin = soapStonePlugin;
    this.client = client;
    this.httpClient = httpClient;
    this.gson = gson;
  }

  public void execute(final Tile tile, final String message) {
    if (this.playerNameIsNotFetchable()) return;
    if (this.soapStonePlugin.getSoapStoneTiles().hasASoapStoneAtWorldPoint(tile.getWorldLocation())) return;

    final Player player = this.client.getLocalPlayer();

    final SoapStoneTile soapStone =
        new SoapStoneTile(
            message,
            player.getName(),
            tile.getWorldLocation()
        );

    this.soapStonePlugin.getSoapStoneTiles().add(soapStone);
    this.sendToServer(soapStone);
  }

  private void sendToServer(final SoapStoneTile soapStone) {
    final String json = this.gson.toJson(soapStone);

    final Request request = new Request.Builder()
        .url(SoapStoneServerConstants.SAVE_URL)
        .post(RequestBody.create(RuneLiteAPI.JSON, json))
        .build();

    this.httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(final Call call, final IOException e) {
        log.debug("Error submitting SoapStone to server: ", e);
      }

      @Override
      public void onResponse(final Call call, final Response response) throws IOException {
        response.close();
      }
    });
  }

  private boolean playerNameIsNotFetchable() {
    return this.client.getLocalPlayer() == null || this.client.getLocalPlayer().getName() == null;
  }
}
