package ru.itis.homework

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_song.*
import ru.itis.homework.MediaPlayerService.LocalBinder

class SongActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val EXTRA_NAME = "title"
        private const val EXTRA_ARTIST = "artist"
        private const val EXTRA_YEAR = "year"
        private const val EXTRA_COVER = "cover"
        private const val EXTRA_AUDIO = "audio"
        private const val EXTRA_INDEX = "index"
    }

    private var currentSongIndex: Int = 0
    lateinit var currentSong: Song
    private var stopPlayImage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        val intFilter1 = IntentFilter(MediaPlayerService.BROADCAST_NEXT_SONG_AFTER_COMPLETION)
        registerReceiver(playNextAudio, intFilter1)
        val intFilter2 = IntentFilter()
        intFilter2.addAction(MediaPlayerService.BROADCAST_CHANGE_UI_NEXT_SONG)
        intFilter2.addAction(MediaPlayerService.BROADCAST_CHANGE_UI_PREV_SONG)
        intFilter2.addAction(MediaPlayerService.BROADCAST_CHANGE_UI_PLAYPAUSE_SONG)
        registerReceiver(changeUIAfterSkipTrack, intFilter2)

        val name = intent?.extras?.getString(EXTRA_NAME)
        val artist = intent?.extras?.getString(EXTRA_ARTIST)
        val year = intent?.extras?.getInt(EXTRA_YEAR)
        val coverPhoto = intent?.extras?.getInt(EXTRA_COVER) ?: -1
        currentSongIndex = intent?.extras?.getInt(EXTRA_INDEX) ?: -1
        currentSong = SongsRepository.songsList[currentSongIndex]

        tv_title.text = name
        tv_artist.text = artist
        tv_year.text = year.toString()
        iv_cover.setImageResource(coverPhoto)

        ibtn_play.setOnClickListener(this)
        ibtn_next.setOnClickListener(this)
        ibtn_prev.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ibtn_play -> {
                if (stopPlayImage) {
                    ibtn_play.setImageResource(R.drawable.ic_play_black_48dp)
                    stopPlayImage = false
                    mService?.pauseMedia()
                }
                else {
                    ibtn_play.setImageResource(R.drawable.ic_stop_black_48dp)
                    stopPlayImage = true
                    mService?.resumeMedia()
                }
            }
            R.id.ibtn_next -> {
                if (currentSongIndex == SongsRepository.songsList.size - 1) {
                    currentSongIndex = 0
                }
                else {
                    currentSongIndex++
                }
                currentSong = SongsRepository.songsList[currentSongIndex]

                tv_title.text = currentSong.title
                tv_artist.text = currentSong.artist
                tv_year.text = currentSong.year.toString()
                iv_cover.setImageResource(currentSong.cover)

                mService?.nextMedia()
            }
            R.id.ibtn_prev -> {
                if (currentSongIndex == 0) {
                    currentSongIndex = SongsRepository.songsList.size - 1
                }
                else {
                    currentSongIndex--
                }
                currentSong = SongsRepository.songsList[currentSongIndex]

                tv_title.text = currentSong.title
                tv_artist.text = currentSong.artist
                tv_year.text = currentSong.year.toString()
                iv_cover.setImageResource(currentSong.cover)

                mService?.prevMedia()
            }
        }
    }

    var mService: MediaPlayerService? = null
    var mBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MediaPlayerService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            unbindService(mConnection)
        }
        unregisterReceiver(playNextAudio)
        unregisterReceiver(changeUIAfterSkipTrack)
    }

    private val playNextAudio: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (currentSongIndex == SongsRepository.songsList.size - 1) {
                currentSongIndex = 0
            }
            else {
                currentSongIndex++
            }
            currentSong = SongsRepository.songsList[currentSongIndex]

            tv_title.text = currentSong.title
            tv_artist.text = currentSong.artist
            tv_year.text = currentSong.year.toString()
            iv_cover.setImageResource(currentSong.cover)

            mService?.nextMedia()
        }
    }

    private val changeUIAfterSkipTrack: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MediaPlayerService.BROADCAST_CHANGE_UI_NEXT_SONG) {
                if (currentSongIndex == SongsRepository.songsList.size - 1) {
                    currentSongIndex = 0
                }
                else {
                    currentSongIndex++
                }
            }
            else if (intent?.action == MediaPlayerService.BROADCAST_CHANGE_UI_PREV_SONG) {
                if (currentSongIndex == 0) {
                    currentSongIndex = SongsRepository.songsList.size - 1
                }
                else {
                    currentSongIndex--
                }
            }
            else {
                if (stopPlayImage) {
                    ibtn_play.setImageResource(R.drawable.ic_play_black_48dp)
                    stopPlayImage = false
                    mService?.pauseMedia()
                }
                else {
                    ibtn_play.setImageResource(R.drawable.ic_stop_black_48dp)
                    stopPlayImage = true
                    mService?.resumeMedia()
                }
            }
            currentSong = SongsRepository.songsList[currentSongIndex]

            tv_title.text = currentSong.title
            tv_artist.text = currentSong.artist
            tv_year.text = currentSong.year.toString()
            iv_cover.setImageResource(currentSong.cover)
        }
    }
}
