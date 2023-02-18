package edu.singaporetech.services

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityHighscoreBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HighscoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHighscoreBinding

    // SQL Database
    private lateinit var myRepository: MyRepository


    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHighscoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get repository
        myRepository = MyRepository(this.applicationContext)

        // Load all time top 5 scores + time (database)
        GlobalScope.launch {
            val scores = myRepository.getTopHighscores()

            for (i in scores.indices) {
                when (i) {
                    0 -> {
                        binding.score1Text.text = "1. " + scores[i].score.toString() +
                                " (" + scores[i].aliveTime + "s)"
                    }
                    1 -> {
                        binding.score2Text.text = "2. " + scores[i].score.toString() +
                                " (" + scores[i].aliveTime + "s)"
                    }
                    2 -> {
                        binding.score3Text.text = "3. " + scores[i].score.toString() +
                                " (" + scores[i].aliveTime + "s)"
                    }
                    3 -> {
                        binding.score4Text.text = "4. " + scores[i].score.toString() +
                                " (" + scores[i].aliveTime + "s)"
                    }
                    4 -> {
                        binding.score5Text.text = "5. " + scores[i].score.toString() +
                                " (" + scores[i].aliveTime + "s)"
                    }
                }
            }
        }


        // Upon clicking, user will go to HighscoreActivity
        binding.playagainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Upon clicking, user will go to CreditsActivity
        binding.mainmenuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}