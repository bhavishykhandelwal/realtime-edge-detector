package com.rnd.edgedetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.opengl.GLSurfaceView
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import android.widget.TextView
import android.widget.Button
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rnd.edgedetector.gl.EdgeRenderer
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: EdgeRenderer
    private lateinit var fpsTextView: TextView
    private lateinit var toggleButton: Button 
    private lateinit var frameAnalyzer: FrameAnalyzer 
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!OpenCVLoader.initLocal()) {
            Log.e("OpenCV", "OpenCV initialization failed.")
        }

        // 1. Setup UI components
        glSurfaceView = findViewById(R.id.gl_surface_view)
        fpsTextView = findViewById(R.id.fps_counter)
        toggleButton = findViewById(R.id.toggle_button) 
        
        // 2. Setup GLSurfaceView and Renderer
        renderer = EdgeRenderer()
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // 3. Check Permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, 
                arrayOf(Manifest.permission.CAMERA), 
                CAMERA_REQUEST_CODE
            )
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, 
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    
    // ... (onRequestPermissionsResult function) ...

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480)) 
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                .build()
            
            // --- JNI Pipeline Implementation (Analyzer) ---
            frameAnalyzer = FrameAnalyzer(renderer, glSurfaceView, fpsTextView)
            imageAnalysis.setAnalyzer(cameraExecutor, frameAnalyzer)
            
         
            toggleButton.setOnClickListener {
                frameAnalyzer.toggleProcessing()
                val buttonText = if (frameAnalyzer.isEdgeDetectionEnabled) "Processing: ON" else "Processing: OFF"
                toggleButton.text = buttonText
            }
            // ---------------------------------------------
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)

            } catch(exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
}