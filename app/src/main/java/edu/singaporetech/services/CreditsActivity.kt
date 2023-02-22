package edu.singaporetech.services

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityCreditsBinding


class CreditsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditsBinding
    // SOUNDS
    private var soundSys = SoundSystem(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundSys.initializeSounds()
        // Upon clicking, user will go to MainActivity
        binding.backButton.setOnClickListener {
            soundSys.playClick()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        soundSys.releaseSounds()
    }
}