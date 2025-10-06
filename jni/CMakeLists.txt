cmake_minimum_required(VERSION 3.22)
project("native-processor")

# Defines a shared library named 'native-processor' (used by Kotlin's System.loadLibrary)
add_library( 
    native-processor
    SHARED
    NativeProcessor.cpp  # The C++ source file we will create next
)

# Link the NDK system log for C++ logging
find_library(log-lib log)
target_link_libraries(
    native-processor
    ${log-lib}
)