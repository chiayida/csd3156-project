package edu.singaporetech.services

import android.media.MediaPlayer
import java.io.IOException

class SoundSystem(var gameActivity: GameActivity)  {


    private lateinit var pewSFX : MediaPlayer
    private lateinit var pew2SFX : MediaPlayer
    private lateinit var clickSFX : MediaPlayer
    private lateinit var gameBGM : MediaPlayer

    fun InitializeSounds(){
        // INIT SOUNDS
        pewSFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pewSFX.setVolume(0.35f, 0.35f)
        pew2SFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pew2SFX.setVolume(0.35f, 0.35f)
        clickSFX = MediaPlayer.create(gameActivity, R.raw.button_select)
        gameBGM = MediaPlayer.create(gameActivity, R.raw.game_bgm)

    }

    fun ReleaseSounds(){
        pewSFX.release()
        pew2SFX.release()
        clickSFX.release()
        gameBGM.stop()
        gameBGM.release()
    }

    fun playGameBGM(){
        gameBGM.isLooping = true // set looping
        gameBGM.setVolume(1.0f, 1.0f) // set volume
        gameBGM.start()
    }

    fun playShootSFX(type : ProjectileType){
        if(type == ProjectileType.Player){
            pewSFX.start()
        }
        else{
            pew2SFX.start()
        }
    }

    fun playClick(){
        clickSFX.start()
    }

}