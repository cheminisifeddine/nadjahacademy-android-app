package dz.nadjahacademy.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dz.nadjahacademy.app.R
import kotlinx.coroutines.*

@AndroidEntryPoint
class VideoDownloadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Preparing downloads…"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_DOWNLOAD -> {
                val lessonId = intent.getStringExtra(EXTRA_LESSON_ID) ?: return START_NOT_STICKY
                val url = intent.getStringExtra(EXTRA_VIDEO_URL) ?: return START_NOT_STICKY
                val title = intent.getStringExtra(EXTRA_LESSON_TITLE) ?: lessonId
                serviceScope.launch { downloadVideo(lessonId, url, title) }
            }
            ACTION_CANCEL -> {
                val lessonId = intent.getStringExtra(EXTRA_LESSON_ID)
                serviceScope.coroutineContext.cancelChildren()
                if (lessonId != null) cancelDownload(lessonId)
            }
            ACTION_STOP_SERVICE -> {
                serviceScope.cancel()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_STICKY
    }

    private suspend fun downloadVideo(lessonId: String, url: String, title: String) {
        updateNotification("Downloading: $title")
        // Actual byte streaming would happen here via OkHttp or DownloadManager.
        // This skeleton delegates to the OS DownloadManager for reliability.
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        val request = android.app.DownloadManager.Request(android.net.Uri.parse(url))
            .setTitle(title)
            .setDescription("Nadjah Academy offline lesson")
            .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(
                this,
                android.os.Environment.DIRECTORY_MOVIES,
                "nadjah/$lessonId.mp4",
            )
        downloadManager.enqueue(request)
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun cancelDownload(lessonId: String) {
        // Cancel via DownloadManager if needed
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Downloads",
                NotificationManager.IMPORTANCE_LOW,
            ).apply { description = "Nadjah Academy offline video downloads" }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(message: String): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Nadjah Academy")
            .setContentText(message)
            .setOngoing(true)
            .build()

    private fun updateNotification(message: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(message))
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "nadjah_downloads"
        const val NOTIFICATION_ID = 2001
        const val ACTION_DOWNLOAD = "dz.nadjahacademy.action.DOWNLOAD"
        const val ACTION_CANCEL = "dz.nadjahacademy.action.CANCEL_DOWNLOAD"
        const val ACTION_STOP_SERVICE = "dz.nadjahacademy.action.STOP_DOWNLOAD_SERVICE"
        const val EXTRA_LESSON_ID = "lessonId"
        const val EXTRA_VIDEO_URL = "videoUrl"
        const val EXTRA_LESSON_TITLE = "lessonTitle"

        fun buildStartIntent(context: Context, lessonId: String, url: String, title: String): Intent =
            Intent(context, VideoDownloadService::class.java).apply {
                action = ACTION_DOWNLOAD
                putExtra(EXTRA_LESSON_ID, lessonId)
                putExtra(EXTRA_VIDEO_URL, url)
                putExtra(EXTRA_LESSON_TITLE, title)
            }
    }
}
