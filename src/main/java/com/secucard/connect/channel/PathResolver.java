package com.secucard.connect.channel;

/**
 * Resolve an URL like resource path of an secucard object like Transaction or Merchant.
 */
public interface PathResolver {

  /**
   * Returns the resource path.
   *
   * @param type      The type to resolve into a path.
   * @param separator The path separator character, like '/' or '.'
   * @return The path as a string.
   */
  String resolve(Class type, char separator);
}
