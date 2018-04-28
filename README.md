# Laundry Notification App

## Executive Summary
Are you so busy that you forget to switch your laundry around?  Do you find yourself having to re-wash clean laundry because it has sat in the washing machine too long?  The Laundry Notification App is here to help!  This app uses a sensor that can be attached to the outside of your washing machine.  The sensor detects when the machine starts and when it has completed.  Once complete, the app notifies you via text message that the laundry is ready to be switched so that you never have to re-wash a load of laundry again!

## App Capabilities
* Uses a MetaWear sensor to detect when you washing machine starts, stops, and is unloaded
* Sends text messages when the washing machine is finished running and follow-ups if the laundry is not unloaded

## Hardware / Software Requirements
The following hardware and software components are necessary to run this app.  Please see the [design page](./Design.md) for more details.
* A [MetaWear sensor](https://mbientlab.com/store/) containing an accelerometer.
* An Android device running Android API v22 or above to run the app.  This device must be able to maintain proximity to the MetaWear sensor to connect via bluetooth.
* An [If This Then That (IFTTT)](https://ifttt.com/) account.
* A mobile device on which you will receive notifications.
* A washing machine which you would like to monitor.  This app has only been tested on top-loading washing machines.
* Installation of Android Studio

## Installation

Clone the repository.

```
git clone https://github.com/SarahLN/Laundry-Notification-App.git
```

Open the LaundryNotification folder, which contains the app code, in Android Studio.  Within Android Studio, click File > Open... and navigate tot he LaundryNotification folder within the repository.  Click OK.

Plug the Android device that will be used to run the app into the computer and click the Run button in Android Studio.  This will compile and install the APK on the device.  Once it has been installed, the device can be unplugged from the computer and used independently.

## Setup

Basic setup needs to be performed prior to running the app.

#### IFTTT Setup

First, a webhook to send notifications must be created in IFTTT.

* Go to https://ifttt.com/maker_webhooks to enable webhooks
* Go to https://ifttt.com/create to create a webhook
  * For the `IF` condition, select `webhooks`
    * Select the `Receive a web request` trigger.
    * In the `Event Name` field, enter `machine_status_change`.
  * For the `THEN` condition, select a notification method (SMS, email, etc.)
    * Configure the notification method you choose to contain a message indicating that the laundry machine has finished and needs to be unloaded.
  * Once the configuration is complete, save the applet.

Next, the API key needs to be retrieved.

* Go to https://ifttt.com/services/maker_webhooks/settings.
* Under `Account Info`, you will see a URL field.
* Note the API key value at the end of the URL after `https://maker.ifttt.com/use/`.  This value will be entered into the app to send notifications.

Up to three API keys can be entered in the app to support sending notifications to multiple people.
