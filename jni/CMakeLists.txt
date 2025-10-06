cmake_minimum_required(VERSION 3.22)
project("native-processor")

set(OCV_PATH "/path/to/your/OpenCV-android-sdk/sdk/native/jni") 

find_package(OpenCV REQUIRED core imgproc) 

include_directories(${OpenCV_INCLUDE_DIRS})

add_library( 
    native-processor
    SHARED
    NativeProcessor.cpp  
)

# Link the NDK system log and OpenCV libraries
find_library(log-lib log)
target_link_libraries(
    native-processor
    ${log-lib}
    ${OpenCV_LIBS} # Link all required OpenCV libraries (core and imgproc)
)