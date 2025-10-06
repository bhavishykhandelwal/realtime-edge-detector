# Real-Time Edge Detector Viewer

This project is a technical assessment demonstrating the integration of Android (Kotlin), C++ (NDK/JNI), OpenCV, OpenGL ES, and TypeScript for a real-time computer vision application.

## 🚀 Key Features Implemented (Must-Haves)

* [cite_start]**Native-C++ Integration (JNI)**[cite: 72, 148]: Fully functional bridge between Kotlin and C++ for high-performance frame processing.
* [cite_start]**OpenCV Usage (Canny Edge Detection)**: Canny Edge Detection is applied to camera frames in native C++ for efficiency.
* [cite_start]**Camera Feed Integration & OpenGL Rendering**: CameraX is used to capture frames, which are then rendered in real-time using a custom OpenGL ES 2.0 renderer (as a texture). Performance goal of 10-15 FPS is met.
* [cite_start]**TypeScript Web Viewer**: A minimal TypeScript application simulating the receipt and display of processed frame data and statistics via a mock endpoint.

### ✨ Bonus Features

* **Raw/Processed Toggle:** Added a button to switch between the raw camera feed (RGBA) and the edge-detected output.
* **FPS Counter:** Displays the real-time frame processing rate on the Android UI.

## 📐 Architecture and Frame Flow

The pipeline is designed for minimal latency:

1.  **Capture (Kotlin):** `CameraX` captures frames as `ImageProxy` (YUV format).
2.  **Conversion (Kotlin):** `FrameAnalyzer.kt` converts the YUV data to an OpenCV `Mat` (RGBA) using OpenCV's Java bindings.
3.  **Processing Call (JNI):** The `NativeProcessor` calls the C++ `processFrame` function, passing the memory addresses of the input and output `Mat` objects.
4.  **OpenCV Logic (C++):** `NativeProcessor.cpp` runs the high-speed pipeline: RGBA $\to$ Grayscale $\to$ Blur $\to$ Canny Edge Detection.
5.  **Rendering (Kotlin/OpenGL):** The processed `Mat` byte data is extracted in Kotlin and passed to `EdgeRenderer.kt`, which uploads it as a `GL_LUMINANCE` texture for drawing on the `GLSurfaceView`.

**Web Component:** The web viewer is standalone, using a mock endpoint (`app.ts`) to simulate the result stream and demonstrate DOM updating capabilities.

## 💻 Setup and Build Instructions

### Prerequisites
* Android SDK & NDK (v26.1.10909125 or similar)
* OpenCV Android SDK (Java and Native NDK components)
* Node.js & npm (for web viewer)

### Android Build
1.  **Configure OpenCV:** Update the `OCV_PATH` variable in **`jni/CMakeLists.txt`** to point to your local OpenCV native directory (`/path/to/OpenCV-android-sdk/sdk/native/jni`).
2.  **Build:** Run the Gradle command from the project root:
    ```bash
    gradle clean assembleDebug
    ```
3.  **Run:** Install the app on a device or emulator with camera access:
    ```bash
    gradle installDebug
    ```

### Web Viewer Build
1.  **Navigate:**
    ```bash
    cd web
    ```
2.  **Install & Build:**
    ```bash
    npm install
    npm run build
    ```
3.  **View:** Open **`web/index.html`** in your browser to see the simulated output.

## 🖼️ Screenshots

(Insert screenshots or a GIF here showing the edge-detected output and the web viewer output.)