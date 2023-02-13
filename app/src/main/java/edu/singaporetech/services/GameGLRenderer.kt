package edu.singaporetech.services
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix

class GameGLRenderer : GLSurfaceView.Renderer {
    private var mProgram: Int = 0

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * " +
                "mat4(0.1, 0., 0., 0.," +
                "0., 0.1, 0., 0., " +
                "0., 0., 0.1, 0.," +
                "0., 0., 0., 1.) * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vec4(0, 0, 0, 1);" +
                "}"

    private lateinit var mSquare: GameGLSquare

    fun loadShader(type: Int, shaderCode: String): Int {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        //Shader Initialize
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {
            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)
            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)
            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }

        //Shape Initialize
        mSquare = GameGLSquare()
        mSquare.init(mProgram)
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(mProgram)

        mSquare.draw(mProgram, vPMatrix)

        GLES20.glUseProgram(0)
    }

}