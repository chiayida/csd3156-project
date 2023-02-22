package edu.singaporetech.services

interface OnGameEngineUpdate{
    fun gameLogicInit()
    fun physicsInit()
    fun onPhysicsUpdate(dt : Float)
    fun onGameLogicUpdate(dt : Float)
}

class GameEngine constructor( val updateInterval: Long, private val listener: OnGameEngineUpdate  ) {

    private var previousTime = System.currentTimeMillis()
    private var fpsTime = System.currentTimeMillis()
    private var deltaTime: Float = 0f
    fun getDeltaTime(): Float { return deltaTime }
    private var frames: Int = 0
    private var fps: Int = 0
    fun getFPS(): Int { return fps }
    private var fpsUpdated = false
    fun getFPSUpdated(): Boolean { return fpsUpdated }

    private var paused : Boolean = false
    fun setPaused(_Pause : Boolean) {paused = _Pause}
    fun getPaused() : Boolean {return paused}

    fun engineInit() {
        systemsInit()
    }
    fun engineUpdate() {

        frames++
        deltaTime = (System.currentTimeMillis() - previousTime).toFloat()
        previousTime = System.currentTimeMillis()
        fpsUpdated = false

        if (System.currentTimeMillis() - fpsTime >= 1000) {
            fpsUpdated = true
            fps = frames
            frames = 0
            fpsTime = System.currentTimeMillis()
        }

        if(paused) return
        systemsUpdate(deltaTime)
    }
    /*
    *
    * */
    private fun systemsInit() {
        listener.gameLogicInit()
        listener.physicsInit()
    }

    /*
    *
    * */
    private fun systemsUpdate(dt : Float) {
        listener.onGameLogicUpdate(dt)
        listener.onPhysicsUpdate(dt)
    }


}