<template>
  <div>
    <div class="w-full bg-red-500 p-2 flex">
      <div class="flex-auto">SMS Gateway</div>
      <!--<div>
        <svg
          @click="switchTab('help')"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-6 h-6 mr-1 cursor-pointer">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
        </svg>
      </div>-->
      <div>
        <svg
          @click="
            switchTab('settings');
            updateSettings();
          "
          class="w-6 h-6 cursor-pointer"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z" />
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </div>
    </div>
    <div v-show="tabType === 'main'">
      <div class="m-auto p-5 mt-10">
        <div v-if="!isConnected">
          <div>Webhook URL</div>
          <input
            v-model="webhookUrl"
            type="text"
            placeholder="https://yourdomain.com/webhooks"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            class="w-full border p-1 shadow-md" />
          <div class="text-sm mt-1 mb-4 text-gray-500/75">
            URL to send events of new received SMS messages and app statuses
          </div>
          <div>Messages Queue URL</div>
          <input
            v-model="messagesQueueUrl"
            type="text"
            placeholder="https://yourdomain.com/messages-queue"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            class="w-full border p-1 shadow-md" />
          <div class="text-sm mt-1 mb-2 text-gray-500/75">
            URL to fetch messages queue to be sent
          </div>
          <div class="w-full text-center">
            <button
              class="border rounded-lg mt-5 py-2 px-10 bg-cyan-300 shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!validForm"
              @click="connect">
              Connect
            </button>
          </div>
        </div>
        <div v-else>
          <div class="m-2">Webhook URL: {{ webhookUrl }}</div>
          <div class="m-2">Messages Queue URL: {{ messagesQueueUrl }}</div>
          <div class="w-full text-center">
            <button
              class="border rounded-lg mt-5 py-2 px-10 bg-red-500 shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
              @click="disconnect">
              Disconnect
            </button>
          </div>
        </div>
      </div>
      <div class="flex">
        <div
          :class="
            'cursor-pointer w-1/2 text-center py-2 ' + activeTab('messages')
          "
          @click="logType = 'messages'">
          Messages
        </div>
        <div
          :class="
            'cursor-pointer w-1/2 text-center py-2 ' + activeTab('events')
          "
          @click="logType = 'events'">
          Events
        </div>
      </div>
      <div class="overflow-y-scroll" style="height: calc(100vh - 385px)">
        <div
          v-show="logType === 'messages'"
          v-for="(messageElem, ind) in messages"
          :key="'message' + ind"
          class="p-5">
          <div class="flex">
            <div class="pr-1 my-auto">
              <svg
                v-if="messageElem.type === 'received'"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9 12.75l3 3m0 0l3-3m-3 3v-7.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg
                v-else-if="messageElem.type === 'sent'"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M15 11.25l-3-3m0 0l-3 3m3-3v7.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
              </svg>
            </div>
            <div>
              {{ messageElem.message }}
              <div class="text-gray-500/75 text-xs">
                {{ messageElem.time }}
              </div>
            </div>
          </div>
        </div>
        <div
          v-show="logType === 'events'"
          v-for="(log, ind) in logs"
          :key="'log' + ind"
          class="p-5">
          <div class="flex" :class="bindStatus(log.type)">
            <div class="pr-1 my-auto">
              <svg
                v-if="log.type === 'info'"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
              </svg>
              <svg
                v-else-if="log.type === 'warn'"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
              </svg>
              <svg
                v-else-if="log.type === 'error'"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg
                v-else
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
                class="w-8 h-8">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
              </svg>
            </div>
            <div>
              {{ log.message }}
              <div class="text-gray-500/75 text-xs">
                {{ log.time }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-show="tabType === 'settings'">
      <div class="flex p-2">
        <div class="mr-2">Message Queue Receiving Timeout</div>
        <input
          v-model="messageQueueTimeout"
          class="border rounded-md w-16"
          min="1"
          max="999"
          type="number"
          placeholder="30" />
      </div>
      <div class="flex p-2">
        <div class="mr-2">SMS Sending Timeout</div>
        <input
          v-model="sendingTimeout"
          class="border rounded-md w-16"
          min="1"
          max="999"
          type="number"
          placeholder="2" />
      </div>
      <div class="flex p-2">
        <div class="mr-2">Auto-start</div>
        <label class="relative inline-flex items-center cursor-pointer">
          <!--<toggle v-model="autoStart" />-->
          <input type="checkbox" v-model="autoStart" />
        </label>
      </div>
      <div class="flex p-2">
        <div class="mr-2">Send Phone Status</div>
        <label class="relative inline-flex items-center cursor-pointer">
          <!--<toggle v-model="sendPhoneStatus" />-->
          <input type="checkbox" v-model="sendPhoneStatus" />
        </label>
      </div>
      <div class="flex p-2">
        <div class="mr-2">Enable retreival of message queue by interval</div>
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" v-model="enableMessageQueueInterval" />
        </label>
      </div>
      <div class="flex p-2">
        <div class="mr-2">Overwrite message queue on fetch</div>
        <label class="relative inline-flex items-center cursor-pointer">
          <input type="checkbox" v-model="overwriteMessageQueue" />
        </label>
      </div>
      <div class="p-2">
        <button
          class="border rounded-lg mt-5 py-2 px-10 bg-red-300 shadow-md"
          @click="clearLogs">
          Clear event list
        </button>
      </div>
      <div class="p-2">
        <button
          class="border rounded-lg mt-5 py-2 px-10 bg-red-300 shadow-md"
          @click="clearStorage">
          Clear local storage
        </button>
      </div>
    </div>
    <!--<div v-show="tabType === 'help'">this is the help section</div>-->
  </div>
</template>

<script>
import { Preferences } from '@capacitor/preferences';
import { Device } from '@capacitor/device';
import { PushNotifications } from '@capacitor/push-notifications';
import moment from 'moment';
import axios from 'axios';
import toggle from '../components/toggle.vue';
export default {
  name: 'IndexPage',
  components: {
    toggle,
  },
  data() {
    return {
      //url: "http://smsapi.mcuniverses.com/sms/?token=b570",
      webhookUrl: '',
      messagesQueueUrl: '',

      messageQueueTimeout: 30,
      sendingTimeout: 2,
      autoStart: false,
      sendPhoneStatus: false,
      enableMessageQueueInterval: false,
      overwriteMessageQueue: true,

      logs: [],
      messages: [],
      messagesQueue: [],
      isConnected: false,
      intervalID: 0,
      deviceID: 0,

      logType: 'events',
      tabType: 'main',
    };
  },
  computed: {
    validForm() {
      return this.webhookUrl !== '';
    },
  },
  watch: {
    autoStart(newState) {
      if (newState === true) {
        //cordova.plugins.autoStart.enable();
        //cordova.plugins.backgroundMode.enable();
      } else {
        //cordova.plugins.autoStart.disable();
        //cordova.plugins.backgroundMode.disable();
      }
    },
  },
  async mounted() {
    this.deviceID = await Device.getId();

    await this.fetchStorage();

    //await PushNotifications.addListener('registration', token => {
    //  this.throwStatus('UPDATE_DEVICE_TOKEN', { deviceToken: token });
    //});

    if (this.autoStart === true) {
      this.connect();
      //cordova.plugins.backgroundMode.enable();
    }
  },
  methods: {
    //#region Localstorage
    async updateStorage(key, data) {
      await Preferences.set({
        key,
        value: JSON.stringify(data),
      });
    },
    async getStorage(key) {
      return await Preferences.get(key);
    },
    async fetchStorage() {
      const storageDataTemplate = {
        webhookUrl: '',
        messagesQueueUrl: '',

        messageQueueTimeout: 30,
        sendingTimeout: 2,
        autoStart: false,
        sendPhoneStatus: false,
        enableMessageQueueInterval: false,
        overwriteMessageQueue: true,

        logs: [],
        messages: [],
      };

      const { keys } = await Preferences.keys();

      // if storage is empty, create a new one.
      // if storage is not empty, load the data from it.
      if (keys.length === 0) {
        for (const [key, value] of Object.entries(storageDataTemplate)) {
          this.updateStorage(key, value);
        }
      } else {
        for await (const [key, value] of Object.entries(storageDataTemplate)) {
          let myval = await Preferences.get({ key });
          this[key] = JSON.parse(myval.value);
        }
      }
    },
    clearStorage() {
      Preferences.clear();
    },
    updateSettings() {
      this.updateStorage('messageQueueTimeout', this.messageQueueTimeout);
      this.updateStorage('sendingTimeout', this.sendingTimeout);
      this.updateStorage('sendPhoneStatus', this.sendPhoneStatus);
      this.updateStorage('autoStart', this.autoStart);
      this.updateStorage(
        'enableMessageQueueInterval',
        this.enableMessageQueueInterval
      );
      this.updateStorage('overwriteMessageQueue', this.overwriteMessageQueue);
    },
    //#endregion

    //#region SMS receiving
    async onSMSArrive(sms) {
      //this.postRequest(this.webhookUrl, {
      //  status: 'SMS_RECEIVED',
      //  message: sms.body,
      //  sender: sms.address,
      //  deviceId: this.deviceID,
      //});
      this.throwStatus('SMS_RECEIVED', {
        message: sms.body,
        sender: sms.address,
        deviceId: this.deviceID,
      });
      this.throwMessage(
        `New message from ${sms.address}: ${sms.body}`,
        'received'
      );
    },
    //#endregion

    //#region SMS sending
    startMessagesQueueSender() {
      if (this.enableMessageQueueInterval) {
        this.intervalID = setInterval(
          this.getMessageQueue,
          this.messageQueueTimeout * 1000 || 30000
        );
      } else {
        PushNotifications.addListener(
          'pushNotificationReceived',
          notification => {
            this.getMessageQueue();
            //this.throwLog(
            //  `Push notification received (id:${notification.id})`,
            //  'info'
            //);
          }
        );
      }
    },
    sendSMS(recipient, message, id, data) {
      console.log('sendSMS.args', recipient, message, id);
      if (recipient && message) {
        console.log('sendSMS.charAt', recipient, message, id);
        if (recipient.charAt(0) === '+') {
          console.log('sendSMS.send', recipient, message, id);
          sms.send(
            recipient,
            message,
            {
              replaceLineBreaks: false,
              android: {
                intent: '', // INTENT
              },
            },
            () => {
              this.throwMessage(
                `Sent message to ${recipient}: ${message}`,
                'sent'
              );
              this.throwStatus('SMS_SENT', { id, data });
            },
            error => {
              this.throwLog(
                `Error occured when sending SMS (smsid:${id}): ${error}`,
                'warn' // info
              );
              this.throwStatus(error, { id, data });
            }
          );
        } else {
          // this.throwMessage
          this.throwLog(`Invalid phone number! (smsid:${id})`, 'warn');
          this.throwStatus('ERR_INVALID_NUMBER', { id, data });
        }
      }
    },
    async sendMessageQueue() {
      console.log('sendMessageQueue', 'messagesQueue:', this.messagesQueue);
      if (this.messagesQueue.length > 0) {
        const waitforme = delay => {
          return new Promise(resolve => {
            setTimeout(() => {
              resolve('');
            }, delay);
          });
        };

        for (const el of this.messagesQueue) {
          // i forgot to change "in" to "of"
          await waitforme(this.sendingTimeout * 1000 || 2000);
          console.log('el in this.messagesQueue', el);
          this.sendSMS(el.recipient, el.message, el.id || 0, el.data);
        }
        this.messagesQueue = [];

        //this.throwLog('Successfully sent messages queue.', 'info');
        this.throwStatus('MESSAGE_QUEUE_SENT');
      } else {
        //this.throwLog( this gets extremely annoying
        //  'Attempted to send messages queue but it is empty.',
        //  'info'
        //);
        //this.throwLog('Messages Queue is empty!', 'info');
        this.throwStatus('MESSAGE_QUEUE_EMPTY');
      }
    },
    getMessageQueue() {
      if (this.sendPhoneStatus === true) {
        this.throwStatus('CONNECTION_ALIVE');
      }

      console.log(
        'this.getMessageQueue',
        'messagesQueue:',
        this.messagesQueue,
        'messagesQueueUrl:',
        this.messagesQueueUrl
      );

      // this.messagesQueue.length === 0
      // this was originally here so when new message queue is gotten,
      // it wont overwrite the last one. but it caused too many issues.

      if (
        (this.messagesQueue.length === 0 || this.overwriteMessageQueue) &&
        this.messagesQueueUrl !== ''
      ) {
        console.log('messages queue length pass');
        axios
          .post(
            this.messagesQueueUrl,
            {
              deviceId: this.deviceID,
            },
            {
              headers: {
                'Access-Control-Allow-Origin': '*',
              },
            }
          )
          .then(response => {
            if (response.data.messages) {
              this.messagesQueue = response.data.messages || [];
              console.log('received messages queue', response.data.messages);
              let self = this;
              sms.hasPermission(
                sendingPerms => {
                  if (sendingPerms === true) {
                    console.log('has perms');
                    self.sendMessageQueue();
                  } else {
                    self.throwLog(
                      `Sending of message queue failed. SMS sending permission was not accepted. Check it and try reconnecting. (error:${error})`,
                      'error'
                    );
                    self.throwStatus('ERR_NO_SENDING_PERMISSION');
                  }
                },
                error => {
                  self.throwLog(
                    'Error occured when checking for SMS sending permissions: ' +
                      error,
                    'error'
                  );
                  self.throwStatus(error);
                }
              );
            } else {
              //this.throwLog('Messages not found in response body!', 'warn');
              this.throwStatus('ERR_MESSAGE_QUEUE_BODY_NOT_FOUND');
            }
          })
          .catch(error => {
            this.throwLog(
              'Error occured when receiving SMS from server with error status: ' +
                error.response.status,
              'error'
            );
            this.throwStatus('ERR_GET_MESSAGE_QUEUE');
          });
      }
    },
    //#endregion

    //#region core
    async connect() {
      this.updateStorage('webhookUrl', this.webhookUrl);
      this.updateStorage('messagesQueueUrl', this.messagesQueueUrl);

      // initial check
      if (this.isConnected === true || !this.validForm) {
        console.log('Initial check fail');
        this.throwLog(
          'Connection failed. You are already connected or the form is invalid.',
          'error'
        );
        this.throwStatus('CONNECTION_FAILED');
        return;
      }

      console.log('Initial check passed.');

      // push notifications check
      let permStatus = await PushNotifications.checkPermissions();

      if (permStatus.receive === 'prompt') {
        console.log('PushNotifications permission prompting');
        permStatus = await PushNotifications.requestPermissions();
      }

      if (permStatus.receive !== 'granted') {
        console.log('PushNotifications permission not granted');
        this.throwLog(
          'Connection failed. Push notifcations permission was not accepted! Check it and try reconnecting.',
          'error'
        );
        this.throwStatus('CONNECTION_FAILED');
        return;
      }

      await PushNotifications.addListener('registration', token => {
        console.log('PushNotifications.registration');
        this.throwStatus('UPDATE_DEVICE_TOKEN', { deviceToken: token.value });
      });

      await PushNotifications.addListener('registrationError', err => {
        console.log(
          'PushNotifications.registrationError',
          'err.error:',
          err.error
        );
        this.throwLog(
          `Connection failed. FCM registration had failed. Try reconnecting. (error:${err.error})`,
          'error'
        );
        this.throwStatus('CONNECTION_FAILED');
      });

      await PushNotifications.register();

      console.log('Push notification permission check passed.');

      // sms sending check

      let smsSendingResult = await new Promise((resolve, reject) => {
        sms.hasPermission(
          sendingPerms => {
            if (sendingPerms === true) {
              console.log('SMS sending permission is available');
              resolve(true);
            } else {
              console.log('asking for SMS sending permission');
              sms.requestPermission(
                () => {
                  console.log('SMS sending permission was accepted');
                  //this.throwLog('SMS Permissions were accepted.', 'info');
                  this.throwStatus('SENDING_PERMISSION_ACCEPTED');
                  resolve(true);
                },
                error => {
                  console.log('SMS sending permission was not accepted');
                  this.throwLog(
                    `Connection failed. SMS sending permission was not accepted. Check it and try reconnecting. (error:${error})`,
                    'error'
                  );
                  this.throwStatus('SENDING_PERMISSION_DENIED');
                  this.throwStatus('CONNECTION_FAILED');
                  resolve(false);
                }
              );
            }
          },
          error => {
            console.log('SMS sending permission check error.', 'error:', error);
            this.throwLog(
              `Connection failed. Error occured when checking for SMS sending permissions. Try reconnecting. (error:${error})`,
              'error'
            );
            this.throwStatus(error);
            this.throwStatus('CONNECTION_FAILED');
            resolve(false);
          }
        );
      });

      if (!smsSendingResult) {
        console.log('SMS sending permision fail');
        return;
      }

      console.log('SMS sending permission check passed.');

      if (this.messagesQueueUrl !== '') {
        console.log('Starting message queue sender');
        this.startMessagesQueueSender();
      } else {
        console.log('Not starting message queue sender');
        this.throwLog(
          `Messages Queue URL was not provided, the app won't send any SMS.`,
          'warn'
        );
      }

      this.isConnected = true;
      this.throwStatus('CONNECTED');
      this.throwLog(`Connected to ${this.webhookUrl}`, 'info');

      // sms reading check + startup of sms watcher
      /*
      cordova.plugins.SMSReceive.startWatch(
        success => {
          console.log('SMS sending permission check passed.');

          if (this.messagesQueueUrl !== '') {
            console.log('Starting message queue sender');
            this.startMessagesQueueSender();
          } else {
            console.log('Not starting message queue sender');
            this.throwLog(
              `Messages Queue URL was not provided, the app won't send any SMS.`,
              'warn'
            );
          }

          document.addEventListener('onSMSArrive', this.onSMSArrive);
          this.isConnected = true;
          this.throwStatus('CONNECTED');
          this.throwLog(`Connected to ${this.webhookUrl}`, 'info');
          console.log('Connected');
        },
        error => {
          if (error === 'PERMISSION_DENIED') {
            console.log('SMS reading permission denied');
            this.throwLog(
              `Connection failed. SMS reading permission was not accepted. Check it and try reconnecting. (error:${error})`,
              'error'
            );
            this.throwStatus('ERR_NO_SMS_READING_PERMISSION');
            this.throwStatus('CONNECTION_FAILED');
          } else if (error === 'SMS_EQUALS_NULL') {
            console.log('SMS reading permission error SMS_EQUALS_NULL');
            this.throwLog(
              `Connection failed. SMS reader could not start. Try reconnecting. (error:${error})`,
              'error'
            );
            this.throwStatus('ERR_SMS_EQUALS_NULL');
            this.throwStatus('CONNECTION_FAILED');
          } else {
            console.log('SMS reading permission error.', 'error:', error);
            this.throwLog(
              `Connection failed. SMS reader could not start. Try reconnecting. (error:${error})`,
              'error'
            );
            this.throwStatus(error);
            this.throwStatus('CONNECTION_FAILED');
          }
        }
      );*/
    },
    async disconnect() {
      if (this.isConnected === true) {
        console.log('Starting disconnecting');

        if (this.enableMessageQueueInterval) {
          clearInterval(this.intervalID);
          console.log('Interval cleared');
        } else {
          await PushNotifications.removeAllListeners();
          console.log('All PushNotifications listeners removed');
        }
        /*
        cordova.plugins.SMSReceive.stopWatch(
          success => {
            console.log(
              'Stopped watching for incoming messages. success:',
              success
            );
            document.removeEventListener('onSMSArrive', this.onSMSArrive);
          },
          error => {
            console.log(
              'Error occured when stopped watching.',
              'error:',
              error
            );
            this.throwLog(
              'Error occured when stopped watching for incoming messages: ' +
                error,
              'error'
            );
          }
        );
        */

        this.isConnected = false;
        this.throwStatus('DISCONNECTED');
        this.throwLog(`Disconnected from ${this.webhookUrl}`, 'info');
        console.log('Disconnected');
      }
    },
    //#endregion

    //#region communication
    clearLogs() {
      this.logs = [];
      this.updateStorage('logs', this.logs);
    },
    throwLog(message, type = 'info') {
      // Types of logs
      // message
      // info
      // warn
      // error

      const MAX_LOGS = 100;

      if (!message || !type) return false;

      let log = {
        message,
        type,
        time: moment().format('YYYY/MM/DD h:mm:ss A'),
      };

      if (this.logs.length > MAX_LOGS) this.logs.length = MAX_LOGS;

      this.logs.unshift(log);
      this.updateStorage('logs', this.logs);
    },
    throwMessage(message, type) {
      // Types of messages
      // received
      // sent

      const MAX_MESSAGES = 100;

      if (!message || !type) return false;

      let log = {
        message,
        type,
        time: moment().format('YYYY/MM/DD h:mm:ss A'),
      };

      if (this.messages.length > MAX_MESSAGES)
        this.messages.length = MAX_MESSAGES;

      this.messages.unshift(log);
      this.updateStorage('messages', this.messages);
    },
    throwStatus(status, options) {
      /*let body = {
        ...(status && { status }),
        ...(messageID && { messageID }),
        ...(data && { data }),
        deviceId: this.deviceID,
      };*/

      let body = Object.assign(
        {
          ...(status && { status }),
          deviceId: this.deviceID,
        },
        options
      );
      this.postRequest(this.webhookUrl, body);

      /*axios
        .post(this.webhookUrl, body, {
          headers: {
            "Access-Control-Allow-Origin": "*",
          },
        })
        .then(response => {
          return response;
        })
        .catch(error => {
          this.throwLog(
            "Error occured when sending status with error " +
              error.response.status,
            "error"
          );
          return error;
        });*/
    },
    postRequest(url, body) {
      axios
        .post(url, body, {
          headers: {
            'Access-Control-Allow-Origin': '*',
          },
        })
        .then(response => {
          return response;
        })
        .catch(error => {
          console.log(
            'HTTP Post Request error',
            'error:',
            error,
            'url:',
            url,
            'status:',
            error.response.status
          );
          this.throwLog(
            'HTTP Post Request returned with error status ' +
              error.response.status,
            'error'
          );
          return error;
        });
    },
    //#endregion

    //#region visual
    bindStatus(type) {
      switch (type) {
        case 'warn':
          return 'text-yellow-500';
        case 'error':
          return 'text-red-500';
      }
    },
    switchTab(tab) {
      const defaulTab = 'main';

      if (this.tabType === tab) {
        this.tabType = defaulTab;
      } else {
        this.tabType = tab;
      }
    },
    activeTab(type) {
      if (this.logType === type) return 'border-b-4';
      return '';
    },
    //#endregion
  },
};
</script>
