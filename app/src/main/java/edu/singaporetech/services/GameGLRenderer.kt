package edu.singaporetech.services
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameGLRenderer(context: Context) : GLSurfaceView.Renderer {
    private var mContext: Context = context
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;\n" +
                "varying vec2 v_TextureCoordinates;\n" +
                "attribute vec3 vPosition;\n" +
                "attribute vec2 vTexture;\n" +
                "void main() {\n" +
                "  v_TextureCoordinates = vTexture;\n" +
                "  gl_Position = uMVPMatrix * \n" +
                "mat4(0.1, 0., 0., 0.,\n" +
                "0., 0.1, 0., 0.,\n" +
                "0., 0., 0.1, 0., 0., 0., 0., 1.) * vec4(vPosition, 1.0);\n" +
                "}\n"

    private val fragmentShaderCode =
        "precision mediump float;\n" +
                "uniform sampler2D u_TextureUnit;\n" +
                "varying vec2 v_TextureCoordinates;\n" +
                "void main() {\n" +
                //"   gl_FragColor = vec4(v_TextureCoordinates, 0.0, 1.0);\n" +
                "   gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);\n" +
                //"   gl_FragColor = vec4(0, 0, 0, 1);\n" +
                "}"

    private var mProgram: Int = 0
    companion object {
        //private var mProgram: Int = 0
        //private lateinit var mContext: Context
        //private val squareList: MutableList<GameGLSquare> = mutableListOf()

        //fun AddSquare(): GameGLSquare {
        //    GLES20.glUseProgram(mProgram)
        //    //Shape Initialize
        //    val Square = GameGLSquare()
        //    Square.init()
        //    GLES20.glUseProgram(0)
        //
        //    squareList.add(Square)
        //    return Square
        //}
    }

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
        //mContext = mTContext

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

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(it, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                val log = GLES20.glGetProgramInfoLog(it)
                throw RuntimeException("Error linking program:\n$log")
            }
        }
        GameGLSquare.InitStartSquare(mProgram)

        for (sl in GameGLSquare.toBeInitializeList) {
            sl.Init()
        }

        GameGLSquare.toBeInitializeList.clear()
    }

    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(mProgram)

        for (sl in GameGLSquare.squareList) {
            sl.draw(vPMatrix)
        }

        GLES20.glUseProgram(0)
    }

}