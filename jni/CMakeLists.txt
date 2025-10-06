cmake_minimum_required(VERSION 3.22)
project("native-processor")

# 🛑 CRITICAL: SET THIS PATH to your OpenCV Android SDK location!
# Example: If you extracted the SDK to your Downloads folder.
set(OCV_PATH "/path/to/your/OpenCV-android-sdk/sdk/native/jni") 

# Find the OpenCV CMake configuration file
find_package(OpenCV REQUIRED core imgproc) # Request core and imgproc modules

# Include directories for the necessary OpenCV headers
include_directories(${OpenCV_INCLUDE_DIRS})

# Defines a shared library named 'native-processor' (used by Kotlin's System.loadLibrary)
add_library( 
    native-processor
    SHARED
    NativeProcessor.cpp  # The C++ source file
)

# Link the NDK system log and OpenCV libraries
find_library(log-lib log)
target_link_libraries(
    native-processor
    ${log-lib}
    ${OpenCV_LIBS} # Link all required OpenCV libraries (core and imgproc)
)