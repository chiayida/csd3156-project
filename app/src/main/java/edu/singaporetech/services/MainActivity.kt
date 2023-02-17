package edu.singaporetech.services

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // SQL Database
    private lateinit var highscoreRepository: HighscoreRepository

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialise repositories
        highscoreRepository = HighscoreRepository(this.applicationContext)


        // Run it once be before submission to reset database
        /*
        GlobalScope.launch {
            highscoreRepository.deleteAllHighscore()
        }
        */

        // Upon clicking, user will go to HighscoreActivity
        binding.playButton.setOnClickListener {
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