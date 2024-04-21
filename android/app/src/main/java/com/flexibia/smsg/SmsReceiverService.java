package com.flexibia.smsg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class SmsReceiverService extends Service {
  private static final String TAG = "SmsRecieverService";

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand");
    if (intent != null) {
      Log.d(TAG, "onStartCommand intent != null");
      Object[] pdus = (Object[]) intent.getExtras().get("pdus");
      if (pdus != null) {
        for (Object pdu : pdus) {
          SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
          String messageBody = smsMessage.getMessageBody();
          String messageSender = smsMessage.getOriginatingAddress();
          Log.d(TAG, String.format("onStartCommand messageBody: %s messageSender: %s", messageBody, messageSender));
          sendToApi(messageBody, messageSender);
          SmsUtils.getInstance(this).throwMessage(String.format("New message from %s: %s", messageSender, messageBody), "received");
          stopSelf();
        }
      }
    }
    //return START_STICKY;
    //return START_NOT_STICKY;
    return START_REDELIVER_INTENT;
  }

  private void sendToApi(String messageBody, String messageSender) {
    Log.d(TAG, "sendToApi");
    SmsUtils utils = SmsUtils.getInstance(this);
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("message", messageBody);
      jsonObject.put("sender", messageSender);
      jsonObject.put("deviceId", utils.getDeviceId());
      jsonObject.put("status", "SMS_RECEIVED");

      utils.throwRawStatus(jsonObject);
    } catch (Exception e) {
      Log.e(TAG, "sendToApi caught exception", e);
    }
  }
}
