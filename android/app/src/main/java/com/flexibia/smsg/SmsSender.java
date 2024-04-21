package com.flexibia.smsg;

//import com.capacitorjs.plugins.pushnotifications.PushNotificationsPlugin;
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
  public void onCreate() {
    Log.d(TAG, "onCreate");
    super.onCreate();
  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    Log.d(TAG, "onMessageReceived");
    //super.onMessageReceived(remoteMessage);
    //Log.d(TAG, "onMessageReceived");

    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());
    }

    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
    }

    if (SmsUtils.getInstance(this).getBoolPref("isConnected") == true) {
      Log.d(TAG, "onMessageReceived isConnected: true");
      Intent serviceIntent = new Intent(this, SmsSenderService.class);
      serviceIntent.putExtra("intentType", "messageQueue");
      startService(serviceIntent);
    }
  }

  @Override
  public void onNewToken(@NonNull String deviceToken) {
    Log.d(TAG, "onNewToken");
    //super.onNewToken(deviceToken);
    //Log.d(TAG, "onNewToken");
    SmsUtils utils = SmsUtils.getInstance(this);
    if (utils.getBoolPref("isConnected") == true) {
      Log.d(TAG, "onNewToken isConnected: true");
      Intent serviceIntent = new Intent(this, SmsSenderService.class);
      serviceIntent.putExtra("intentType", "deviceToken");
      serviceIntent.putExtra("deviceToken", deviceToken);
      startService(serviceIntent);
    }
  }
}
