package edu.singaporetech.services

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.app.Activity.RESULT_OK


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // SOUNDS
    var soundSys = SoundSystem(this)
    // SQL Database
    private lateinit var myRepository: MyRepository
    private var flag = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggleContinue(false)
        soundSys.InitializeSounds()
        flag = intent.getBooleanExtra("flag", false)
        Log.d("Main", flag.toString())

        // Initialise repositories
        myRepository = MyRepository(this.applicationContext)

        // Upon clicking, user will go to GameActivity
        binding.playButton.setOnClickListener {
            soundSys.playClick()
            if (flag) {
                toggleContinue(true)

                binding.yesButton.setOnClickListener {
                    soundSys.playClick()
                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                    intent.putExtra("Resume", true)
                    startActivity(intent)
                }

                binding.noButton.setOnClickListener {
                    soundSys.playClick()
                    GlobalScope.launch {
                        myRepository.deleteAllPlayers()
                        myRepository.deleteAllEnemies()
                        myRepository.deleteAllProjectiles()
                    }

                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                startActivity(intent)
            }
        }

        // Upon clicking, user will go to HighscoreActivity
        binding.scoreButton.setOnClickListener {
            soundSys.playClick()
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }

        // Upon clicking, user will go to CreditsActivity
        binding.creditsButton.setOnClickListener {
            soundSys.playClick()
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        soundSys.playGameBGM()
    }
    override fun onPause() {
        super.onPause()
        soundSys.StopSounds()
    }
    override fun onDestroy() {
        super.onDestroy()
        soundSys.ReleaseSounds()
    }
    override fun onResume() {
        super.onResume()
        flag = intent.getBooleanExtra("flag", false)
        Log.i("Main", flag.toString())
        toggleContinue(false)
    }
    fun toggleContinue(show : Boolean){
        if(show){
            binding.continueText.visibility = View.VISIBLE
            binding.yesButton.visibility = View.VISIBLE
            binding.noButton.visibility = View.VISIBLE

            binding.playButton.visibility = View.INVISIBLE
            binding.scoreButton.visibility = View.INVISIBLE
            binding.creditsButton.visibility = View.INVISIBLE
        }
        else{
            binding.continueText.visibility = View.INVISIBLE
            binding.yesButton.visibility = View.INVISIBLE
            binding.noButton.visibility = View.INVISIBLE

            binding.playButton.visibility = View.VISIBLE
            binding.scoreButton.visibility = View.VISIBLE
            binding.creditsButton.visibility = View.VISIBLE
        }

    }
}