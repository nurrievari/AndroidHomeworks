package ru.itis.homework

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SongAdapter (var list: List<Song>,
                   private val clickLambda: (Song) -> Unit) : RecyclerView.Adapter<SongItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemHolder = SongItemHolder.create(parent, clickLambda)

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SongItemHolder, position: Int) = holder.bind(list[position])

}
