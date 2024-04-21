package com.flexibia.smsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
      Intent serviceIntent = new Intent(context, SmsService.class);
      serviceIntent.putExtras(intent.getExtras());
      context.startService(serviceIntent);
    }
  }
}
