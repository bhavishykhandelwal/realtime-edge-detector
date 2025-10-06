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
    private val fpsTextView: TextView
) : ImageAnalysis.Analyzer {

    private val nativeProcessor = NativeProcessor()
    private val outputMat = Mat() // Canny output (single channel)
    private val yuvMat = Mat()
    private val rgbaMat = Mat() // Raw camera frame (four channels)
   
    @Volatile var isEdgeDetectionEnabled: Boolean = true

    // FPS variables
    private var lastTime = System.currentTimeMillis()

    fun toggleProcessing() {
        isEdgeDetectionEnabled = !isEdgeDetectionEnabled
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        
        // --- STEP 1: YUV-to-Mat Conversion ---
        val inputMat: Mat = yuv420ToMat(imageProxy)
        
        if (inputMat.empty()) {
            imageProxy.close()
            return
        }

        // --- STEP 2: Conditional Processing (The Toggle) ---
        val processedMat: Mat
        val processedMatChannels: Int
        
        if (isEdgeDetectionEnabled) {
            // Edge Detection (Canny)
            nativeProcessor.processFrame(inputMat.nativeObj, outputMat.nativeObj)
            processedMat = outputMat 
            processedMatChannels = 1 // Canny output is single-channel grayscale
        } else {
            // Raw Camera Feed
            processedMat = inputMat // This is the RGBA Mat from yuv420ToMat
            processedMatChannels = 4 // RGBA is four channels
        }

        // --- STEP 3: Rendering Update (OpenGL) ---
        
        val bufferSize = processedMat.total().toInt() * processedMatChannels
        val processedBytes = ByteArray(bufferSize)
        processedMat.get(0, 0, processedBytes)
        
        // Pass the data to the OpenGL renderer (Must use an updated renderer signature!)
        renderer.updateFrame(processedBytes, processedMat.cols(), processedMat.rows(), processedMatChannels)
        glSurfaceView.requestRender() 

        // --- Cleanup and FPS ---
        val currentTime = System.currentTimeMillis()
        val fps = 1000f / (currentTime - lastTime)
        
        glSurfaceView.post {
            fpsTextView.text = "FPS: ${fps.roundToInt()}"
        }
        lastTime = currentTime

        // Release the camera buffer
        imageProxy.close() 
    }
    
    // YUV_420_888 to Mat Conversion Utility
    private fun yuv420ToMat(image: ImageProxy): Mat {
        // ... (YUV plane extraction code) ...

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

        yuvMat.create(image.height + image.height / 2, image.width, CvType.CV_8UC1)
        yuvMat.put(0, 0, data)

        // Convert YUV to RGBA (This is the Mat used for the raw feed and Canny input)
        Imgproc.cvtColor(yuvMat, rgbaMat, Imgproc.COLOR_YUV420p2RGBA, 4)
        
        return rgbaMat
    }
}