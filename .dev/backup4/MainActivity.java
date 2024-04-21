package com.flexibia.smsg;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    registerPlugin(SmsPlugin.class);
    super.onCreate(savedInstanceState);
  }
}
