# VS Code Tip: In C++ files, VS Code will use the NDK compiler based on this setup.
cmake_minimum_required(VERSION 3.22)
project("native-processor")

# This is where you would link the OpenCV libraries later
add_library( # Sets up a shared library named 'native-processor'
    native-processor
    SHARED
    jni/NativeProcessor.cpp  # This is the C++ file we'll create next
)

# Link the NDK system log (for logging from C++)
find_library(log-lib log)
target_link_libraries(native-processor ${log-lib})