package ru.itis.homework

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

 object Utils {

    const val DELAY_WEATHER_CIRCLE = 100L
    const val COUNT_WEATHER_CIRCLE = 20
    const val DELAY_WEATHER_SEARCH = 1000L

    private const val FORMULA_NUMBER_ONE = 45
    private const val FORMULA_NUMBER_TWO = 0.5
    private const val FORMULA_NUMBER_THREE = 8

    private const val TEMP_VERY_COLD = -20.0
    private const val TEMP_NORMAL_COLD = -10.0
    private const val TEMP_LOW_WARM = 0.0
    private const val TEMP_NORMAL_WARM = 10.0
    private const val TEMP_VERY_WARM = 20.0

    fun degToDirection(deg: Int): String {
        val temp = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val dir = deg / FORMULA_NUMBER_ONE + FORMULA_NUMBER_TWO
        return temp[(dir % FORMULA_NUMBER_THREE).toInt()]
    }

    fun getImageIcon(icon: String): Int {
        val map = mapOf(Pair("01d", R.drawable.day_clear_sky),
            Pair("02d", R.drawable.day_few_clouds),
            Pair("03d", R.drawable.day_scattered_clouds),
            Pair("04d", R.drawable.day_broken_clouds),
            Pair("09d", R.drawable.day_shower_rain),
            Pair("10d", R.drawable.day_rain),
            Pair("11d", R.drawable.day_thunderstorm),
            Pair("13d", R.drawable.day_snow),
            Pair("50d", R.drawable.day_mist),
            Pair("01n", R.drawable.night_clear_sky),
            Pair("02n", R.drawable.night_few_clouds),
            Pair("03n", R.drawable.night_scattered_clouds),
            Pair("04n", R.drawable.night_broken_clouds),
            Pair("09n", R.drawable.night_shower_rain),
            Pair("10n", R.drawable.night_rain),
            Pair("11n", R.drawable.night_thunderstorm),
            Pair("13n", R.drawable.night_snow),
            Pair("50n", R.drawable.night_mist)
        )
        return map[icon] ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getTempColor(context: Context, temp: Double): Int {
        var color = 0;
        when (temp) {
            in Double.NEGATIVE_INFINITY .. TEMP_VERY_COLD -> color = context.getColor(R.color.colorVeryCold)
            in TEMP_VERY_COLD .. TEMP_NORMAL_COLD -> color = context.getColor(R.color.colorNormalCold)
            in TEMP_NORMAL_COLD .. TEMP_LOW_WARM -> color = context.getColor(R.color.colorLowCold)
            in TEMP_LOW_WARM .. TEMP_NORMAL_WARM -> color = context.getColor(R.color.colorLowWarm)
            in TEMP_NORMAL_WARM .. TEMP_VERY_WARM -> color = context.getColor(R.color.colorNormalWarm)
            in TEMP_VERY_WARM .. Double.POSITIVE_INFINITY -> color = context.getColor(R.color.colorVeryWarm)
        }
        return color
    }
}
