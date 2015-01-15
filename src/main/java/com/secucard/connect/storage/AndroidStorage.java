package com.secucard.connect.storage;

import android.content.SharedPreferences;
import com.secucard.connect.channel.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Stores data in SharedPreferences.
 * todo: must be tested.
 */
public class AndroidStorage extends DataStorage {
  public static final String PREFIX = "#convertedbyme#";
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
        } else if (object instanceof Integer) {
            editor.putInt(id, (Integer) object);
        } else if (object instanceof Long) {
            editor.putLong(id, (Long) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(id, (Boolean) object);
        } else {
          // convert to string
          String str;
          try {
            str = PREFIX + object.getClass().getCanonicalName() + ":" +jsonMapper.map(object);
          } catch (IOException e) {
            throw new DataStorageException("Error mapping object to JSON", e);
          }
          save(id, str, replace);
        }
        editor.apply();
    }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    // todo: implement
  }

  @Override
    public Object get(String id) {
      Map<String, ?> all = sharedPreferences.getAll();
      Object o = all.get(id);
      if (o instanceof String ){
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
    // todo: implement
    return null;
  }

  @Override
    public void clear(String id, Long timestampMs) {
        // todo: support timestamp
        if (id == null || "*".equals(id)) {
            sharedPreferences.edit().clear().apply();
        } else if (id.contains("*")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Map<String, ?> map = sharedPreferences.getAll();
            for (String key : map.keySet()) {
                if (wildCardMatch(key, id)) {
                    editor.remove(id);
                }
            }
            editor.apply();
        } else
            sharedPreferences.edit().remove(id).apply();
    }
}
