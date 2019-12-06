package ru.itis.homework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.media.app.NotificationCompat
import androidx.core.app.NotificationCompat as MediaNotificationCompat

class MediaPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val mBinder: IBinder? = LocalBinder()

    lateinit var audioList: List<Song>
    private var activeAudio = -1
    lateinit var activeSong: Song
    private var activeSongIndex : Int = -1
    private var curPosition = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val intent1 = IntentFilter(MainActivity.BROADCAST_ACTION)
        registerReceiver(playNewMedia, intent1)

        val intent2 = IntentFilter()
        intent2.addAction(BROADCAST_PAUSE_SONG)
        intent2.addAction(BROADCAST_RESUME_SONG)
        intent2.addAction(BROADCAST_NEXT_SONG)
        intent2.addAction(BROADCAST_PREV_SONG)
        registerReceiver(handleNotificationActions, intent2)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            audioList = SongsRepository.songsList.toMutableList()
            activeSongIndex = intent.extras?.getInt(EXTRA_INDEX) ?: -1
            activeSong = audioList[activeSongIndex]
            activeAudio = intent.extras?.getInt(EXTRA_AUDIO) ?: -1
            initMedia()
            playMedia()
        }
        buildNotification(BROADCAST_RESUME_SONG)
        return START_STICKY
    }

    inner class LocalBinder : Binder() {
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer.release()
        }
        unregisterReceiver(playNewMedia)
        unregisterReceiver(handleNotificationActions)
    }

    private fun initMedia() {
        mediaPlayer = MediaPlayer.create(this, activeAudio)

        mediaPlayer.setOnCompletionListener {
            val intent1 = Intent(BROADCAST_NEXT_SONG_AFTER_COMPLETION)
            sendBroadcast(intent1)
        }
    }

    fun playMedia() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        buildNotification(BROADCAST_RESUME_SONG)
    }

    fun stopMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    fun pauseMedia() {
        if (mediaPlayer.isPlaying) {
            curPosition = mediaPlayer.currentPosition
            mediaPlayer.pause()
        }
        buildNotification(BROADCAST_PAUSE_SONG)
    }

    fun resumeMedia() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(curPosition)
            mediaPlayer.start()
        }
        buildNotification(BROADCAST_RESUME_SONG)
    }

    fun nextMedia() {
        if (activeSongIndex == audioList.size - 1) {
            activeSongIndex = 0
        }
        else {
            activeSongIndex++
        }
        activeAudio = audioList[activeSongIndex].audio
        activeSong = audioList[activeSongIndex]
        stopMedia()
        initMedia()
        playMedia()
    }

    fun prevMedia() {
        if (activeSongIndex == 0) {
            activeSongIndex = audioList.size - 1
        }
        else {
            activeSongIndex--
        }
        activeAudio = audioList[activeSongIndex].audio
        activeSong = audioList[activeSongIndex]
        stopMedia()
        initMedia()
        playMedia()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "media channel"
            val descriptionText = "channel for playing music"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(action: String) {
        val playStopPendingIntent: PendingIntent?
        val prevPendingIntent = createPendingIntent(BROADCAST_PREV_SONG)
        val nextPendingIntent = createPendingIntent(BROADCAST_NEXT_SONG)

        val iconPlayStop: Int
        if (action == BROADCAST_PAUSE_SONG) {
            iconPlayStop = R.drawable.ic_play_black_24dp
            playStopPendingIntent = createPendingIntent(BROADCAST_RESUME_SONG)
        }
        else {
            iconPlayStop = R.drawable.ic_stop_black_24dp
            playStopPendingIntent = createPendingIntent(BROADCAST_PAUSE_SONG)
        }

        val onClickIntent = Intent(this, SongActivity::class.java).apply {
            putExtra(EXTRA_NAME, activeSong.title)
            putExtra(EXTRA_ARTIST, activeSong.artist)
            putExtra(EXTRA_YEAR, activeSong.year)
            putExtra(EXTRA_COVER, activeSong.cover)
            putExtra(EXTRA_AUDIO, activeSong.audio)
            putExtra(EXTRA_INDEX, activeSongIndex)
        }
        val onClickPendIntent = PendingIntent.getActivity(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = MediaNotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_music_circle_outline_black_48dp)
            setContentTitle(activeSong.title)
            setContentText(activeSong.artist)
            setLargeIcon(BitmapFactory.decodeResource(resources, activeSong.cover))
            priority = MediaNotificationCompat.PRIORITY_DEFAULT
            setContentIntent(onClickPendIntent)
            addAction(R.drawable.ic_skip_previous_black_24dp, "prev", prevPendingIntent)
            addAction(iconPlayStop, "play_pause", playStopPendingIntent)
            addAction(R.drawable.ic_skip_next_black_24dp, "next", nextPendingIntent)
            setStyle(NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1)
            )
        }
        startForeground(NOTIFICATION_ID, builder.build())
//        with(NotificationManagerCompat.from(this)) {
//            notify(NOTIFICATION_ID, builder.build())
//        }
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action)
        return PendingIntent.getBroadcast(this, 0, intent, 0)
    }

    private val playNewMedia: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            activeSongIndex = intent?.extras?.getInt(EXTRA_INDEX) ?: -1
            if (activeSongIndex != -1) {
                activeAudio = audioList[activeSongIndex].audio
                activeSong = audioList[activeSongIndex]
            } else {
                stopSelf()
            }
            stopMedia()
            initMedia()
            playMedia()
        }
    }

    private val handleNotificationActions: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BROADCAST_RESUME_SONG -> {
                    resumeMedia()
                    val intent1 = Intent(BROADCAST_CHANGE_UI_PLAYPAUSE_SONG)
                    sendBroadcast(intent1)
                }
                BROADCAST_PAUSE_SONG -> {
                    pauseMedia()
                    val intent1 = Intent(BROADCAST_CHANGE_UI_PLAYPAUSE_SONG)
                    sendBroadcast(intent1)
                }
                BROADCAST_PREV_SONG -> {
                    prevMedia()
                    val intent1 = Intent(BROADCAST_CHANGE_UI_PREV_SONG)
                    sendBroadcast(intent1)
                }
                BROADCAST_NEXT_SONG -> {
                    nextMedia()
                    val intent1 = Intent(BROADCAST_CHANGE_UI_NEXT_SONG)
                    sendBroadcast(intent1)
                }
            }

        }
    }

    companion object {
        const val BROADCAST_NEXT_SONG_AFTER_COMPLETION = "ru.itis.homework.broadcast_completionAudio"
        const val BROADCAST_CHANGE_UI_NEXT_SONG = "ru.itis.homework.broadcast_change_ui_nextAudio"
        const val BROADCAST_CHANGE_UI_PREV_SONG = "ru.itis.homework.broadcast_change_ui_prevAudio"
        const val BROADCAST_CHANGE_UI_PLAYPAUSE_SONG = "ru.itis.homework.broadcast_change_ui_playpauseAudio"

        const val BROADCAST_RESUME_SONG = "ru.itis.homework.broadcast_resumeAudio"
        const val BROADCAST_PAUSE_SONG = "ru.itis.homework.broadcast_pauseAudio"
        const val BROADCAST_PREV_SONG = "ru.itis.homework.broadcast_prevAudio"
        const val BROADCAST_NEXT_SONG = "ru.itis.homework.broadcast_nextAudio"

        private const val EXTRA_NAME = "title"
        private const val EXTRA_ARTIST = "artist"
        private const val EXTRA_YEAR = "year"
        private const val EXTRA_COVER = "cover"
        private const val EXTRA_AUDIO = "audio"
        private const val EXTRA_INDEX = "index"

        const val NOTIFICATION_ID = 27
        const val CHANNEL_ID = "media channel id"
    }
}
