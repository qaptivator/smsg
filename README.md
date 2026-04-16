# smsg

SMS Gateway for API

this is an android app to send and recieve sms using an android phone and your backend.

if you struggle with **Twillio** or its regional limitations, you can install this app on a phone, let the phone constantly run, and now all SMS are sent from the **sim card** of the phone.

**this was made and actively developed back in _2023-2024_, so this is just old code being pushed to github. it is also quite hard remembering every quirk of such old code, so there may be many issues if you actually decide to use this. you can also read the code directly for more direct information**

# usage

once you install and start the app, you have two urls to put in: webhook url and messages queue url. once any event happens, including system ones and sms, it will send POST requests to the webhook url. for sending messages, it will poll the messages queue url for any messages to send.

## recieve messages

at your server, make a POST route for accepting SMS messages, eg `POST /webhooks/sms`. then, put in the URL of your server and the route into the webhooks field and start the app. when the phone recieves an SMS message, it will send this JSON to the server:

```json
{
  "status": "RECEIVED",
  "deviceId": "your-unique-device-id",
  "message": "the body of the sms message",
  "from": "+1234567890",
  "time": "2024/01/01 12:00:00 PM"
}
```

it may also send other statuses like:

- `CONNECTED`: sent when the app successfully registers and starts the gateway.
- `DISCONNECTED`: sent when the user manually stops the gateway.
- `CONNECTION_FAILED`: sent if the fcm or network registration fails.

## send messages

because your device isnt an active server, your backend cant directly connect to it. thats why this app utilizes **polling**, where it requests the server for a `messages` array field with messages to send. here is an example of a response that you can send:

```json
{
  "messages": [
    {
      "id": "internal-db-id-1",
      "phoneNumber": "+1234567890",
      "message": "hello from the backend!"
    },
    {
      "id": "internal-db-id-2",
      "phoneNumber": "+0987654321",
      "message": "another queued message"
    }
  ]
}
```

## configuration

the app has various things which you can tweaj in the settings tab:

## quirks

the phone mustnt sleep. the phone can enter weird sleep modes even if the screen is on. i dont remember how exactly i made it work but youll have to figure out yourself how to make it constantly work.

**be aware that there could be a lot of unexpected things when running this app. there are no known bugs in the interface and app, but there CAN be underground rocks in the live usage, which i either didnt notice or have forgotten by now**

# license

MIT
