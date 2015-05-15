package com.secucard.connect.service;

import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.ClientContext;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ServiceFactory {
  private Map<String, String[]> names = null;
  private Set<AbstractService> services = new HashSet<>();

  public ServiceFactory(ClientContext context) {
    ClientConfiguration config = context.getConfig();

    if (config == null) {
      throw new IllegalStateException("Configuration must not be null.");
    }

    ServiceLoader<AbstractService> loader = ServiceLoader.load(AbstractService.class, getClassLoader());
    for (AbstractService service : loader) {
      service.setContext(context);
      service.init();
      // in Android ServiceLoader impl. doesn't cache services,
      services.add(service);
    }

    getService("*"); // fetch service ids
  }

  public <T extends AbstractService> T getService(String serviceId) {
    Class<T> serviceClass = null;
    try {
      serviceClass = resolveServiceId(serviceId);
    } catch (Exception e) {
      // todo: log exception
    }

    if (serviceClass == null) {
      return null;
    }

    return getService(serviceClass);
  }

  public <T extends AbstractService> T getService(Class<T> serviceClass) {

    for (AbstractService service : services) {
      if (service.getClass().equals(serviceClass)) {
        return (T) service;
      }
    }
    return null;
  }

  private <T extends AbstractService> Class<T> resolveServiceId(String id) throws IOException, ClassNotFoundException {
    if (names == null) {
      // lazy load names
      names = new HashMap<>();
      Enumeration<URL> resources = getClassLoader().getResources("META-INF/services");
      while (resources.hasMoreElements()) {
        File f = new File(resources.nextElement().getFile());
        if (f.isDirectory()) {
          File[] files = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
              return name.contains("AbstractService");
            }
          });
          if (files != null) {
            for (File file : files) {
              BufferedReader reader = new BufferedReader(new InputStreamReader(
                  new FileInputStream(file), "utf-8"));
              String line;
              while ((line = reader.readLine()) != null) {
                parseLine(line, names);
              }
            }
          }
        }
      }
    }

    for (Map.Entry<String, String[]> entry : names.entrySet()) {
      String[] ids = entry.getValue();
      for (String s : ids) {
        if (s.equalsIgnoreCase(id)) {
          return (Class<T>) Class.forName(entry.getKey());
        }
      }
    }

    return null;
  }

  private ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  private void parseLine(String line, Map<String, String[]> names) throws IOException, ServiceConfigurationError {
    int ci = line.indexOf('#');
    if (ci >= 0) {
      String className = line.substring(0, ci);
      className = className.trim();
      int n = className.length();
      if (n != 0) {
        if ((className.indexOf(' ') >= 0) || (className.indexOf('\t') >= 0)) {
          return;
        }
        int cp = className.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
          return;
        }
        for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
          cp = line.codePointAt(i);
          if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
            return;
          }
        }
      }

      ArrayList<String> ids = new ArrayList<>(3);
      String sub = StringUtils.substringBetween(line, "{", "}");
      if (sub != null) {
        StringTokenizer st = new StringTokenizer(sub, ",");
        while (st.hasMoreTokens()) {
          String id = st.nextToken().trim();
          if (StringUtils.isNotBlank(id)) {
            ids.add(id);
          }
        }
      }

      if (!ids.isEmpty() && !names.containsKey(className)) {
        names.put(className, ids.toArray(new String[ids.size()]));
      }
    }
  }

}
