package com.example.mealtracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mealtracker.R

@RequiresApi(Build.VERSION_CODES.O)
class DailyReminderWorker (
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        makeStatusNotification("Have you logged your meals today?", applicationContext)
        return Result.success()
    }


    private fun makeStatusNotification(message: String, context: Context) {
        val name = "MealReminderChannel"
        val description = "Channel for daily meal reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelId = "MealTrackerReminder"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Meal Tracker")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.notify(1, builder.build())
    }
}