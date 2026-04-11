# Float Clock ⏱️

A lightweight, precision floating clock overlay designed for Android. 

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
