package com.flexibia.smsg;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.getcapacitor.BridgeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BridgeActivity {
  private static final String TAG = "MainActivitySMSG";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    registerPlugin(SmsPlugin.class);
    super.onCreate(savedInstanceState);
    //FirebaseApp.initializeApp(this);
    /*FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    FirebaseMessaging.getInstance().getToken()
      .addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
          if (!task.isSuccessful()) {
            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
            return;
          }

          // Get new FCM registration token
          String token = task.getResult();
          Log.d(TAG, "FCM Token: " + token);
        }
      });*/
  }
}
