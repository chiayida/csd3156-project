package edu.singaporetech.services

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityHighscoreBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HighscoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHighscoreBinding
    var soundSys = SoundSystem(this)

    // SQL Database
    private lateinit var myRepository: MyRepository


    @SuppressLint("SetTextI18n", "RtlHardcoded")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHighscoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        soundSys.InitializeSounds()
        // Get repository
        myRepository = MyRepository(this.applicationContext)

        // Load all time top 5 scores + time (database)
        GlobalScope.launch {
            val scores = myRepository.getTopHighscores()

            for (i in scores.indices) {
                when (i) {
                    0 -> {
                        binding.score1Text.text = "1. " + scores[i].score.toString() +
                                " (" +  String.format("%.2f", scores[i].aliveTime) + "s)"

                        binding.score1Text.gravity = Gravity.LEFT
                    }
                    1 -> {
                        binding.score2Text.text = "2. " + scores[i].score.toString() +
                                " (" +  String.format("%.2f", scores[i].aliveTime) + "s)"
                        binding.score2Text.gravity = Gravity.LEFT
                    }
                    2 -> {
                        binding.score3Text.text = "3. " + scores[i].score.toString() +
                                " (" +  String.format("%.2f", scores[i].aliveTime) + "s)"
                        binding.score3Text.gravity = Gravity.LEFT
                    }
                    3 -> {
                        binding.score4Text.text = "4. " + scores[i].score.toString() +
                                " (" +  String.format("%.2f", scores[i].aliveTime) + "s)"
                        binding.score4Text.gravity = Gravity.LEFT
                    }
                    4 -> {
                        binding.score5Text.text = "5. " + scores[i].score.toString() +
                                " (" +  String.format("%.2f", scores[i].aliveTime) + "s)"
                        binding.score5Text.gravity = Gravity.LEFT
                    }
                }
            }
        }


        // Upon clicking, user will go to HighscoreActivity
        binding.playagainButton.setOnClickListener {
            soundSys.playClick()
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Upon clicking, user will go to CreditsActivity
        binding.mainmenuButton.setOnClickListener {
            soundSys.playClick()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        soundSys.ReleaseSounds()
    }
}