
<template>
    <div>
      <div class="w-full bg-red-500 p-2 flex">
        <div class="flex-auto">
           SMS Gateway
        </div>
        <div>
          <svg @click="tabType === 'main' || tabType === 'settings' ? tabType = 'help' : tabType = 'main'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 mr-1">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
          </svg>
        </div>
        <div>
          <svg @click="tabType === 'main' || tabType === 'help' ? tabType = 'settings' : tabType = 'main'" class="w-6 h-6 cursor-pointer" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </div>
      </div>
      <div v-show="tabType === 'main'">
        <div class="m-auto p-5 mt-10">
          <!--<div class="w-full text-center">
              <button class="border rounded-lg mt-5 py-2 px-10 bg-green-300 shadow-md" @click="startSMSsender">Start SMS sender</button>
          </div>
          <div class="w-full text-center">
              <button class="border rounded-lg mt-5 py-2 px-10 bg-red-300 shadow-md" @click="stopSMSsender">Stop SMS sender</button>
          </div>-->
  
          <div v-if="!isConnected">
            <div>Webhook URL</div>
            <input v-model="webhookUrl" type="text" placeholder="https://yourdomain.com/webhooks" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" class="w-full border p-1 shadow-md" />
            <div class="text-sm mt-1 mb-4 text-gray-500/75">URL to send events of new received messages and app statuses</div>
            <div>Messages queue URL</div>
            <input v-model="messagesQueueUrl" type="text" placeholder="https://yourdomain.com/messages-queue" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" class="w-full border p-1 shadow-md" />
            <div class="text-sm mt-1 mb-2 text-gray-500/75">URL to read queue of messages to be sent</div>
            <div class="w-full text-center">
              <button class="border rounded-lg mt-5 py-2 px-10 bg-cyan-300 shadow-md" @click="connect">Connect</button>
            </div>
          </div>
          <div v-else>
            <div class="w-full text-center">
              <button class="border rounded-lg mt-5 py-2 px-10 bg-red-500 shadow-md" @click="disconnect">Disconnect</button>
            </div>
          </div>
        </div>
        <div class="flex">
          <div :class="'cursor-pointer w-1/2 text-center py-2 ' + activeTab('messages')" @click="logType = 'messages'">Messages</div>
          <div :class="'cursor-pointer w-1/2 text-center py-2 ' + activeTab('events')" @click="logType = 'events'">Events</div>
        </div>
        <div class="overflow-y-scroll" style="height: calc(100vh - 385px);">
          <div v-show="logType == 'messages'" v-for="(messageElem, ind) in messages" :key="'message'+ind" class="p-5">
            <div class="flex">
              <div class="pr-1 my-auto">
                <svg v-if="messageElem.type === 'received'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75l3 3m0 0l3-3m-3 3v-7.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else-if="messageElem.type === 'sent'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 11.25l-3-3m0 0l-3 3m3-3v7.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
                </svg>
              </div>
              <div>
                {{messageElem.message}}
                <div class="text-gray-500/75 text-xs">
                  {{messageElem.time}}
                </div>
              </div>
            </div>
          </div>
          <div v-show="logType == 'events'" v-for="(log, ind) in logs" :key="'log'+ind" class="p-5">
            <div class="flex" :class="bindStatus(log.type)">
              <div class="pr-1 my-auto">
                <svg v-if="log.type === 'info'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M11.25 11.25l.041-.02a.75.75 0 011.063.852l-.708 2.836a.75.75 0 001.063.853l.041-.021M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9-3.75h.008v.008H12V8.25z" />
                </svg>
                <svg v-else-if="log.type === 'warn'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
                </svg>
                <svg v-else-if="log.type === 'error'" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9.75 9.75l4.5 4.5m0-4.5l-4.5 4.5M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-9 5.25h.008v.008H12v-.008z" />
                </svg>
              </div>
              <div>
                {{log.message}}
                <div class="text-gray-500/75 text-xs">
                  {{log.time}}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-show="tabType === 'settings'">
        <div class="flex p-2">
          <div class="mr-2">Bundle Timeout</div>
          <input v-model="bundleTimeout" class="border rounded-md w-8" min="1" max="999" type="number" placeholder="30" />
        </div>
        <div class="flex p-2">
          <div class="mr-2">SMS Sending Timeout</div>
          <input v-model="sendingTimeout" class="border rounded-md w-8" min="1" max="999" type="number" placeholder="2" />
        </div>
        <div class="flex p-2">
          <div class="mr-2">Auto-start</div>
          <label class="relative inline-flex items-center cursor-pointer">
              <input @click="toggleAutostart" type="checkbox" value="" class="sr-only peer" />
              <div class="w-11 h-6 bg-gray-200 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
          </label>
        </div>
        <div class="flex p-2">
          <div class="mr-2">Send Phone Status</div>
          <label class="relative inline-flex items-center cursor-pointer">
              <input v-model="sendPhoneStatus" type="checkbox" value="" class="sr-only peer" />
              <div class="w-11 h-6 bg-gray-200 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
          </label>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import moment from 'moment';
  import axios from 'axios';
  export default {
    name: "IndexPage",
    data() {
      return {
        url: "http://smsapi.mcuniverses.com/sms/?token=b570",
        webhookUrl: "",
        messagesQueueUrl: "",
        bundleTimeout: 30,
        sendingTimeout: 2,
        logs: [],
        messages: [],
        messagesQueue: [],
        isSending: false,
        isConnected: false,
        autoStart: false,
        sendPhoneStatus: false,
        logType: "events",
        tabType: "main",
        settingsOpened: false,
        intervalID: 0
      }
    },
    computed: {
  
    },
    mounted() {
      if (this.autoStart == true) {
        this.connect();
        cordova.plugins.backgroundMode.enable();
      }
    },
    methods: {
      startWatch() {
        cordova.plugins.SMSReceive.startWatch(
          () => {
            this.throwLog("Succesfully started watching: " + strSuccess, 'info')
            document.addEventListener('onSMSArrive', this.onSmsReceived)
          }, 
          err => {
            this.throwLog("SMS watcher error: " + err, 'error')
          }
        );
      },
      stopWatch() {
        cordova.plugins.SMSReceive.stopWatch(
          success => { 
            this.throwLog('SMS watching stopped: ' + success, 'info')
          },
          error => { 
            this.throwLog('SMS watching stopping error: ' + error, 'error') 
          }
        );
      },
      onSmsReceived(sms) {
        axios.post(this.url, {
          message: sms.body,
          sender: sms.address
        }, 
        {
          headers: {
            "Access-Control-Allow-Origin": "*"
          }
        }).then( () => {
          this.throwMessage(`SMS received from ${sms.address}: ${sms.body}`, 'received')
        }).catch( error => {
          this.throwLog("Error occured while sending SMS to server: " + error, 'info');
        })
      },
      sendSms(recipient, message, id) {
        let smsStatus = "undefined"
        if (recipient && message) {
          if (recipient.charAt(0) == "+") {
            sms.send(
              recipient,
              message, 
              {
                replaceLineBreaks: false,
                android: {
                  intent: ''
                }
              },
              () => {
                this.throwMessage(`Succesfully sent an sms to ${recipient}: ${message}`, 'sent');
                this.smsStatus = "success"
              },
              error => {
                this.throwLog("Error occured while sending SMS: " + error, 'info'); 
                this.smsStatus = "error"
              }
            );
            axios.post(this.url, {
              id,
              status: smsStatus
            }, 
            {
              headers: {
                "Access-Control-Allow-Origin": "*"
              }
            }).then( () => {
              this.throwLog("Sent SMS status to server: " + smsStatus, 'info');
            }).catch( error => {
              this.throwLog("Error occured while sending status to server: " + error, 'info');
            })
          } else {
            this.throwLog("Invalid phone number: " + recipient, 'warn');
          }
        } else {
          this.throwLog("Invalid phone number!", 'warn');
        }
      },
      requestSendingPermission(hasPerms) {
        if (hasPerms === false) {
          sms.requestPermission(
            () => { this.throwLog("SMS Permissions were accepted.", 'info'); },
            error => { this.throwLog("SMS Permissions were denied. " + error, 'warn'); }
          );
        }
      },
      async checkSendingPermission(hasPerms) {
        if (hasPerms === true) {
          if (this.messagesQueue && this.messagesQueue.length > 0) {
  
            /*this.messagesQueue.forEach(el => {
              setTimeout( () => { this.sendSms(el.recipient, el.message, el.id), this.sendingTimeout * 1000 || 2000 } )
            });*/
  
            const delayLoop = (delay) => {
              return (el, i) => {
                setTimeout(() => {
                  this.sendSms(el.recipient, el.message, el.id)
                }, i * delay);
              }
            };
  
            this.messagesQueue.forEach( delayLoop( this.sendingTimeout * 1000 ) ).then(() => { this.isSending = false })
          } else {
            this.throwLog("Message queue is empty!", 'warn');
          }
        } else {
         this.throwLog("No SEND_SMS Permission Found! Reqeusting for it now.", 'warn');
         this.requestSendingPermission(hasPerms)
        }
      },
      smsSender() {
        if (sendPhoneStatus == true) {
          this.throwStatus("ALIVE")
        }
  
        if (this.isSending == false) {
          axios.post(this.url, {}, {
          headers: {
            "Access-Control-Allow-Origin": "*"
          }
          }).then( response => {
            this.messagesQueue = response.data.messages
            this.isSending = true
            this.sendQueue()
          }).catch( error => {
            this.throwLog("Error occured while receiving SMS from server: " + error, "error")
          })
        }
      },
      startSMSsender() {
        this.intervalID = setInterval(
          () => { this.smsSender() },
          this.bundleTimeout * 1000 || 30000
        )
      },
      stopSMSsender() {
        clearInterval( this.intervalID )
      },
      sendQueue() {
        sms.hasPermission(
          this.checkSendingPermission, 
          err => {
            this.throwLog("Error occured while checking permissions: " + err, 'info');
          }
        )
      },
      connect() {
        /*if (this.url && this.isConnected == false) {
          this.isConnected = true
          this.throwLog("Successfully connected to " + this.url, "info")
        }*/
        this.isConnected = true
        /*if (this.url && this.isConnected == false) {
          cordova.plugins.SMSReceive.startWatch(
            () => {
              document.addEventListener('onSMSArrive', this.onSmsReceived)
              //this.startSMSsender()
              this.throwLog("Succesfully started watching SMS on: " + this.url, 'info')
              this.isConnected = true
            }, 
            err => {
              this.throwLog("An error occurred when starting SMS watcher: " + err, 'error')
            }
          );
        }*/
      },
      disconnect() {
        /*if (this.isConnected == true) {
          this.isConnected = false
          this.throwLog("Disconnected from " + this.url, "warn")
        }*/
        cordova.plugins.SMSReceive.stopWatch(
          success => {
            //this.stopSMSsender()
            this.throwLog('SMS watcher was stopped: ' + success, 'info')
            this.isConnected = true
          },
          error => { 
            this.throwLog('An error occurred when stopping SMS watcher: ' + error, 'error') 
          }
        );
      },
      throwLog(message, type) {
        // Types of logs
        // message
        // info
        // warn
        // error
  
        const MAX_LOGS = 100;
  
        if (!message || !type) return false;
  
        let log = { message, type, time: moment().format("YYYY/MM/DD h:mm:ss A") };
  
        if(this.logs.length > MAX_LOGS) this.logs.length = MAX_LOGS;
  
        this.logs.unshift(log);
      },
      throwMessage(message, type) {
        // Types of messages
        // received
        // sent
  
        const MAX_MESSAGES = 100;
  
        if (!message || !type) return false;
  
        let log = { message, type, time: moment().format("YYYY/MM/DD h:mm:ss A") };
  
        if(this.messages.length > MAX_MESSAGES) this.messages.length = MAX_MESSAGES;
  
        this.messages.unshift(log);
      },
      throwStatus(status, id) {
        let body = {
          ...status && { status },
          ...id && { id }
        }
        axios.post(this.webhookUrl, body, { 
          headers: { 
            "Access-Control-Allow-Origin": "*" 
          }
        }).then( response => {
          return response
        }).catch( error => {
          this.throwLog("Error occured when throwing status: " + error, "error")
          return error
        })
      },
      /*
      receiving sms format
      body: {
        recipient: <phone number>,
        message: <sms message>
      }
  
      get data format
      <empty body>
      response: {
        messages: [
          {
            id: <messgae id>
            recipient: <phone number>,
            message: <sms message>
          }
        ]
      }
  
      send status format
      body: {
        id: <messgae id>
        status: "success" or "error"
      }
  
      workflow of receiving sms
      1. send received sms data to server
  
      workflow of sending sms
      1. get first bundle of sms
      2. turn on the 'sending' mode, so app wont accept new bundles
      3. start sending every sms from bundle, and return status of the last sms to the server using webhook
      4. after bundle got sent, disable 'sending' mode, and accept new bundle
  
      workflow of sms queue (api)
      1. add sms to queue
      2. send first bundle of sms to app
      3. listen for incoming statuses
        if it's success, remove sms from queue and add it to log. 
        if it's error, leave sms in queue and increment attempts counter.
        if it reached deisred amount of attempts, remove the sms from queue and add it to log as 'failed'.
  
      sms structure
        address = phone number
        body = message text
        date = timestamp in seconds
        isTrusted = false
        type = "onSMSArrive"
        everything else are system variables
      */
      bindStatus(type) {
        switch(type) {
          case "warn":
            return "text-yellow-500";
          case "error":
            return "text-red-500";
          default:
            break;
        }
      },
      activeTab(type) {
        if (this.logType == type) return "border-b-4"
        return ""
      },
      toggleAutostart() {
        this.autoStart = !this.autoStart
        if (this.autoStart == true) {
          cordova.plugins.autoStart.enable();
          cordova.plugins.backgroundMode.enable();
        } else {
          cordova.plugins.autoStart.disable();
          cordova.plugins.backgroundMode.disable();
        }
      }
    }
  }
  </script>
  