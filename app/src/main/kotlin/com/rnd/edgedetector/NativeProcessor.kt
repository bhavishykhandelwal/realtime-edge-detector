package com.rnd.edgedetector

class NativeProcessor {
    companion object {
        init {
            // Load the C++ shared library
            System.loadLibrary("native-processor")
        }
    }
    // Declare the native method (JNI signature uses 'long' for Mat addresses)
    external fun processFrame(matAddrIn: Long, matAddrOut: Long)
}