package com.secucard.connect.channel;

import com.secucard.connect.SecuException;
import com.secucard.connect.model.annotation.ProductInfo;

import java.lang.annotation.Annotation;
import java.util.StringTokenizer;

/**
 * Builds the URL like resource path according to the ProductInfo annotation of a secucard api type.
 */
public class PathResolver {

  /**
   * Returns the resource path.
   *
   * @param type      The type to resolve into a path.
   * @param separator The path separator character, like '/' or '.'
   * @return The path as a string.
   */
  public String resolveType(Class type, char separator) {
    try {
      String resourceId;
      Annotation annotation = type.getAnnotation(ProductInfo.class);
      if (annotation == null) {
        throw new IllegalArgumentException("Type has no metadata annotated: " + type);
      } else {
        resourceId = ((ProductInfo) annotation).resourceId();
      }
      StringTokenizer st = new StringTokenizer(resourceId, ".");
      String path = "";
      while (st.hasMoreTokens()) {
        char[] chars = st.nextToken().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        path += separator + new String(chars);
      }
      return path.substring(1);
    } catch (Exception e) {
      throw new SecuException("Error building path for type " + type, e);
    }
  }

  /**
   * Returns a
   * @param appId
   * @param separator
   * @return
   */
  public String resolveAppId(String appId, char separator) {
    String path = "General" + separator + "Apps";
    if (appId != null) {
      path += separator + appId;
    }

    path += separator + "callBackend";

    return path;
  }
}
