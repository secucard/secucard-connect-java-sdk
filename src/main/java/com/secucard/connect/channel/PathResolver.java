package com.secucard.connect.channel;

public interface PathResolver {

  /**
   * Returns the URL like path to use to access an object of a given type.
   *
   * @param type
   * @param separator The path part separator character, like '/'.
   * @return The path as string.
   */
  String resolve(Class type, char separator);
}
