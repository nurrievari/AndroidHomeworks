package ru.itis.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var adapter: SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = SongAdapter(SongsRepository.songsList) {
            goToItem(it)
        }
        rv_songs.adapter = adapter
    }

    var mService: MediaPlayerService? = null
    var mBound = false
    private val mConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaPlayerService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun goToItem(song: Song) {
        val index = SongsRepository.songsList.indexOf(song)
        if (!mBound) {
            val intent = Intent(this, MediaPlayerService::class.java)
            intent.putExtra(EXTRA_INDEX, index)
            intent.putExtra(EXTRA_AUDIO, song.audio)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
            else {
                startService(intent)
            }
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
        else {
            val intent = Intent(BROADCAST_ACTION)
            intent.putExtra(EXTRA_INDEX, index)
            sendBroadcast(intent)
        }
        startActivity((Intent(this, SongActivity::class.java).apply {
            putExtra(EXTRA_NAME, song.title)
            putExtra(EXTRA_ARTIST, song.artist)
            putExtra(EXTRA_YEAR, song.year)
            putExtra(EXTRA_COVER, song.cover)
            putExtra(EXTRA_AUDIO, song.audio)
            putExtra(EXTRA_INDEX, index)
        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            unbindService(mConnection)
        }
    }

    companion object {
        const val BROADCAST_ACTION = "ru.itis.homework.broadcast_newAudio"

        private const val EXTRA_NAME = "title"
        private const val EXTRA_ARTIST = "artist"
        private const val EXTRA_YEAR = "year"
        private const val EXTRA_COVER = "cover"
        private const val EXTRA_AUDIO = "audio"
        private const val EXTRA_INDEX = "index"
    }
}
