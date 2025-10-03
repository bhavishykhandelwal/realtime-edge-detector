#include <jni.h>
#include <string>
#include <android/log.h>

// Define the JNI function (it will be empty for now)
extern "C" JNIEXPORT void JNICALL
Java_com_rnd_edgedetector_NativeProcessor_processFrame(JNIEnv* env, jobject thiz, jlong matAddrIn, jlong matAddrOut) {
    // Log a message to confirm the C++ function is callable
    __android_log_print(ANDROID_LOG_INFO, "NativeCode", "JNI call received successfully!");
}