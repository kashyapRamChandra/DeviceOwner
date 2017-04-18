# Device Owner
A generic device owner app that allows you to selectively grant other apps permission to run in [Lock Task mode](https://developer.android.com/work/cosu.html). This app is primarily intended for developers.

You must install this app immediately after a factory reset.

1. Reset your device.
1. **Skip setting up a user account.**
1. Enable developer options and ADB.
1. Build, install, and run this app. (Note: there is no release configuration or signing key. You can create your own, or just install the debug version.)
1. When prompted, allow this app to become a Device Administrator.
1. When prompted, log into your device with `adb shell` and run the command shown on the screen.
1. Set up a user account if needed.
