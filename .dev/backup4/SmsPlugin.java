package com.flexibia.smsg;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONObject;
import org.json.JSONArray;

@CapacitorPlugin(name = "SmsGateway")
public class SmsPlugin extends Plugin {
  private static final String TAG = "SmsReceiver";

  @Override
  public void load() {
  }
  private void triggerRefresh() {
    notifyListeners("refreshView", null);
  }
}
