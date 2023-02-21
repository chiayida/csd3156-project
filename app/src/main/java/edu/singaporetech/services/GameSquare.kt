package edu.singaporetech.services

import android.content.Context
import android.graphics.Path
import java.util.concurrent.CopyOnWriteArrayList

class GameSquare(context: Context) {

    var textureHandle: Int = edu.singaporetech.services.R.drawable.coin

    var x: Float = 0f
    var y: Float = 0f

    var xScale: Float = 70f
    var yScale: Float = 70f

    var rotation: Float = 0f

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
        textureHandle = id
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