package edu.singaporetech.services
import android.content.Context
import android.graphics.*
import android.view.View

class GameCanvasView(context: Context) : View(context) {
    private var extraCanvas: Canvas
    private var extraBitmap: Bitmap

    private val pathBounds = RectF()

    init {
        extraBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.BLACK)
    }

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = Color.BLACK
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        //Clear scene
        extraCanvas.drawColor(Color.BLACK)

        //Remove objects at the start to prevent cases where objects is deleted while looping below
        for (sl in GameSquare.toBeDeleted) {
            GameSquare.squareList.remove(sl)
        }
        GameSquare.toBeDeleted.clear()

        //Loop through all square to draw
        for (sl in GameSquare.squareList) {

            val matrix = Matrix()
            val path = sl.getPath()
            path.computeBounds(pathBounds, true)
            // Map the texture to the bounds of the path so that texture always appear on path
            matrix.setRectToRect(
                sl.textureSquare,
                pathBounds, Matrix.ScaleToFit.FILL
            )

            paint.shader = sl.bitmapShader
            paint.shader.setLocalMatrix(matrix)
            extraCanvas.drawPath(path, paint)
        }

        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        //Force redraw
        postInvalidate()
    }
}