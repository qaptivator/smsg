package com.flexibia.smsg;

import com.capacitorjs.plugins.pushnotifications.PushNotificationsPlugin;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONObject;

public class SmsSender extends FirebaseMessagingService {
  private static final String TAG = "SmsSender";
  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    Log.d(TAG, "onMessageReceived");
    if (SmsUtils.getInstance(this).getBoolPref("isConnected") == true) {
      Log.d(TAG, "onMessageReceived isConnected: true");
      Intent serviceIntent = new Intent(this, SmsSenderService.class);
      startService(serviceIntent);
    }
  }

  @Override
  public void onNewToken(@NonNull String deviceToken) {
    super.onNewToken(deviceToken);
    Log.d(TAG, "onNewToken");
    SmsUtils utils = SmsUtils.getInstance(this);
    if (utils.getBoolPref("isConnected") == true) {
      Log.d(TAG, "onNewToken isConnected: true");
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "UPDATE_DEVICE_TOKEN");
        jsonObject.put("deviceToken", deviceToken);
        jsonObject.put("deviceId", utils.getDeviceId());
        utils.throwRawStatus(jsonObject);
      } catch (Exception e) {
        Log.e(TAG, "onNewToken caught exception", e);
      }
    }
  }
}
