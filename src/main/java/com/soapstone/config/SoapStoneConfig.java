package com.soapstone.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SoapStoneConfig.GROUP_NAME)
public interface SoapStoneConfig extends Config {
  String GROUP_NAME = "soapstone";

  @ConfigItem(
      position = 1,
      keyName = "soapStoneProfanityFilter",
      name = "Profanity Filter",
      description = "Having the profanity filter on will mean any swearing or offensive language will be starred out as best as the plugin can."
  )
  default boolean profanityFilterEnabled()
  {
    return true;
  }
}
