// #include <jni.h>
// #include <string>
// #include <android/log.h>

// // Define the JNI function (it will be empty for now)
// extern "C" JNIEXPORT void JNICALL
// Java_com_rnd_edgedetector_NativeProcessor_processFrame(JNIEnv* env, jobject thiz, jlong matAddrIn, jlong matAddrOut) {
//     // Log a message to confirm the C++ function is callable
//     __android_log_print(ANDROID_LOG_INFO, "NativeCode", "JNI call received successfully!");
// }



#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <jni.h>

extern "C" JNIEXPORT void JNICALL
Java_com_rnd_edgedetector_NativeProcessor_processFrame(JNIEnv* env, jobject thiz, jlong matAddrIn, jlong matAddrOut) {
    // 1. Convert long addresses back to cv::Mat pointers
    cv::Mat& inputMat = *(cv::Mat*)matAddrIn;
    cv::Mat& outputMat = *(cv::Mat*)matAddrOut;

    // 2. Edge Detection Logic (Must-Have) [cite: 28, 49]
    cv::cvtColor(inputMat, outputMat, cv::COLOR_RGBA2GRAY);
    cv::GaussianBlur(outputMat, outputMat, cv::Size(5, 5), 0);
    cv::Canny(outputMat, outputMat, 50, 150);
}