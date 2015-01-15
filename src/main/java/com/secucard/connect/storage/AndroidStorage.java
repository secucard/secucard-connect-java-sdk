package com.secucard.connect.storage;

import android.content.SharedPreferences;
import com.secucard.connect.channel.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Stores data in SharedPreferences.
 * and in todo: separate disk cache.
 */
public class AndroidStorage extends DataStorage {
  public static final String PREFIX = "#convertedbyme#";
  public static final String TIME_PREFIX = "#time#";
  private JsonMapper jsonMapper = new JsonMapper();
  private final SharedPreferences sharedPreferences;

  public AndroidStorage(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
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
      // convert to string
      String str;
      try {
        str = PREFIX + object.getClass().getCanonicalName() + ":" + jsonMapper.map(object);
      } catch (IOException e) {
        throw new DataStorageException("Error mapping object to JSON", e);
      }
      save(id, str, replace);
    }
    editor.apply();
  }

  private void putTime(String id, SharedPreferences.Editor editor) {
    editor.putLong(TIME_PREFIX + id, System.currentTimeMillis());
  }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    // todo: implement, use separate file based cache for streams, not SharedPreferences
  }

  @Override
  public Object get(String id) {
    Map<String, ?> all = sharedPreferences.getAll();
    Object o = all.get(id);
    if (o instanceof String) {
      String str = (String) o;
      if (str.contains(PREFIX)) {
        int idx = str.indexOf(":");
        String cname = str.substring(PREFIX.length(), idx);
        try {
          return jsonMapper.map(str.substring(idx), Class.forName(cname));
        } catch (IOException | ClassNotFoundException e) {
          throw new DataStorageException("Error mapping JSON to object.", e);
        }
      }
    }
    return o;
  }

  @Override
  public InputStream getStream(String id) {
    // todo: implement, use separate file based cache for streams, not SharedPreferences
    return null;
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
  }
}
