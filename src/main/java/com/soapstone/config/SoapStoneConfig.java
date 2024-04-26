package com.soapstone.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SoapStoneConfig.GROUP_NAME)
public interface SoapStoneConfig extends Config {
  String GROUP_NAME = "soapstone";

  @ConfigItem(
      position = 1,
      keyName = "soapstoneServerBaseUrl",
      name = "SoapStones URL",
      description = "This URL needs to return the SoapStones in JSON format as described in the README.md."
  )
  default String getSoapStoneServerBaseUrl() {
    return "";
  }
}
