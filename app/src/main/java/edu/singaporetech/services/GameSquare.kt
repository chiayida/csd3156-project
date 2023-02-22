package edu.singaporetech.services

import android.content.Context
import android.graphics.*
import java.util.concurrent.CopyOnWriteArrayList

class GameSquare(context: Context) {

    private val mContext = context

    private var textureHandle: Bitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.coin)
    var textureSquare = RectF(0f, 0f, textureHandle.width.toFloat(), textureHandle.height.toFloat())
    private var rotationMatrix = Matrix()
    var rotatedTexture: Bitmap = Bitmap.createBitmap(textureHandle, 0, 0, textureHandle.width, textureHandle.height, rotationMatrix, true)
    var bitmapShader = BitmapShader(rotatedTexture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    var x: Float = 0f
    var y: Float = 0f

    var xScale: Float = 70f
    var yScale: Float = 70f

    private var rotation: Float = 0f

    companion object {
        val squareList: CopyOnWriteArrayList<GameSquare> = CopyOnWriteArrayList()
        val toBeDeleted: CopyOnWriteArrayList<GameSquare> = CopyOnWriteArrayList()

        fun clear() {
            squareList.clear()
            toBeDeleted.clear()
        }
    }

    init {
        squareList.add(this)
    }

    //Change texture for the square
    fun setImageResource(id: Int) {
        textureHandle = BitmapFactory.decodeResource(mContext.resources, id)
        textureSquare = RectF(0f, 0f, textureHandle.width.toFloat(), textureHandle.height.toFloat())

        rotationMatrix = Matrix()
        rotationMatrix.postRotate(rotation)
        rotatedTexture = Bitmap.createBitmap(textureHandle, 0, 0, textureHandle.width, textureHandle.height, rotationMatrix, true)
        bitmapShader = BitmapShader(rotatedTexture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    fun setRotate(_value: Float) {
        rotation = _value
        rotationMatrix = Matrix()
        rotationMatrix.postRotate(rotation)
        rotatedTexture = Bitmap.createBitmap(textureHandle, 0, 0, textureHandle.width, textureHandle.height, rotationMatrix, true)
        bitmapShader = BitmapShader(rotatedTexture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    //Run time get the path to draw using position and scale
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