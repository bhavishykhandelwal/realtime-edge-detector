// val glSurfaceView: GLSurfaceView = findViewById(R.id.gl_surface_view)
// val renderer = EdgeRenderer()
// glSurfaceView.setEGLContextClientVersion(2)
// glSurfaceView.setRenderer(renderer)
// // IMPORTANT: Only render when data is ready
// glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY



// // In MainActivity, inside your CameraX setup function:

// val imageAnalysis = ImageAnalysis.Builder()
//     // Define target resolution
//     .setTargetResolution(Size(640, 480)) 
//     // Execute on a background thread to maintain performance [cite: 32]
//     .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
//     .build()

// imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), { imageProxy ->
//     // 1. Convert ImageProxy to OpenCV Mat (Requires OpenCV Java module)
//     // The details of this conversion (YUV -> Mat) are complex and depend on your OpenCV Java setup.
//     // Let's assume you get inputMat (YUV or RGBA) and outputMat (for result).
    
//     // imageProxy.use { 
//     //    // ... conversion logic to inputMat 
//     // } 

//     // 2. JNI Call (The C++ Processing)
//     val inputMat: Mat = // ... converted Mat from camera frame
//     val outputMat = Mat() // Create a Mat for the result

//     // Calculate time for FPS counter (Optional/Bonus) [cite: 57]
//     val startTime = System.currentTimeMillis() 

//     NativeProcessor().processFrame(inputMat.nativeObj, outputMat.nativeObj)

//     // 3. Update Renderer
//     // Convert the processed Mat (outputMat) back to a byte array for OpenGL
//     val processedBytes = ByteArray(outputMat.total().toInt() * outputMat.channels())
//     outputMat.get(0, 0, processedBytes)
    
//     renderer.updateFrame(processedBytes, outputMat.cols(), outputMat.rows())
//     glSurfaceView.requestRender() // Tell OpenGL to draw the new frame

//     // Calculate and update FPS counter
//     val timeTaken = System.currentTimeMillis() - startTime
//     // updateTextView(findViewById(R.id.fps_counter), 1000f / timeTaken)

//     imageProxy.close() // VERY IMPORTANT: Releases the buffer for the next frame
// })
// // Bind the use case to the lifecycle
// // cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)




class MainActivity : AppCompatActivity() {

    // 1. Declare components as lateinit properties
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: EdgeRenderer
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // **A. UI and Renderer Setup (Your Code Here)**
        glSurfaceView = findViewById(R.id.gl_surface_view)
        renderer = EdgeRenderer() // Assumes you've imported com.rnd.edgedetector.gl.EdgeRenderer
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // **B. Start Camera (After permissions check)**
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            // ... request permissions ...
        }
    }

    private fun startCamera() {
        // This function sets up the CameraProvider and binds the ImageAnalysis use case
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                .build()

            // **C. JNI Pipeline Implementation (Delegated to Analyzer)**
            imageAnalysis.setAnalyzer(cameraExecutor, YourFrameAnalyzer(renderer, glSurfaceView))
            
            // Bind the ImageAnalysis to the lifecycle
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
            } catch(exc: Exception) {
                // Handle binding errors
            }

        }, ContextCompat.getMainExecutor(this))
    }
    // ... allPermissionsGranted() and onRequestPermissionsResult() functions here ...
}