package com.flexibia.smsg;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class SmsSenderService extends Service {
  private static final String TAG = "SmsSenderService";

  /*public class SmsMessageQueue {
    public SmsMessage[] messages;
  }

  public class SmsMessage {
    public String message;
    public String recipient;
    public String id;
    public String data;
  }*/

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand");

    try {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        String intentType = intent.getStringExtra("intentType");
        if (intentType != null && !intentType.equals("")) {
          if (intentType.equals("messageQueue")) {
            handleMessageQueue();
          }
          if (intentType.equals("deviceToken")) {
            String deviceToken = intent.getStringExtra("deviceToken");
            if (deviceToken != null && !deviceToken.equals("")) {
              handleDeviceToken(deviceToken);
            }
          }
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "onStartCommand caught exception", e);
    } finally {
      stopSelf();
    }

    //return START_STICKY;
    //return START_NOT_STICKY;
    return START_REDELIVER_INTENT;
  }

  private void handleDeviceToken(String deviceToken) {
    SmsUtils utils = SmsUtils.getInstance(this);
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("status", "UPDATE_DEVICE_TOKEN");
      jsonObject.put("deviceToken", deviceToken);
      jsonObject.put("deviceId", utils.getDeviceId());
      utils.throwRawStatus(jsonObject);
    } catch (Exception e) {
      Log.e(TAG, "handleDeviceToken caught exception", e);
    }
  }

  private void handleMessageQueue() {
    SmsUtils utils = SmsUtils.getInstance(this);
    try {
      if (utils.getBoolPref("sendPhoneStatus") == true) {
        utils.throwStatus("CONNECTION_ALIVE");
      }

      String messageQueueUrl = utils.getStringPref("messagesQueueUrl");
      // we will remove the message queue check entirely
      if (messageQueueUrl != "") {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", utils.getDeviceId());

        utils.postRequest(messageQueueUrl, jsonObject,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              try {
                Log.d(TAG, "onStartCommand onResponse");
                if (response != null) {
                  JSONArray messageQueue = response.getJSONArray("messages");
                  Log.d(TAG, "onStartCommand messageQueue");
                  if (messageQueue != null) {
                    sendMessageQueue(messageQueue);
                  }
                }
              } catch (Exception e) {
                Log.e(TAG, "onStartCommand request response error", e);
              }
            }
          }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {
              Log.e(TAG, "onStartCommand request error", response);
            }
          });
      }
    } catch (Exception e) {
      Log.e(TAG, "onStartCommand caught exception", e);
    }
  }

  private void sendMessageQueue(JSONArray messageQueue) {
    Log.d(TAG, "sendMessageQueue");
    SmsUtils utils = SmsUtils.getInstance(this);
    try {
      if (messageQueue != null && messageQueue.length() > 0) {
        for (int i = 0; i < messageQueue.length(); i++) {
          Log.d(TAG, "sendMessageQueue iteration");
          // im too lazy to make the delay
          JSONObject messageObj = messageQueue.getJSONObject(i);
          Log.d(TAG, String.format("sendMessageQueue messageObj: %s", messageObj.toString()));
          String recipient = getStringSafe(messageObj, "recipient");
          String message = getStringSafe(messageObj, "message");
          Log.d(TAG, String.format("sendMessageQueue recipient: %s message: %s", recipient, message));
          if (!recipient.equals("") && !message.equals("")) {
            Log.d(TAG, "sendMessageQueue sendSms");
            sendSms(
              recipient,
              message,
              getStringSafe(messageObj, "id"),
              getStringSafe(messageObj, "data")
            );
          }
        }
        utils.throwStatus("MESSAGE_QUEUE_SENT");
      }
      //else {
      //  utils.throwStatus("MESSAGE_QUEUE_EMPTY");
      //}
    } catch (Exception e) {
      Log.e(TAG, "sendMessageQueue caught exception", e);
    }
  }

  private void sendSms(String recipient, String message, String id, String data) {
    Log.d(TAG, String.format("sendSms recipient: %s message: %s", recipient, message));
    SmsUtils utils = SmsUtils.getInstance(this);
    String defaultId = id.equals("") ? "0" : id;
    try {
      Log.d(TAG, "sendSms charAt");
      // "" is string, '' is char
      if (recipient.charAt(0) == '+') {
        Log.d(TAG, "sendSms send");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);
        utils.throwMessage(String.format("Sent message to %s: %s", recipient, message), "sent");
        utils.throwSmsStatus("SMS_SENT", defaultId, data);
        Log.d(TAG, "sendSms already sent");
      } else {
        utils.throwLog(String.format("Invalid phone number! (smsid:%s)", id), "warn");
        utils.throwSmsStatus("ERR_INVALID_NUMBER", defaultId, data);
      }
    } catch (Exception e) {
      // looks like i dont have a status for when sms sending failed
      Log.e(TAG, "sendSms caught exception", e);
      utils.throwLog(String.format("Error occured when sending SMS (smsid:%s)", id), "warn");
    }
  }

  private String getStringSafe(JSONObject jsonObject, String key) {
    try {
      return jsonObject.getString(key);
    } catch (JSONException e) {
      return "";
    }
  }
}

