package com.secucard.connect.service;

import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.ClientContext;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.rest.OAuthProvider;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.MemoryDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ServiceFactory {
  private Map<String, String[]> names = null;
  private Set<AbstractService> services = new HashSet<>();

  protected static void setUpStomp(ClientContext context, AuthProvider authProvider) {
    StompChannel sc = new StompChannel(context.getClientId(), context.getConfig().getStompConfiguration());
    sc.setAuthProvider(authProvider);
    context.setStompChannel(sc);
  }

  public void init(ClientContext context) {
    ClientConfiguration config = context.getConfig();

    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }

    setUpContext(context);

    ServiceLoader<AbstractService> loader = ServiceLoader.load(AbstractService.class, getClassLoader());
    for (AbstractService service : loader) {
      service.setContext(context);
      // android ServiceLoader impl. doesn't cache services,
      services.add(service);
    }

    getService("*"); // fetch service ids
  }

  /**
   * Wiring all dependencies and setting up context needed in services.
   * Override to implement special behaviour.
   *
   * @param context The client context to set up.
   */
  protected void setUpContext(ClientContext context) {
    ClientConfiguration config = context.getConfig();

    DataStorage dataStorage;
    /*try {
      dataStorage = new SimpleFileDataStorage(config.getStoragePath());
    } catch (IOException e) {
      throw new SecuException("Error creating file storage", e);
    } */
    dataStorage = new MemoryDataStorage();
    context.setDataStorage(dataStorage);

    RestChannel rc = new RestChannel(context.getClientId(), config.getRestConfiguration());
    context.setRestChannel(rc);

    OAuthProvider ap = new OAuthProvider(context.getClientId(), config);
    ap.setDataStorage(dataStorage);
    ap.setRestChannel(rc);

    rc.setAuthProvider(ap);

    context.setAuthProvider(ap);

    setUpStomp(context, ap);
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
