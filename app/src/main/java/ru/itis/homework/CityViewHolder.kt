package ru.itis.homework

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_city.*

class CityViewHolder(override val containerView: View,
                     private val clickLambda: (Int) -> Unit)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    @RequiresApi(Build.VERSION_CODES.M)
    fun bind(weather: WeatherResponse) {
        tv_city.text = weather.name
        Log.e("temp", weather.toString())
        val temp =
            if (weather.main.temp.toInt() > 0) {
                "+${weather.main.temp.toInt()}"
            } else {
                weather.main.temp.toInt().toString()
            }
        tv_temp.text = temp
        tv_temp.setTextColor(Utils.getTempColor(containerView.context, weather.main.temp))
        itemView.setOnClickListener {
            clickLambda(weather.id)
        }
    }

    fun updateFromBundle(bundle: Bundle) {
        for (key in bundle.keySet()) {
            if (key == "name") {
                tv_city.text = bundle.getString(key)
            } else if (key == "temp") {
                tv_temp.text = bundle.getDouble(key).toString()
            }
        }
    }

    companion object {

        fun create(parent: ViewGroup, clickLambda: (Int) -> Unit) =
                CityViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false),
                        clickLambda
                )
    }

}
