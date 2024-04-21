package com.flexibia.smsg;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

public class SmsUtils {
  private static final String TAG = "SmsUtils";
  private static final int MAX_LOGS = 100;
  private static SmsUtils instance;
  private Context context;
  private SharedPreferences preferences;
  private RequestQueue queue;

  private SmsUtils(Context context) {
    this.context = context;
    this.preferences = context.getSharedPreferences("CapacitorStorage", Context.MODE_PRIVATE);
    this.queue = Volley.newRequestQueue(this.context);
  }

  public static SmsUtils getInstance(Context context) {
    if (instance == null) {
      instance = new SmsUtils(context.getApplicationContext());
    }
    return instance;
  }

  public void postRequest(String url, JSONObject jsonBody, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, onResponse, onErrorResponse);
    queue.add(request);
  }

  /*
  public static void postRequest(URL url, String jsonBody, Function<String,Number> callback) {
    new Thread(() -> {
      try {
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod("POST");
        client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        client.setDoOutput(true);

        try(OutputStream os = client.getOutputStream()) {
          byte[] input = jsonBody.getBytes("utf-8");
          os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
          new InputStreamReader(client.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String responseLine = null;
          while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
          }
          callback.apply(response.toString(), client.getResponseCode());
        }

        int code = client.getResponseCode();
        Log.d(TAG, String.format("postRequest responseCode: %s", code));
        client.disconnect();
      } catch (Exception e) {
        Log.e(TAG, "postRequest caught exception", e);
      }
    }).start();
  }
  */

  public String getDeviceId() {
    return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  public boolean getBoolPref(String key) {
    return Boolean.parseBoolean(getPref(key));
  }

  public void setBoolPref(String key, boolean value) {
    setPref(key, Boolean.toString(value));
  }

  public String getStringPref(String key) {
    return removeDoubleQuotes(getPref(key));
  }

  public void setStringPref(String key, String value) {
    setPref(key, addDoubleQuotes(value));
  }

  private String getPref(String key) {
    // return preferences.getString(key, null);
    return preferences.getString(key, "");
  }

  private void setPref(String key, String value) {
    executeOperationPref(editor -> editor.putString(key, value));
  }

  private interface PreferencesOperation {
    void execute(SharedPreferences.Editor editor);
  }

  private void executeOperationPref(PreferencesOperation op) {
    SharedPreferences.Editor editor = preferences.edit();
    op.execute(editor);
    editor.apply();
  }

  private static String removeDoubleQuotes(String str) {
    if (str.startsWith("\"") && str.endsWith("\"")) {
      return str.substring(1, str.length() - 1);
    }
    return str;
  }

  private static String addDoubleQuotes(String str) {
    if (str.startsWith("\"") && str.endsWith("\"")) {
      return str;
    }
    return "\"" + str + "\"";
  }

  public void throwLog(String message, String type) {

  }

  public void throwMessage(String message, String type) {

  }

  public void throwStatus(String status) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("status", status);
      jsonObject.put("deviceId", getDeviceId());
      throwRawStatus(jsonObject);
    } catch (Exception e) {
      Log.e(TAG, "throwStatus caught exception", e);
    }
  }

  public void throwSmsStatus(String status, String id, String data) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("status", status);
      jsonObject.put("id", id);
      jsonObject.put("data", data);
      jsonObject.put("deviceId", getDeviceId());
      throwRawStatus(jsonObject);
    } catch (Exception e) {
      Log.e(TAG, "throwStatus caught exception", e);
    }
  }

  public void throwRawStatus(JSONObject jsonObject) {
    String webhookUrl = getStringPref("webhookUrl");
    if (webhookUrl != "") {
      postRequest(webhookUrl, jsonObject, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {}
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {}
      });
    }
  }
}
