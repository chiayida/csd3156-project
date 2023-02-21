package edu.singaporetech.services
import android.content.Context
import android.graphics.*
import android.view.View

class GameCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    init {
        extraBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.BLACK)
    }

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = Color.BLACK
        //// Smooths out edges of what is drawn without affecting shape.
        //isAntiAlias = true
        //// Dithering affects how colors with higher-precision than the device are down-sampled.
        //isDither = true
        //style = Paint.Style.STROKE // default: FILL
        //strokeJoin = Paint.Join.ROUND // default: MITER
        //strokeCap = Paint.Cap.ROUND // default: BUTT
        //strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        extraCanvas.drawColor(Color.BLACK)

        for (sl in GameSquare.toBeDeleted) {
            GameSquare.squareList.remove(sl)
        }
        GameSquare.toBeDeleted.clear()

        for (sl in GameSquare.squareList) {
            val texture = BitmapFactory.decodeResource(resources, sl.textureHandle)
            val matrix = Matrix()
            val pathBounds = RectF()
            val path = sl.getPath()
            path.computeBounds(pathBounds, true)
            // Map the texture to the bounds of the path so that texture always appear on path
            matrix.setRectToRect(
                RectF(0f, 0f, texture.width.toFloat(), texture.height.toFloat()),
                pathBounds, Matrix.ScaleToFit.FILL
            )
            //matrix.postRotate(45f)
            paint.shader = BitmapShader(texture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader.setLocalMatrix(matrix)
            extraCanvas.drawPath(path, paint)
        }

        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        //Force redraw
        postInvalidate()
    }
}