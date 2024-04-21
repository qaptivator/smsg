package com.flexibia.smsg;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.Nullable;

import android.provider.Settings;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import java.net.URLEncoder;

public class SmsService extends Service {
  private static final String TAG = "SmsService";
  private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
  private SharedPreferences preferences;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate");
    preferences = getSharedPreferences("CapacitorStorage", MODE_PRIVATE);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand");
    Log.d(TAG, String.format("onStartCommand intent.getAction(): %s", intent.getAction()));
    //if (intent != null && intent.getAction() != null && intent.getAction().equals(SMS_RECEIVED)) {
    if (intent != null) {
      Log.d(TAG, "onStartCommand SMS_RECEIVED");
      Object[] pdus = (Object[]) intent.getExtras().get("pdus");
      if (pdus != null) {
        for (Object pdu : pdus) {
          SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
          String messageBody = smsMessage.getMessageBody();
          String messageSender = smsMessage.getOriginatingAddress();
          Log.d(TAG, String.format("onStartCommand messageBody: %s messageSender: %s", messageBody, messageSender));
          sendToApi(messageBody, messageSender);
        }
      }
    }
    //return START_STICKY;
    return START_NOT_STICKY;
  }

  private void sendToApi(String messageBody, String messageSender) {
    new Thread(() -> {
      try {
        String apiUrl = preferencesGet("webhookUrl");
        //String encodedUrl = URLEncoder.encode(apiUrl, "UTF-8");
        String encodedUrl = apiUrl;
        Log.d(TAG, String.format("SmsService.sendToApi apiUrl: %s encodedUrl: %s", apiUrl, encodedUrl));

        URL url = new URL(encodedUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String deviceId = getDeviceId();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", messageBody);
        jsonObject.put("sender", messageSender);
        jsonObject.put("deviceId", deviceId);
        jsonObject.put("status", "SMS_RECEIVED");
        String jsonString = jsonObject.toString();

        //String jsonString = String.format(
        //  "{\"message\": \"%s\", \"sender\": \"%s\", \"status\": \"SMS_RECEIVED\", \"deviceId\": \"%s\"}",
        //  messageBody, messageSender, deviceId);

        Log.d(TAG, String.format("SmsService.sendToApi jsonString: %s", jsonString));

        try(OutputStream os = conn.getOutputStream()) {
          byte[] input = jsonString.getBytes("utf-8");
          os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        Log.d(TAG, "Response code: " + code);
        conn.disconnect();
      } catch (Exception e) {
        Log.e(TAG, "Error sending message to API", e);
      }
      stopSelf();
    }).start();
  }

  private String preferencesGet(String key) {
    //SharedPreferences preferences = getSharedPreferences("CapacitorStorage", MODE_PRIVATE);
    //when updating preferences, i stringify the values first.
    //i completely forgot about that...
    return removeDoubleQuotes(preferences.getString(key, null));
  }

  private String getDeviceId() {
    return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  private static String removeDoubleQuotes(String str) {
    if (str.startsWith("\"") && str.endsWith("\"")) {
      return str.substring(1, str.length() - 1);
    }
    return str;
  }
}
