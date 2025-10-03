val VERTEX_SHADER = """
    uniform mat4 uMVPMatrix;
    attribute vec4 aPosition;
    attribute vec2 aTextureCoord;
    varying vec2 vTextureCoord;
    void main() {
        gl_Position = uMVPMatrix * aPosition;
        vTextureCoord = aTextureCoord;
    }
"""

val FRAGMENT_SHADER = """
    precision mediump float;
    varying vec2 vTextureCoord;
    uniform sampler2D uTexture;
    void main() {
        // Sample the texture. We use a single channel (RED or LUMINANCE) 
        // from the processed OpenCV Mat data and output it as white/black.
        vec4 color = texture2D(uTexture, vTextureCoord);
        gl_FragColor = vec4(color.r, color.r, color.r, 1.0); // Simple grayscale display
    }
"""