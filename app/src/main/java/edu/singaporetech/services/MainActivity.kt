package edu.singaporetech.services

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // SQL Database
    private lateinit var myRepository: MyRepository

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val flag = intent.getBooleanExtra("flag", false)
        Log.d("Main", flag.toString())

        // Initialise repositories
        myRepository = MyRepository(this.applicationContext)

        // Upon clicking, user will go to GameActivity
        binding.playButton.setOnClickListener {
            if (flag) {
                binding.continueText.visibility = View.VISIBLE
                binding.yesButton.visibility = View.VISIBLE
                binding.noButton.visibility = View.VISIBLE

                binding.playButton.visibility = View.INVISIBLE
                binding.scoreButton.visibility = View.INVISIBLE
                binding.creditsButton.visibility = View.INVISIBLE

                binding.yesButton.setOnClickListener {
                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                    startActivity(intent)
                }

                binding.noButton.setOnClickListener {
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
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }

        // Upon clicking, user will go to CreditsActivity
        binding.creditsButton.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
    }
}