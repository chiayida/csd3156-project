package edu.singaporetech.services

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity

class SoundSystem(var gameActivity: AppCompatActivity)  {

    private lateinit var pewSFX : MediaPlayer
    private lateinit var pew2SFX : MediaPlayer
    private lateinit var clickSFX : MediaPlayer
    private lateinit var gameBGM : MediaPlayer
    private lateinit var playerDamagedSFX : MediaPlayer
    private lateinit var enmeyDamagedSFX : MediaPlayer

    fun initializeSounds(){

        // INIT SOUNDS
        pewSFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pewSFX.setVolume(0.35f, 0.35f)
        pew2SFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pew2SFX.setVolume(0.35f, 0.35f)
        clickSFX = MediaPlayer.create(gameActivity, R.raw.button_select)
        gameBGM = MediaPlayer.create(gameActivity, R.raw.game_bgm)
        playerDamagedSFX = MediaPlayer.create(gameActivity, R.raw.explode)
        playerDamagedSFX.setVolume(0.35f, 0.35f)
        enmeyDamagedSFX = MediaPlayer.create(gameActivity, R.raw.explode)
        enmeyDamagedSFX.setVolume(0.35f, 0.35f)

    }
    fun stopSounds(){
        gameBGM.stop()
    }
    fun releaseSounds(){
        pewSFX.release()
        pew2SFX.release()
        clickSFX.release()
        gameBGM.release()
        playerDamagedSFX.release()
        enmeyDamagedSFX.release()
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

    fun playDamageSFX(isPlayer : Boolean){
        if(isPlayer){
            playerDamagedSFX.start()
        }
        else{
            enmeyDamagedSFX.start()
        }
    }

    fun playClick(){
        clickSFX.start()
    }

}