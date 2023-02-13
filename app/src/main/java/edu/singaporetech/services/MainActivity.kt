package edu.singaporetech.services

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Upon clicking, user will go to GameActivity
        binding.playButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Upon clicking, user will go to CreditsActivity
        binding.creditsButton.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
    }
}