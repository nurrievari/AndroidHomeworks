package ru.itis.homework

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun dateToTimestamp(date: Date) = date.time

    @TypeConverter
    fun timastampToDate(timestamp: Long) = Date(timestamp)

}
