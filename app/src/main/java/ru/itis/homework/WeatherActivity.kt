package ru.itis.homework

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.activity_weather.tv_temp
import kotlinx.android.synthetic.main.item_city.*
import kotlinx.coroutines.*

class WeatherActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val service: WeatherService by lazy {
        ApiFactory.weatherService
    }
    private var cityId: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        cityId = intent.extras?.getInt("id") ?: 0
        launch {
            val response = withContext(Dispatchers.IO) {
                service.weatherById(cityId)
            }
            tv_city_name.text = response.name
            tv_description.text = response.weather[0].description
            val temp =
                    if (response.main.temp.toInt() > 0) {
                        "+${response.main.temp.toInt()}"
                    } else {
                        response.main.temp.toInt().toString()
                    }

            tv_temp.text = temp
            tv_temp.setTextColor(Utils.getTempColor(this@WeatherActivity, response.main.temp))

            val wind = "wind: ${Utils.degToDirection(response.wind.deg)}, ${response.wind.speed.toInt()} m/s"
            tv_deg.text = wind
            val pressure = "pressure: ${response.main.pressure} mbars"
            tv_pressure.text = pressure
            val humidity = "humidity: ${response.main.humidity}%"
            tv_humidity.text = humidity
            iv_sky.setImageResource(Utils.getImageIcon(response.weather[0].icon))
        }
    }
}
