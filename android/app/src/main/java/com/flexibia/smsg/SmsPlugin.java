package com.flexibia.smsg;

import android.content.Intent;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;
import org.json.JSONArray;

@CapacitorPlugin(name = "SmsGateway")
public class SmsPlugin extends Plugin {
  private static final String TAG = "SmsReceiver";

  public static Bridge staticBridge = null;
  public static SmsPlugin getSmsGatewayInstance() {
    if (staticBridge != null && staticBridge.getWebView() != null) {
      PluginHandle handle = staticBridge.getPlugin("SmsGateway");
      if (handle == null) {
        return null;
      }
      return (SmsPlugin) handle.getInstance();
    }
    return null;
  }

  //private SmsSender smsSender;

  @Override
  public void load() {
    super.load();
    //this.smsSender = new SmsSender();
  }

  @PluginMethod
  public void register(PluginCall call) {
    FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    FirebaseMessaging
      .getInstance()
      .getToken()
      .addOnCompleteListener(
        task -> {
          if (!task.isSuccessful()) {
            sendConnectionError(task.getException().getLocalizedMessage());
            return;
          }
          Intent serviceIntent = new Intent(getContext(), SmsSenderService.class);
          serviceIntent.putExtra("intentType", "deviceToken");
          serviceIntent.putExtra("deviceToken", task.getResult());
          getContext().startService(serviceIntent);
        }
      );
    call.resolve();
  }

  @PluginMethod
  public void unregister(PluginCall call) {
    FirebaseMessaging.getInstance().setAutoInitEnabled(false);
    FirebaseMessaging.getInstance().deleteToken();
    call.resolve();
  }

  private void sendConnectionError(String error) {
    JSObject data = new JSObject();
    data.put("error", error);
    notifyListeners("connectionError", data);
  }

  public void triggerFetchStorage() {
    notifyListeners("fetchStorage", null);
  }
}
