package com.flexibia.smsg;

import com.capacitorjs.plugins.pushnotifications.PushNotificationsPlugin;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class SmsSender extends FirebaseMessagingService {
  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
  }

  @Override
  public void onNewToken(@NonNull String s) {
    super.onNewToken(s);
    PushNotificationsPlugin.onNewToken(s);
  }
}
