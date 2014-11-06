package com.secucard.connect;

/**
* @author Thomas Krauß
*/
public interface PathResolver {
  String resolve(Class type, char separator);
}
