package edu.singaporetech.services

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import edu.singaporetech.services.databinding.ActivityServiceBinding
import androidx.work.WorkManager

class ServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServiceBinding
    private var randomService: RandomService? = null
    private var isServiceBound = false
    private lateinit var workManager: WorkManager

    companion object {
        const val KEY_INPUT = "input"
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Local binding
            val binder = service as RandomService.LocalBinder
            randomService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isServiceBound = false
        }
    }

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(this)

        val textEditPrime = binding.textEditPrime
        val buttonRandom = binding.buttonRandom
        val buttonCheck = binding.buttonCheck
        val textViewResult = binding.textViewResult

        // Binds connection to RandomService upon start.
        val intent = Intent(this, RandomService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)


        // Get the current (last) generated random number from the list within RandomService.
        buttonRandom.setOnClickListener {
            if (isServiceBound) {
                val latestRandomNumber = randomService!!.getCurrentRandomNumber()
                textEditPrime.setText(latestRandomNumber.toString())
            }
        }


        // 3. Process the PrimeWorker to do the required work, scheduled once per click.
        buttonCheck.setOnClickListener {
            var input : Long = 0
            if (!textEditPrime.text.isNullOrBlank()) {
                input = textEditPrime.text.toString().toLong()
            }

            // 4. Work will only be done if the user’s device has enough battery
            val constraints = androidx.work.Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            // The current number shown in the top EditText field will be sent as inputData.
            val data = Data.Builder()
                .putLong(KEY_INPUT, input)
                .build()

            val workRequest = OneTimeWorkRequest.Builder(PrimeWorker::class.java)
                .setInputData(data)
                .setConstraints(constraints)
                .build()

            // Process the PrimeWorker to do work
            workManager.enqueue(workRequest)
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .observe(this) { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        if (!textEditPrime.text.isNullOrBlank()) {
                            val result = workInfo.outputData.getBoolean(PrimeWorker.KEY_RESULT, false)
                            val timeTaken = workInfo.outputData.getLong(PrimeWorker.KEY_TIME, 0)
                            val resultText = if (result) "PRIME" else "NOT PRIME"

                            // 5. Once the work is done, show the output.
                            textViewResult.text = "$input IS $resultText. Time taken: $timeTaken ms."
                        }
                    }
                }
        }
    }

    // Unbinds connection to RandomService when stopped
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isServiceBound = false
    }
}