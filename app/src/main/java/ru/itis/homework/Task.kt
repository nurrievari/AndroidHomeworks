package ru.itis.homework

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var description: String?,
    var date: Date,
    var isDone: Boolean
)
