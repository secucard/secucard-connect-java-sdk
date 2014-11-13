package com.secucard.connect.service;

import com.secucard.connect.SecuException;
import com.secucard.connect.channel.PathResolverImpl;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.rest.StaticGenericTypeResolver;
import com.secucard.connect.channel.rest.UserAgentProviderImpl;
import com.secucard.connect.channel.stomp.JsonBodyMapper;
import com.secucard.connect.channel.stomp.SecuStompChannel;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.MemoryDataStorage;
import com.secucard.connect.storage.SimpleFileDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ServiceFactory {
  private ServiceLoader<AbstractService> loader;
  private Map<String, String[]> names = null;

  public ServiceFactory(ClientContext context) {
    init(context);
  }

  private void init(ClientContext context) {
    ClientConfiguration config = context.getConfig();

    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }

    DataStorage dataStorage;
    /*try {
      dataStorage = new SimpleFileDataStorage(config.getStoragePath());
    } catch (IOException e) {
      throw new SecuException("Error creating file storage", e);
    } */
    dataStorage = new MemoryDataStorage();
    context.setDataStorage(dataStorage);

    context.setDataStorage(dataStorage);

    PathResolverImpl pathResolver = new PathResolverImpl();

    // rest
    RestChannel rc = new RestChannel(context.getClientId(), config.getRestConfiguration());
    rc.setPathResolver(pathResolver);
    rc.setTypeResolver(new StaticGenericTypeResolver());
    rc.setStorage(context.getDataStorage());
    rc.setUserAgentProvider(new UserAgentProviderImpl());
    context.setRestChannel(rc);

    // stomp
    SecuStompChannel sc = new SecuStompChannel(context.getClientId(), config.getStompConfiguration());
    sc.setBodyMapper(new JsonBodyMapper());
    sc.setPathResolver(pathResolver);
    sc.setAuthProvider(rc);
    context.setStompChannel(sc);

    loader = ServiceLoader.load(AbstractService.class, getClassLoader());
    for (AbstractService service : loader) {
      service.setContext(context);
    }

    getService("*"); // fetch service ids
  }

  public <T> T getService(String serviceId) {
    Class serviceClass = null;
    try {
      serviceClass = resolveServiceId(serviceId);
    } catch (Exception e) {
      // todo: log exception
    }

    if (serviceClass == null) {
      return null;
    }

    return (T) getService(serviceClass);
  }

  public <T> T getService(Class<T> serviceClass) {
    for (AbstractService service : loader) {
      if (service.getClass().equals(serviceClass)) {
        return (T) service;
      }
    }
    return null;
  }

  private Class resolveServiceId(String id) throws IOException, ClassNotFoundException {
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
          return Class.forName(entry.getKey());
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
