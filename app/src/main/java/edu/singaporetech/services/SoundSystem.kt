package edu.singaporetech.services

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class SoundSystem(var gameActivity: AppCompatActivity)  {

    private lateinit var pewSFX : MediaPlayer
    private lateinit var pew2SFX : MediaPlayer
    private lateinit var clickSFX : MediaPlayer
    private lateinit var gameBGM : MediaPlayer
    private lateinit var pdamagedSFX : MediaPlayer
    private lateinit var edamagedSFX : MediaPlayer

    fun InitializeSounds(){

        // INIT SOUNDS
        pewSFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pewSFX.setVolume(0.35f, 0.35f)
        pew2SFX = MediaPlayer.create(gameActivity, R.raw.pew1)
        pew2SFX.setVolume(0.35f, 0.35f)
        clickSFX = MediaPlayer.create(gameActivity, R.raw.button_select)
        gameBGM = MediaPlayer.create(gameActivity, R.raw.game_bgm)
        pdamagedSFX = MediaPlayer.create(gameActivity, R.raw.explode)
        pdamagedSFX.setVolume(0.35f, 0.35f)
        edamagedSFX = MediaPlayer.create(gameActivity, R.raw.explode)
        edamagedSFX.setVolume(0.35f, 0.35f)

    }
    fun StopSounds(){
        gameBGM.stop()
    }
    fun ReleaseSounds(){
        pewSFX.release()
        pew2SFX.release()
        clickSFX.release()
        gameBGM.release()
        pdamagedSFX.release()
        edamagedSFX.release()
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
            pdamagedSFX.start()
        }
        else{
            edamagedSFX.start()
        }
    }

    fun playClick(){
        clickSFX.start()
    }

}