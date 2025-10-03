package com.rnd.edgedetector.gl

import android.opengl.GLSurfaceView
// import necessary OpenGL classes (GLES20, GLU)
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EdgeRenderer : GLSurfaceView.Renderer {
    private var programHandle: Int = 0
    private var textureId: IntArray = IntArray(1)
    private var width: Int = 0
    private var height: Int = 0
    
    // Mat for synchronizing texture updates
    private val textureUpdateLock = Any() 
    private var textureBuffer: ByteBuffer? = null

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // 1. Compile and link GLSL shaders (using ShaderUtil)
        programHandle = ShaderUtil.createAndLinkProgram(...) 
        // 2. Generate OpenGL Texture ID
        GLES20.glGenTextures(1, textureId, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        this.width = width
        this.height = height
    }

    override fun onDrawFrame(gl: GL10) {
        // 1. Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        
        // 2. Use the shader program
        GLES20.glUseProgram(programHandle)

        // 3. Update Texture if new data is available
        synchronized(textureUpdateLock) {
            textureBuffer?.let { buffer ->
                // Bind the texture
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])
                
                // Upload new image data from the OpenCV Mat
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, 
                                     width, height, 0, GLES20.GL_LUMINANCE, 
                                     GLES20.GL_UNSIGNED_BYTE, buffer)
                textureBuffer = null // Mark buffer as consumed
            }
        }
        // 4. Draw the quad using the texture
        // (Code to draw vertices and set uniforms here)
    }

    /**
     * Called by MainActivity with the processed Mat data.
     */
    fun updateFrame(data: ByteArray, width: Int, height: Int) {
        synchronized(textureUpdateLock) {
            // Allocate a new buffer or reuse, then copy byte data
            textureBuffer = ByteBuffer.allocateDirect(data.size).put(data)
            textureBuffer!!.position(0)
            this.width = width
            this.height = height
        }
    }
}