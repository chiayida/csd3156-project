package edu.singaporetech.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class RandomService : Service() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val randomNumberList = mutableListOf<Int>()
    private val binder = LocalBinder()

    // 1. Allows local binding
    inner class LocalBinder : Binder() {
        fun getService() : RandomService = this@RandomService
    }

    // 3. Binds to ServiceActivity upon start
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    // 2.  Generates a random 9 digit number every 1 second (1000 ms)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            while (true) {
                // Generate a random 9 digit number, add to list
                val randomNumber = (100000000..999999999).random()
                randomNumberList.add(randomNumber)
                Log.d("RandomService: ", "$randomNumber added")

                // Delay 1 second (1000ms)
                delay(1000)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // 4. Returns last generated random number from list
    fun getCurrentRandomNumber(): Int {
        return randomNumberList.last()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
