package com.flexibia.smsg;

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

public class SmsService extends Service {
  private static final String TAG = "SmsService";
  private SharedPreferences preferences;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    preferences = getSharedPreferences("CapacitorStorage", MODE_PRIVATE);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && intent.getAction() != null && intent.getAction().equals(TelephonyManager.ACTION_RESPOND_VIA_MESSAGE)) {
      Object[] pdus = (Object[]) intent.getExtras().get("pdus");
      if (pdus != null) {
        for (Object pdu : pdus) {
          SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
          String messageBody = smsMessage.getMessageBody();
          String messageSender = smsMessage.getOriginatingAddress();
          sendToApi(messageBody, messageSender);
        }
      }
    }
    return START_STICKY;
  }

  private void sendToApi(String messageBody, String messageSender) {
    new Thread(() -> {
      try {
        String apiUrl = preferencesGet("webhookUrl");

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String deviceId = getDeviceId();

        String jsonInputString = String.format(
          "{\"message\": \"%s\", \"sender\": \"%s\", \"status\": \"SMS_RECEIVED\", \"deviceId\": \"%s\"}",
          messageBody, messageSender, deviceId);

        try(OutputStream os = conn.getOutputStream()) {
          byte[] input = jsonInputString.getBytes("utf-8");
          os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        Log.d(TAG, "Response code: " + code);
      } catch (Exception e) {
        Log.e(TAG, "Error sending message to API", e);
      }
    }).start();
  }

  private String preferencesGet(String key) {
    //SharedPreferences preferences = getSharedPreferences("CapacitorStorage", MODE_PRIVATE);
    return preferences.getString(key, null);
  }

  private String getDeviceId() {
    return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
  }
}
