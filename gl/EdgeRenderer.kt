// gl/EdgeRenderer.kt

package com.rnd.edgedetector.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EdgeRenderer : GLSurfaceView.Renderer {

    // Vertex coordinates for a screen-filling quad
    private val vertexCoords = floatArrayOf(
        -1.0f, -1.0f, 0.0f, // bottom left
         1.0f, -1.0f, 0.0f, // bottom right
        -1.0f,  1.0f, 0.0f, // top left
         1.0f,  1.0f, 0.0f  // top right
    )

    // Texture coordinates (flipping Y is often necessary)
    private val textureCoords = floatArrayOf(
        0.0f, 1.0f, // bottom left
        1.0f, 1.0f, // bottom right
        0.0f, 0.0f, // top left
        1.0f, 0.0f  // top right
    )

    private var programHandle: Int = 0
    private var textureId: IntArray = IntArray(1)
    private var width: Int = 0
    private var height: Int = 0
    
    private var channels: Int = 1 
    
    // Buffers for vertex and texture coordinates
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer

    // Texture update fields
    private val textureUpdateLock = Any() 
    private var nextFrameBuffer: ByteBuffer? = null

    // Handles to shader variables
    private var positionHandle: Int = 0
    private var textureCoordHandle: Int = 0
    private var textureUniformHandle: Int = 0
    private val MVPMatrix = FloatArray(16)
    
    init {
        // Initialize buffers
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertexCoords).position(0)
        
        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.put(textureCoords).position(0)
        
        Matrix.setIdentityM(MVPMatrix, 0) // Initialize projection matrix
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        
        // Load and compile shaders
        val vertexShader = ShaderUtil.loadShader(GLES20.GL_VERTEX_SHADER, ShaderUtil.VERTEX_SHADER_CODE)
        val fragmentShader = ShaderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderUtil.FRAGMENT_SHADER_CODE)
        programHandle = ShaderUtil.createProgram(vertexShader, fragmentShader)

        // Get handles to variables
        positionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition")
        textureCoordHandle = GLES20.glGetAttribLocation(programHandle, "aTextureCoord")
        textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "uTexture")

        // Generate OpenGL Texture ID
        GLES20.glGenTextures(1, textureId, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        
        GLES20.glUseProgram(programHandle)
        
        // 1. Update Texture if new data is available
        synchronized(textureUpdateLock) {
            nextFrameBuffer?.let { buffer ->
                // Determine format based on channel count
                val glFormat = if (channels == 4) GLES20.GL_RGBA else GLES20.GL_LUMINANCE
                
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])
                
                // Configure texture parameters
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
                
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, glFormat, 
                                     width, height, 0, glFormat, 
                                     GLES20.GL_UNSIGNED_BYTE, buffer)
                nextFrameBuffer = null // Mark buffer as consumed
            }
        }
        
        // 2. Pass in the texture information
        GLES20.glUniform1i(textureUniformHandle, 0) // Texture unit 0

        // 3. Draw the quad
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    /**
     * Called by FrameAnalyzer with the processed Mat data, including channel count.
     * @param data Byte array of the image frame.
     * @param frameWidth Width of the image.
     * @param frameHeight Height of the image.
     * @param channels 1 for grayscale/Canny, 4 for RGBA.
     */
  
    fun updateFrame(data: ByteArray, frameWidth: Int, frameHeight: Int, channels: Int) {
        synchronized(textureUpdateLock) {
            // Reallocate buffer only if size changes
            if (nextFrameBuffer == null || nextFrameBuffer!!.capacity() != data.size) {
                 nextFrameBuffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
            }
            nextFrameBuffer!!.clear()
            nextFrameBuffer!!.put(data)
            nextFrameBuffer!!.position(0)
            
            this.width = frameWidth
            this.height = frameHeight
            this.channels = channels 
        }
    }
}