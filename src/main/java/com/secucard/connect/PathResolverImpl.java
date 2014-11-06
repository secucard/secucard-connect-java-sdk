package com.secucard.connect;

import java.lang.reflect.Field;
import java.util.StringTokenizer;

public class PathResolverImpl implements PathResolver {

  @Override
  public String resolve(Class type, char separator) {
    try {
      Field object = type.getField("OBJECT");
      String value = (String) object.get(null);
      StringTokenizer st = new StringTokenizer(value, ".");
      String path = "";
      while (st.hasMoreTokens()) {
        char[] chars = st.nextToken().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        path += separator + new String(chars);
      }
      return path.substring(1);
    } catch (Exception e) {
      throw new SecuException("Error trying to build path from OBJECT field for type " + type, e);
    }
  }
}
