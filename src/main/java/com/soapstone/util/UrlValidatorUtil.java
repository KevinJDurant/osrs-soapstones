package com.soapstone.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class UrlValidatorUtil {

  public static boolean isInValidURL(final String url) {
    try {
      new URL(url).toURI();
      return false;
    } catch (MalformedURLException | URISyntaxException e) {
      return true;
    }
  }
}
