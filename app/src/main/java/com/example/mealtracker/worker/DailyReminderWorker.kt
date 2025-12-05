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

/**
 * Worker class responsible for sending daily reminders to the user.
 * Extends [CoroutineWorker] to perform background work efficiently using Coroutines.
 * This worker is scheduled by [WorkManager] in the MainActivity.
 */
@RequiresApi(Build.VERSION_CODES.O)
class DailyReminderWorker (
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams){

    /**
     * The main entry point for the background work.
     * This method is called by the WorkManager when the constraints are met.
     *
     * @return [Result.success] to indicate the work finished successfully.
     */
    override suspend fun doWork(): Result {
        makeStatusNotification("Have you logged your meals today?", applicationContext)
        return Result.success()
    }

    /**
     * Helper method to create and display the notification.
     * Handles the creation of the NotificationChannel which is mandatory for Android O and above.
     *
     * @param message The text body of the notification.
     * @param context The application context.
     */
    private fun makeStatusNotification(message: String, context: Context) {
        // Constants for the Notification Channel
        val name = "MealReminderChannel"
        val description = "Channel for daily meal reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelId = "MealTrackerReminder"

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description

            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }

        // Build the notification using NotificationCompat for backward compatibility
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Small icon to appear in the status bar
            .setContentTitle("Meal Tracker") // Title of the notification
            .setContentText(message) // Body text provided by the caller
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority ensures heads-up notification
            .setAutoCancel(true) // Automatically dismiss the notification when the user taps it

        // Get the NotificationManager and show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        // Notify with a fixed ID (1). This means subsequent notifications will replace existing ones.
        notificationManager?.notify(1, builder.build())
    }
}