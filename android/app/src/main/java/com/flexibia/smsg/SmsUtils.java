package com.flexibia.smsg;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class SmsUtils {
  private static final String TAG = "SmsUtils";
  private static final int MAX_LOGS = 100;
  private static final int MAX_MESSAGES = 100;
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

  public boolean getBoolPref(String key) { return Boolean.parseBoolean(getPref(key)); }

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

  // in the ui its called an event but in the code its called a log
  public class LogItem {
    public String message;
    public String type;
    public String time;
    public LogItem(String message, String type, String time) {
      this.message = message;
      this.type = type;
      this.time = time;
    }
  }

  public void throwLog(String message, String type) {
    Log.d(TAG, String.format("throwLog message: %s type: %s", message, type));
    Gson gson = new Gson();
    //Type logsType = new TypeToken<List<LogItem>>(){}.getType();
    //List<LogItem> logs = gson.fromJson(stringLogs, logsType);
    try {
      String stringLogs = getPref("logs");
      Log.d(TAG, String.format("throwLog stringLogs: %s", stringLogs));
      if (stringLogs != null && !stringLogs.equals("")) {
        List<LogItem> logs = new ArrayList<>(Arrays.asList(gson.fromJson(stringLogs, LogItem[].class)));
        Log.d(TAG, String.format("throwLog logs: %s", logs.toString()));

        LogItem newLog = new LogItem(message, type, getLogDate());

        if (logs.size() > MAX_LOGS) {
          List<LogItem> subList = logs.subList(0, MAX_LOGS);
          logs.clear();
          logs.addAll(subList);
        }

        logs.add(0, newLog);

        String serializedLogs = gson.toJson(logs);
        setPref("logs", serializedLogs);

        //SmsPlugin.getSmsGatewayInstance().triggerFetchStorage();
      }
    } catch (Exception e) {
      Log.e(TAG, "throwLog caught exception", e);
    }
  }

  public void throwMessage(String message, String type) {
    Log.d(TAG, String.format("throwMessage message: %s type: %s", message, type));
    Gson gson = new Gson();
    try {
      String stringMessages = getPref("messages");
      Log.d(TAG, String.format("throwMessage stringMessages: %s", stringMessages));
      if (stringMessages != null && !stringMessages.equals("")) {
        List<LogItem> messages = new ArrayList<>(Arrays.asList(gson.fromJson(stringMessages, LogItem[].class)));
        Log.d(TAG, String.format("throwMessage messages: %s", messages.toString()));

        LogItem newMessage = new LogItem(message, type, getLogDate());
        Log.d(TAG, String.format("throwMessage newMessage: %s", newMessage.toString()));

        if (messages.size() > MAX_MESSAGES) {
          Log.d(TAG, "throwMessage over MAX_MESSAGES");
          List<LogItem> subList = messages.subList(0, MAX_MESSAGES);
          messages.clear();
          messages.addAll(subList);
        }

        messages.add(0, newMessage);

        String serializedMessages = gson.toJson(messages);
        Log.d(TAG, String.format("throwMessage serializedMessages: %s", serializedMessages));
        setPref("messages", serializedMessages);

        //SmsPlugin.getSmsGatewayInstance().triggerFetchStorage();
      }
    } catch (Exception e) {
      Log.e(TAG, "throwMessage caught exception", e);
    }
  }

  private String getLogDate() {
    Date now = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd h:mm:ss a");
    return formatter.format(now);
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
