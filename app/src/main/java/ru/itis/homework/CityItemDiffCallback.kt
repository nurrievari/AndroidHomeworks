package ru.itis.homework

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil

class CityItemDiffCallback(
    private var oldList: MutableList<WeatherResponse>,
    private var newList: MutableList<WeatherResponse>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newItem = newList[newItemPosition]
        val oldItem = oldList[oldItemPosition]
        val diffBundle = Bundle()
        if (newItem.name !== oldItem.name) {
            diffBundle.putString("name", newItem.name)
        }
        if (newItem.main.temp.compareTo(oldItem.main.temp) == 0) {
            diffBundle.putDouble("temp", newItem.main.temp)
        }

        return if (diffBundle.size() == 0) null else diffBundle
    }

}
