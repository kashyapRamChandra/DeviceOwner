# Device Owner
A generic device owner app that allows you to selectively grant other apps permission to run in [Lock Task mode](https://developer.android.com/work/cosu.html). This app is primarily intended for developers.

You must install this app immediately after a factory reset.

1. Reset your device.
1. **Skip setting up a user account.**
1. Enable developer options and ADB.
1. Build and install this app. (Note: there is no release configuration or signing key. You can create your own, or just install the debug version.)
1. Log into your device with `adb shell`.
1. Run the command `dpm set-device-owner com.chalcodes.deviceowner/.DummyAdminReceiver`.
1. Set up a user account if needed.
