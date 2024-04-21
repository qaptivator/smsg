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
  private static final int MAX_LOGS = 100;

  @Override
  public void load() {
  }
  private void triggerRefresh() {
    notifyListeners("refreshView", null);
  }
  public void throwLog(String message, String type) {
    //String jsonString = Preferences.getInstance(getContext()).get("logs");
    //if (jsonString != null) {
      //JSONArray logs = new JSONObject("{\"value\":[{\"type\":0,\"message\":\"hi\"}]}");
      /*JSONArray logs = new JSONArray(Preferences.getInstance(getContext()).get("logs"));
      jsonObject.put("message", messageBody);
      jsonObject.put("sender", messageSender);
      jsonObject.put("deviceId", deviceId);
      jsonObject.put("status", "SMS_RECEIVED");
      String jsonString = jsonObject.toString();
      triggerRefresh();*/
    //}
  }
  public void throwMessage() {
    triggerRefresh();
  }
}
