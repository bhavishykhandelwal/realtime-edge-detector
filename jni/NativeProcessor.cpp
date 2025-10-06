#include <jni.h>
#include <android/log.h> // for logging

// Standard JNI function signature: Java_PackageName_ClassName_MethodName
extern "C" JNIEXPORT void JNICALL
Java_com_rnd_edgedetector_NativeProcessor_processFrame(
        JNIEnv* env,       // JNI environment pointer
        jobject thiz,      // Calling Kotlin object
        jlong matAddrIn,   // Address for input Mat
        jlong matAddrOut)  // Address for output Mat
{
    // Log to confirm the JNI bridge is functional (Check Logcat for this!)
    __android_log_print(ANDROID_LOG_INFO, "JNI_PROCESSOR", "JNI Bridge Active! Ready for OpenCV processing.");
    
    // Phase 2 logic (OpenCV) will go here.
}