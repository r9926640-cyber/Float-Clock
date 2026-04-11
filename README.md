# Float Clock ⏱️

A lightweight, precision floating clock overlay designed for Android. 

This tool was specifically created to help Xiaomi users accurately track the exact system seconds needed to secure a daily quota slot for the **HyperOS Bootloader Unlock** process. 

First download the Xioami community apk

you can get the app using Google search 
download the letest version and setup the app login and global region 

now read the instructions try to get a permission slot

## Features
* **Precision Timing:** Displays real-time system seconds (`HH:mm:ss`).
* **Added milliseconds support:** Display millisecond (`HH:mm:ss:mss`).
* **Draggable Interface:** Easily move the clock anywhere on the screen.
* **Ghost Mode:** Independent sliders for background opacity and text transparency.
* **Customizable:** Adjust text size and toggle between 12-hour (AM/PM) and 24-hour formats.
* **Modern & Compatible:** Fully compliant with Android 14 (API 34) while supporting Android 6.0+.

## HyperOS Specific Instructions
Due to Xiaomi's aggressive permission management, standard overlay permissions are not enough. If the clock is invisible:
1. Long-press the Float Clock app icon and tap **App info**.
2. Scroll to **Other permissions**.
3. Set **Display pop-up windows while running in the background** to **Always allow**.


## ⚡ Precision Timing & Latency Strategy
To successfully secure a HyperOS bootloader unlock slot, you must account for **Network Latency** (the time it takes for your tap to reach Xiaomi's servers in China).

### The "Pre-Fire" Math
Based on real-world testing from high-latency regions (e.g., using a VPN), there is often a delay of **~0.5 to 1.5 seconds**. To land your request at exactly `00:00:00.100` Beijing Time, you must compensate for this "lag" by tapping early.

**Example Latency Profile:**
* **Finger Tap:** 09:29:58:600 (IST)
* **Network Travel:** +1.49 seconds
* **Server Arrival:** 09:30:00:090 (IST) — **PERFECT HIT**
* **Result Received:** 09:30:00:700 (IST)

### How to use Milliseconds for Sniping
1. **Toggle Milliseconds:** Enable the `.SSS` feature in the app settings.
2. **Calculate your Lag:** Observe how long the "Requesting..." spinner takes to respond during a normal attempt.
3. **The Burst Window:** * If your lag is ~1.5s, start your high-speed tapping burst at **58:500**.
   * If your lag is <0.5s (vpn or No VPN), start your burst at **59:700**.



> **Tip:** Use a Singapore-based VPN for the lowest possible latency if your local IP is blocked (403 Forbidden).


## Build Instructions (Standard PC)
This is a standard Android Gradle project. It contains no external dependencies and relies purely on native Android UI frameworks.

**Prerequisites:**
* JDK 17+
* Android SDK (API 34)

**To build from the command line:**
1. Clone the repository: 

   git clone https://github.com/r9926640-cyber/Float-Clock.git 

2. Navigate to the directory: 

   cd Float-Clock 

3. Compile the debug APK: 

   ./gradlew assembleDebug 

   *The output APK will be located at \`app/build/outputs/apk/debug/app-debug.apk\`*

**To build via IDE:**
Simply import the project directory into **Android Studio**. Gradle will sync, and you can press "Run" to install.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
