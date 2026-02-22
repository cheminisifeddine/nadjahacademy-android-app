package dz.nadjahacademy.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NadjahApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channels = listOf(
                NotificationChannel(
                    "nadjah_default",
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "General notifications" },
                NotificationChannel(
                    "nadjah_course_updates",
                    "Course Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Course announcements and updates" },
                NotificationChannel(
                    "nadjah_reminders",
                    "Learning Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Daily learning reminders" },
                NotificationChannel(
                    "nadjah_community",
                    "Community",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Replies and discussion updates" },
                NotificationChannel(
                    "nadjah_promotions",
                    "Promotions",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Special offers and deals" },
                NotificationChannel(
                    "nadjah_downloads",
                    "Downloads",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Video download progress" },
            )
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
}
