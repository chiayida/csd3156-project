package edu.singaporetech.services

import android.opengl.GLES10.*
import android.opengl.GLES11.glVertexPointer
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLES30.glGenVertexArrays
import java.nio.*

const val COORDS_PER_VERTEX = 3

class GameGLSquare {
    var squareCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f,  0.5f, 0.0f       // top right
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }



    private lateinit var bufferId : IntBuffer

    fun init(mProgram : Int) {
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
    }

    fun draw(mProgram : Int, mvpMatrix: FloatArray) {
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"), 1, false, mvpMatrix, 0)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER,0);
    }
}