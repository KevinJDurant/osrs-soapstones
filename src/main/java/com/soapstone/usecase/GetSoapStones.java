package com.soapstone.usecase;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import com.soapstone.SoapStonePlugin;
import com.soapstone.config.SoapStoneConfig;
import com.soapstone.constants.SoapStoneServerConstants;
import com.soapstone.domain.SoapStoneTile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
@Singleton
public final class GetSoapStones {
  private final OkHttpClient httpClient;
  private final SoapStonePlugin plugin;
  private final Gson gson;
  private final SoapStoneConfig config;

  @Inject
  public GetSoapStones(
      final OkHttpClient httpClient,
      final Gson gson,
      final SoapStonePlugin plugin,
      final SoapStoneConfig config) {
    this.httpClient = httpClient;
    this.plugin = plugin;
    this.gson = gson;
    this.config = config;
  }

  public void execute() {
    this.getSoapStones();
  }

  private void getSoapStones() {
    final Request request = new Request.Builder()
        .url(SoapStoneServerConstants.GET_URL + "?profanity=" + config.profanityFilterEnabled())
        .get()
        .build();

    this.httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(final Call call, final IOException e) {
        log.warn("Unable to fetch SoapStones", e);
      }

      @Override
      public void onResponse(final Call call, final Response response) throws IOException {
        try (final ResponseBody responseBody = response.body()) {
          if (!response.isSuccessful() || responseBody == null) return;
          final List<SoapStoneTile> tiles = convertJsonToTiles(responseBody.string());
          plugin.getSoapStoneTiles().addAll(tiles);
        } catch (final Exception ignored) {}
      }
    });
  }

  private List<SoapStoneTile> convertJsonToTiles(final String json) {
    final SoapStoneTile[] tiles = gson.fromJson(json, SoapStoneTile[].class);
    return Arrays.stream(tiles).collect(Collectors.toList());
  }
}
