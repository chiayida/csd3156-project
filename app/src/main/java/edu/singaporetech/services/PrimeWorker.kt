package edu.singaporetech.services
import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class PrimeWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    companion object {
        const val KEY_INPUT = "input"
        const val KEY_RESULT = "result"
        const val KEY_TIME = "time"
    }

    // 3. Process the PrimeWorker to do the required work.
    // Number is received through inputData, checked for prime number and returns outputData
    override fun doWork(): Result {
        val startTime = System.currentTimeMillis()

        val number = inputData.getLong(KEY_INPUT, -1)
        val result = isPrime(number)

        val endTime = System.currentTimeMillis()
        val timeTaken = endTime - startTime

        val data = Data.Builder()
            .putBoolean(KEY_RESULT, result)
            .putLong(KEY_TIME, timeTaken)
            .build()

        Log.d("PrimeWorker", "Prime calculation for $number finished, $timeTaken ms.")

        return Result.success(data)
    }


    // 2. Test whether number is a Prime number using brute force method
    private fun isPrime(num: Long): Boolean {
        for (i in 2 until num) {
            if (num % i == 0L) {
                return false
            }
        }
        return true
    }
}
