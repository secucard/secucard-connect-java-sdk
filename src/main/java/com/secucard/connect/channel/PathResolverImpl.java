package com.secucard.connect.channel;

import com.secucard.connect.SecuException;
import com.secucard.connect.model.annotation.ProductInfo;

import java.lang.annotation.Annotation;
import java.util.StringTokenizer;

public class PathResolverImpl implements PathResolver {

  @Override
  public String resolve(Class type, char separator) {
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
}
