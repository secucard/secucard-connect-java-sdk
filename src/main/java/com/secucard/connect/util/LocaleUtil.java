package com.secucard.connect.util;

import java.util.Locale;

public class LocaleUtil {
  public static Locale toLocale(String country, Locale locale) {
    if (locale == null || !locale.getCountry().equals(country)) {
      Locale[] locales = Locale.getAvailableLocales();
      for (Locale loc : locales) {
        if (loc.getCountry().equalsIgnoreCase(country)) {
          return loc;
        }
      }
    } else if (locale.getCountry().equals(country)) {
      return locale;
    }
    return null;
  }
}
