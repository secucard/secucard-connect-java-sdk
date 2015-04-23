package com.secucard.connect.storage;

import android.content.SharedPreferences;
import com.secucard.connect.channel.JsonMapper;

import java.io.InputStream;
import java.util.Map;

/**
 * Stores data in SharedPreferences.
 * and separate disk cache.
 */
public class AndroidStorage extends DataStorage {
  public static final String TIME_PREFIX = "#time#";
  private JsonMapper jsonMapper = JsonMapper.get();
  private final SharedPreferences sharedPreferences;
  private final DiskCache diskCache;

  public AndroidStorage(SharedPreferences sharedPreferences, DiskCache diskCache) {
    this.sharedPreferences = sharedPreferences;
    this.diskCache = diskCache;
  }

  @Override
  public void save(String id, Object object, boolean replace) throws DataStorageException {

    if (object == null)
      return;

    if (!replace && sharedPreferences.contains(id)) {
      return;
    }

    SharedPreferences.Editor editor = sharedPreferences.edit();
    if (object instanceof String) {
      editor.putString(id, (String) object);
      putTime(id, editor);
    } else if (object instanceof Integer) {
      editor.putInt(id, (Integer) object);
      putTime(id, editor);
    } else if (object instanceof Long) {
      editor.putLong(id, (Long) object);
      putTime(id, editor);
    } else if (object instanceof Boolean) {
      editor.putBoolean(id, (Boolean) object);
      putTime(id, editor);
    } else {
      throw new DataStorageException("Can't store object of this type. Please provide String repesentation of the object");
    }
    editor.apply();
  }

  private void putTime(String id, SharedPreferences.Editor editor) {
    editor.putLong(TIME_PREFIX + id, System.currentTimeMillis());
  }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    if (diskCache == null) {
      throw new IllegalStateException("No disk cache set up to store the stream.");
    }
    diskCache.save(id, in, replace);
  }

  @Override
  public Object get(String id) {
    Map<String, ?> all = sharedPreferences.getAll();
    return all.get(id);
  }

  @Override
  public InputStream getStream(String id) {
    if (diskCache == null) {
      throw new IllegalStateException("No disk cache set up to read the stream.");
    }
    return diskCache.getStream(id);
  }

  @Override
  public void clear(String id, Long timestampMs) {
    if (id == null) {
      return;
    }

    if ("*".equals(id) && timestampMs == null) {
      sharedPreferences.edit().clear().apply();
    } else {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      Map<String, ?> map = sharedPreferences.getAll();
      for (String key : map.keySet()) {
        if (wildCardMatch(key, id)) {
          String tk = TIME_PREFIX + id;
          Long time = (Long) map.get(tk);
          if (time == null || timestampMs == null || time < timestampMs) {
            editor.remove(id);
            editor.remove(tk);
          }
        }
      }
      editor.apply();
    }

    if (diskCache != null) {
      diskCache.clear(id, timestampMs);
    }
  }

  @Override
  public void destroy() {
    sharedPreferences.edit().clear().apply();
    if (diskCache != null) {
      diskCache.destroy();
    }
  }
}
