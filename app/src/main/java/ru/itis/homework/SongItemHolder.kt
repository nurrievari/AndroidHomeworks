package ru.itis.homework

import kotlinx.android.synthetic.main.item_song.*
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

class SongItemHolder(override val containerView: View,
                     private val clickLambda: (Song) -> Unit)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(song: Song) {
        val description = song.artist + ", " + song.year
        tv_title.text = song.title
        tv_description.text = description
        iv_cover.setImageResource(song.cover)
        itemView.setOnClickListener {
            clickLambda(song)
        }
    }

    companion object {

        fun create(parent: ViewGroup, clickLambda: (Song) -> Unit) =
            SongItemHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false),
                clickLambda
            )
    }

}
