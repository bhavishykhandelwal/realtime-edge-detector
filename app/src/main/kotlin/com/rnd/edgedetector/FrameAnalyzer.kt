// FrameAnalyzer.kt

package com.rnd.edgedetector

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.rnd.edgedetector.gl.EdgeRenderer
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import kotlin.math.roundToInt
import android.widget.TextView

class FrameAnalyzer(
    private val renderer: EdgeRenderer,
    private val glSurfaceView: GLSurfaceView,
    private val fpsTextView: TextView // For bonus FPS counter
) : ImageAnalysis.Analyzer {

    private val nativeProcessor = NativeProcessor()
    private val outputMat = Mat()
    private val yuvMat = Mat()
    private val rgbaMat = Mat() // For intermediate RGBA conversion if needed
    
    // FPS variables
    private var lastTime = System.currentTimeMillis()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        
        // --- STEP 1: YUV-to-Mat Conversion (CRITICAL Performance Step) ---
        val inputMat: Mat = yuv420ToMat(imageProxy)
        
        // 🛑 IMPORTANT: If the conversion failed or we don't have a valid Mat, skip.
        if (inputMat.empty()) {
            imageProxy.close()
            return
        }

        // --- STEP 2: JNI Call (C++ Processing) ---
        val startTime = System.currentTimeMillis() 

        // Calls the C++ function in jni/NativeProcessor.cpp
        nativeProcessor.processFrame(inputMat.nativeObj, outputMat.nativeObj)

        // --- STEP 3: Rendering Update (OpenGL) ---
        
        // The outputMat contains a single-channel (grayscale) Canny image.
        // We convert it to a byte array to upload as an OpenGL texture.
        val processedBytes = ByteArray(outputMat.total().toInt() * outputMat.channels())
        outputMat.get(0, 0, processedBytes)
        
        // Pass the data to the OpenGL renderer
        renderer.updateFrame(processedBytes, outputMat.cols(), outputMat.rows())
        glSurfaceView.requestRender() // Triggers the EdgeRenderer.onDrawFrame()

        // --- Cleanup and FPS (Bonus) ---
        val currentTime = System.currentTimeMillis()
        val timeTaken = currentTime - lastTime
        val fps = 1000f / timeTaken
        
        // Update FPS counter on the UI thread (if you included the bonus TextView)
        glSurfaceView.post {
            fpsTextView.text = "FPS: ${fps.roundToInt()}"
        }
        lastTime = currentTime

        // Release the camera buffer (CRITICAL for performance)
        imageProxy.close() 
    }
    
    // YUV_420_888 to Mat Conversion Utility
    private fun yuv420ToMat(image: ImageProxy): Mat {
        val planes = image.planes
        val yBuffer: ByteBuffer = planes[0].buffer
        val uBuffer: ByteBuffer = planes[1].buffer
        val vBuffer: ByteBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val data = ByteArray(ySize + uSize + vSize)
        yBuffer.get(data, 0, ySize)
        vBuffer.get(data, ySize, vSize)
        uBuffer.get(data, ySize + vSize, uSize)

        // Create a Mat from the YUV data
        yuvMat.create(image.height + image.height / 2, image.width, CvType.CV_8UC1)
        yuvMat.put(0, 0, data)

        // Convert YUV to RGBA (or use YUV to Gray for pure speed)
        Imgproc.cvtColor(yuvMat, rgbaMat, Imgproc.COLOR_YUV420p2RGBA, 4)
        
        // NOTE: In the Canny pipeline, C++ will convert this RGBA Mat back to GRAY for processing.
        return rgbaMat
    }
}