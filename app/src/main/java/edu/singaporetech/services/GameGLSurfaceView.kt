package edu.singaporetech.services
import android.content.Context
import android.opengl.GLSurfaceView


class GameGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: GameGLRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GameGLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}