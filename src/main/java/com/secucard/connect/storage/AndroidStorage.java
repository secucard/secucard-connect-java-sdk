package com.secucard.connect.storage;

import android.content.SharedPreferences;

import com.secucard.connect.model.auth.Token;

import java.util.Map;

/**
 * Stores data in SharedPreferences.
 * todo: must be tested.
 */
public class AndroidStorage extends DataStorage {
  private final SharedPreferences sharedPreferences;

  public AndroidStorage(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override
  public void save(String id, Object object, boolean replace) throws DataStorageException {
      if(object == null)
          return;

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
      throw new UnsupportedOperationException("not implemented yet");
    }
    editor.apply();
  }

  @Override
  public <T> T get(String id) {
    Map<String, ?> all = sharedPreferences.getAll();
    return (T) all.get(id);
  }

  @Override
  public void clear(String id) {
    if (id == null || "*".equals(id)) {
      sharedPreferences.edit().clear().apply();
    } else {
      sharedPreferences.edit().remove(id).apply();
    }
  }
}
