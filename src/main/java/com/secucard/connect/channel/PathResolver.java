package com.secucard.connect.channel;

/**
* @author Thomas Krauß
*/
public interface PathResolver {
  String resolve(Class type, char separator);
}
