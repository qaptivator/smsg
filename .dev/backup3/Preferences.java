package com.flexibia.smsg;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

public class Preferences {
  private static Preferences instance;
  private SharedPreferences preferences;

  private interface PreferencesOperation {
    void execute(SharedPreferences.Editor editor);
  }

  private Preferences(Context context) {
    preferences = context.getSharedPreferences("CapacitorStorage", Context.MODE_PRIVATE);
  }

  public static Preferences getInstance(Context context) {
    if (instance == null) {
      instance = new Preferences(context.getApplicationContext());
    }
    return instance;
  }

  public String getString(String key) {
    return preferences.getString(key, null);
  }

  public void setString(String key, String value) {
    executeOperation(editor -> editor.putString(key, value));
  }

  public boolean getBool(String key) {
    return Boolean.parseBoolean(preferences.getString(key, null));
  }

  public void setString(boolean key, String value) {
    executeOperation(editor -> editor.putString(Boolean.toString(key), value));
  }

  private void executeOperation(PreferencesOperation op) {
    SharedPreferences.Editor editor = preferences.edit();
    op.execute(editor);
    editor.apply();
  }
}
