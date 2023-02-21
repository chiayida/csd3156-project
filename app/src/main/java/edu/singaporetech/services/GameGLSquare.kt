package edu.singaporetech.services

import android.graphics.BitmapFactory
import android.opengl.GLES10.*
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.*
import android.content.Context

import android.graphics.Bitmap
import android.graphics.Path
import android.opengl.Matrix
import java.util.concurrent.CopyOnWriteArrayList


const val COORDS_PER_VERTEX = 3
const val COORDS_PER_TEXTURE = 2

class GameGLSquare(context: Context) {

    var textureHandle: Int = edu.singaporetech.services.R.drawable.coin

    var x: Float = 0f
    var y: Float = 0f

    var xScale: Float = 70f
    var yScale: Float = 70f

    var rotation: Float = 0f

    companion object {
        private var mProgram: Int = 0

        val toBeInitializeList: CopyOnWriteArrayList<GameGLSquare> = CopyOnWriteArrayList()
        val squareList: CopyOnWriteArrayList<GameGLSquare> = CopyOnWriteArrayList()
        val toBeDeleted: CopyOnWriteArrayList<GameGLSquare> = CopyOnWriteArrayList()
        fun InitStartSquare(program : Int) {
            mProgram = program
        }
        fun Clear() {
            toBeInitializeList.clear()
            squareList.clear()
            toBeDeleted.clear()
        }
    }

    init {
        squareList.add(this)
    }

    //Change texture for the square
    fun setImageResource(id: Int) {
        textureHandle = id
    }

    //Run time draw function to be called by renderer
    fun getPath(): Path {
        val left = x -xScale
        val right = x + xScale
        val top = y - yScale
        val bot = y + yScale

        return Path().apply {
            moveTo(left, top)
            lineTo(right, top)
            lineTo(right, bot)
            lineTo(left, bot)
            close()
        }
    }
}