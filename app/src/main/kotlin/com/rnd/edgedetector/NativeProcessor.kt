package com.rnd.edgedetector

class NativeProcessor {
    companion object {
        init {
            // Loads the shared library created by CMakeLists.txt
            System.loadLibrary("native-processor")
        }
    }

    /**
     * Declares the native C++ method. 
     * The 'long' type is used to pass the memory address of an OpenCV Mat.
     */
    external fun processFrame(matAddrIn: Long, matAddrOut: Long)
}