Vimo- Health (Andrpid App)
==========================

Vimo-Health is an Android application which will allows user to use any smart Iot (DIY- Build by team-Vimo) or Vendor based Activity trackers like (Mi Band) to play a innovative and enjoyable health game that motivates user to build his/ her daily health activity continously by building charactor based on their game.

Below Libraries are used for BT and Documentation
[![Build](https://travis-ci.org/Freeyourgadget/Gadgetbridge.svg?branch=master)](https://travis-ci.org/Freeyourgadget/Gadgetbridge)

## Vimo- World
![Vimo-Strive](https://pbs.twimg.com/profile_images/839435931782361088/xyWbjlpt.jpg "Twitter Image")

## Supported Devices
* Mi Band 2


## Features (Strive and VIMO-health)

* Cloud Access
* Realtime Data-Sync
* Offline Data-Sync
* Configure apps (games, and other tools)

## Getting Started (App)

1. Pair your Iot or Mi-device through the Android's Bluetooth Settings or Vimo-Health. 
2. Start Vimo-Health, tap on the device you want to connect to
3. To test, choose "Debug" from the menu and play around 

## Features (Mi Band Support)

* Discovery and pairing
* Mi Band notifications (Display + vibration) for
    * Incoming calls
    * SMS received
    * K-9 mails received
    * Conversations messages
    * Generic Android notifications
* Synchronize the time to the Mi Band 2
* Display firmware version
* Firmware update (beta)
* Heart rate measurement on demand and during sleep
* Synchronize activity data (alpha)
* Set alarms on the Mi Band 2

## How to use (Mi Band 1+2)

* When starting Vimo-health the first time, it will automatically
  attempt to discover and pair your Mi Band. Alternatively you can invoke discovery
  manually via the "+" button. It will ask you for some personal info that appears
  to be needed for proper steps calculation on the band. If you do not provide these,
  some hardcoded default "dummy" values will be used instead. 

  When your Mi Band starts to vibrate and blink during the pairing process,
  tap it quickly a few times in a row to confirm the pairing with the band.

1. Configure other notifications as desired
2. Go back to the "Gadgetbridge" activity
3. Tap the Mi Band item to connect if you're not connected yet
4. To test, chose "Debug" from the menu and play around

**Known Issues:**

* The initial connection to a Mi Band sometimes takes a little patience. Try to connect a few times, wait, 
  and try connecting again. This only happens until you have "bonded" with the Mi Band, i.e. until it 
  knows your MAC address. This behavior may also only occur with older firmware versions.
* If you use other apps like Mi Fit, and "bonding" with Gadgetbridge does not work, please
  try to unpair the band in the other app and try again with Gadgetbridge.
* While all Mi Band devices are supported, some firmware versions might work better than others.
  You can consult the [projects wiki pages](https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band) 
  to check if your firmware version is fully supported or if an upgrade/downgrade might be beneficial.

## Maintained (Code Modification and Necessary Changes for Project Vimo)

* Murshid Hassen

## Library Authors
### Core Team of GB Library (in order of first code contribution)

* Andreas Shimokawa
* Carsten Pfeiffer
* Daniele Gobbetti

### Additional device support

* João Paulo Barraca (HPlus)

## Contribute

Contributions are welcome, be it feedback, bugreports, documentation, translation, research or code. Feel free to work
on any of the open [issues](https://github.com/Freeyourgadget/Gadgetbridge/issues?q=is%3Aopen+is%3Aissue);
just leave a comment that you're working on one to avoid duplicated work.

## Do you have further questions or feedback?

Feel free to open an issue on our issue tracker, but please:
- do not use the issue tracker as a forum, do not ask for ETAs and read the issue conversation before posting
- use the search functionality to ensure that your question wasn't already answered. Don't forget to check the **closed** issues as well!
- remember that this is a community project, people are contributing in their free time because they like doing so: don't take the fun away! Be kind and constructive.

## Having problems?

1. Open Vimo-Health's settings and check the option to write log files
2. Reproduce the problem you encountered
3. Check the logfile at /sdcard/Android/data/nodomain.freeyourgadget.gadgetbridge/files/gadgetbridge.log
4. File an issue at https://github.com/MurshidMac/vimo/issues/new and possibly provide the logfile

Alternatively you may use the standard logcat functionality to access the log.

