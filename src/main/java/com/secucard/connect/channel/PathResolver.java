package com.secucard.connect.channel;

import com.secucard.connect.SecuException;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

/**
 * Builds the URL like resource path according to the ProductInfo annotation of a secucard api type.
 */
public class PathResolver {
  PathResolver() {
  }

  /**
   * Returns the resource path.
   *
   * @param type      The type to resolve into a path.
   * @param separator The path separator character, like '/' or '.'
   * @return The path as a string.
   */
  public String resolveType(Class type, char separator) {
    String resourceId;
    Annotation annotation = type.getAnnotation(ProductInfo.class);
    if (annotation == null) {
      try {
        Field fields = type.getDeclaredField(SecuObject.OBJECT_FIELD);
        resourceId = (String) fields.get(null);
      } catch (Exception e) {
        throw new IllegalArgumentException("Type has no metadata annotated nor static OBJECT field: " + type);
      }
    } else {
      resourceId = ((ProductInfo) annotation).resourceId();
    }
    return resolve(resourceId, separator);
  }

  public String resolveType(Object object, char separator) {
    if (object instanceof SecuObject) {
      String objectId = ((SecuObject) object).getObject();
      if (StringUtils.isNotBlank(objectId)) {
        return resolve(objectId, separator);
      }
    }
    return resolveType(object.getClass(), separator);
  }

  private static String resolve(String resourceId, char separator) {
    try {
      StringTokenizer st = new StringTokenizer(resourceId, ".");
      String path = "";
      while (st.hasMoreTokens()) {
        char[] chars = st.nextToken().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        path += separator + new String(chars);
      }
      return path.substring(1);
    } catch (Exception e) {
      throw new RuntimeException("Error building path for resource " + resourceId, e);
    }
  }

  /**
   * Returns a
   *
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
