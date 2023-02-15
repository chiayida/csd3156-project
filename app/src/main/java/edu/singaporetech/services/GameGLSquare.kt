package edu.singaporetech.services

import android.graphics.BitmapFactory
import android.opengl.GLES10.*
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.*
import android.R
import android.content.Context

import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.Matrix.multiplyMM


const val COORDS_PER_VERTEX = 3
const val COORDS_PER_TEXTURE = 2

class GameGLSquare(context: Context) {
    private var squareCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f, 0f, 0f,     // top left
        -0.5f, -0.5f, 0.0f, 0f, 1f,     // bottom left
        0.5f, -0.5f, 0.0f,  1f, 1f,    // bottom right
        0.5f,  0.5f, 0.0f,  1f, 0f    // top right
    )
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    //private var positionHandle: Int = 0
    private var textureHandle: Int = edu.singaporetech.services.R.drawable.coin
    private var samplerHandle: Int = 0

    private val mContext: Context = context

    private val vertexStride: Int = (COORDS_PER_VERTEX + COORDS_PER_TEXTURE) * 4 // 4 bytes per vertex
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    private lateinit var bufferId : IntBuffer
    private var textureId = IntArray(1)

    var x: Float = 0f
    var y: Float = 0f

    var xScale: Float = 1f
    var yScale: Float = 1f

    private var worldMatrix = FloatArray(16){ i ->
        if (i % 5 == 0) 1f else 0f
    }
    private var resultMatrix= FloatArray(16)
    private var isInitialized: Boolean = false
    companion object {
        private var mProgram: Int = 0

        val toBeInitializeList: MutableList<GameGLSquare> = mutableListOf()
        val squareList: MutableList<GameGLSquare> = mutableListOf()
        val toBeDeleted: MutableList<GameGLSquare> = mutableListOf()
        fun InitStartSquare(program : Int) {
            mProgram = program
        }
    }

    init {
        toBeInitializeList.add(this)
        //Init()
    }

    fun Init() {
        //if (mProgram == 0) {
        //    toBeInitializeList.add(this)
        //}
        //else {
        isInitialized = true
            GLES20.glUseProgram(mProgram)

            var intId = intArrayOf(
                0,  0
            )
            bufferId =
                ByteBuffer.allocateDirect(8).run {
                    order(ByteOrder.nativeOrder())
                    asIntBuffer().apply {
                        put(intId)
                        position(0)
                    }
                }
            GLES20.glGenBuffers(2, bufferId);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId[0]);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId[1]);

            val drawListBuffer: ShortBuffer =
                // (# of coordinate values * 2 bytes per short)
                ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                    order(ByteOrder.nativeOrder())
                    asShortBuffer().apply {
                        put(drawOrder)
                        position(0)
                    }
                }
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 48, vertexBuffer, GLES20.GL_STATIC_DRAW);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 12, drawListBuffer, GLES20.GL_STATIC_DRAW);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

            glGenTextures(1, textureId, 0)
            glBindTexture(GL_TEXTURE_2D, textureId[0])
            val bitmap = BitmapFactory.decodeResource(mContext.getResources(), textureHandle)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
            samplerHandle = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit")

            GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0)

            GLES20.glEnableVertexAttribArray(0)
            GLES20.glEnableVertexAttribArray(1)

            squareList.add(this)

            GLES20.glUseProgram(0)
        //}
    }

    fun setImageResource(id: Int) {
        textureHandle = id

        //if (mProgram != 0) {
        if (isInitialized == true) {
            glBindTexture(GL_TEXTURE_2D, textureId[0])
            val bitmap = BitmapFactory.decodeResource(mContext.getResources(), textureHandle)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
            samplerHandle = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit")

            glBindTexture(GL_TEXTURE_2D, 0)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        worldMatrix[12] = (x - GameActivity.halfScreenWidth) / GameActivity.screenWidth
        worldMatrix[13] = ((y - GameActivity.halfScreenHeight) / GameActivity.screenHeight) * -2F
        worldMatrix[0] = xScale
        worldMatrix[5] = yScale

        android.opengl.Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, worldMatrix, 0)
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"), 1, false, mvpMatrix, 0)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])
        GLES20.glUniform1i(samplerHandle, 0)

        // Enable a handle to the triangle vertices
        vertexBuffer.position(0)
        //GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            0,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        vertexBuffer.position(COORDS_PER_VERTEX)
        //GLES20.glEnableVertexAttribArray(textureHandle)
        GLES20.glVertexAttribPointer(
            1,
            COORDS_PER_TEXTURE,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);

        // Disable vertex array
        //GLES20.glDisableVertexAttribArray(textureHandle)
        //GLES20.glDisableVertexAttribArray(positionHandle)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0);
    }
}