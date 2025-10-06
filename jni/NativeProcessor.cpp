#include <jni.h>
#include <android/log.h>
// 🛑 Add OpenCV Headers
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace cv; // Use the OpenCV namespace

extern "C" JNIEXPORT void JNICALL
Java_com_rnd_edgedetector_NativeProcessor_processFrame(
        JNIEnv* env,
        jobject thiz,
        jlong matAddrIn,
        jlong matAddrOut)
{
    // 1. Convert memory addresses to cv::Mat pointers
    Mat& inputMat = *(Mat*)matAddrIn;
    Mat& outputMat = *(Mat*)matAddrOut;

    // Log confirmation
    __android_log_print(ANDROID_LOG_INFO, "JNI_PROCESSOR", "Processing frame (Size: %dx%d)", inputMat.cols, inputMat.rows);
    
    // --- Phase 2: OpenCV Canny Pipeline ---
    
    // Mat objects for intermediate steps
    Mat grayMat, blurMat;

    // 2. Convert to Grayscale (like RGBA)
    //  The input Mat from the camera is often RGBA or YUV, Canny needs single-channel.
    cvtColor(inputMat, grayMat, COLOR_RGBA2GRAY); 
    
    // 3. Noise Reduction (Gaussian Blur) - Improves Canny accuracy
    GaussianBlur(grayMat, blurMat, Size(5, 5), 0, 0);

    // 4. Canny Edge Detection
    // The output Mat is modified in-place with the resulting edges (grayscale image)
    Canny(blurMat, outputMat, 80, 100); // 80 and 100 are the high/low threshold values

    // 5. Cleanup
    grayMat.release();
    blurMat.release();
    // inputMat and outputMat are managed by the calling Kotlin code, do not release them here.
}