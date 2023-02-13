package edu.singaporetech.services

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityServiceBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}