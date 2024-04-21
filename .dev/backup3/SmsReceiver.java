package com.flexibia.smsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
  private static final String TAG = "SmsReceiver";
  private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

  @Override
  public void onReceive(Context context, Intent intent) {
    //Log.d(TAG, "onReceive start");
    // ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction()))
    if (intent != null && intent.getAction() != null && intent.getAction().equals(SMS_RECEIVED)) {
      Log.d(TAG, "onReceive SMS_RECEIVED");
      Intent serviceIntent = new Intent(context, SmsService.class);
      serviceIntent.putExtras(intent.getExtras());
      context.startService(serviceIntent);
    }
  }
}
