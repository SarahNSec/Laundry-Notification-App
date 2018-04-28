# Laundry Notification App

## Executive Summary
Are you so busy that you forget to switch your laundry around?  Do you find yourself having to re-wash clean laundry because it has sat in the washing machine too long?  The Laundry Notification App is here to help!  This app uses a sensor that can be attached to the outside of your washing machine.  The sensor detects when the machine starts and when it has completed.  Once complete, the app notifies you via text message that the laundry is ready to be switched so that you never have to re-wash a load of laundry again!

## App Capabilities
* Uses a MetaWear sensor to detect when you washing machine starts, stops, and is unloaded
* Sends text messages when the washing machine is finished running and follow-ups if the laundry is not unloaded

## Hardware / Software Requirements
The following hardware and software components are necessary to run this app.  Please see the [design page](./Design.md) for more details.
* A [MetaWear sensor](https://mbientlab.com/store/) containing an accelerometer.
* An Android device running Android API v22 or above.  This device must be able to maintain proximity to the MetaWear sensor to connect via bluetooth.
* An [IFTTT](https://ifttt.com/) account.
* A mobile device on which you will receive notifications.
* A washing machine which you would like to monitor.  This app has only been tested on top-loading washing machines.
