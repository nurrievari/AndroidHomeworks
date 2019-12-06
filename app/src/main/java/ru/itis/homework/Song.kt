package ru.itis.homework

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Song (var title: String,
                 var artist: String,
                 var year: Int,
                 @DrawableRes var cover: Int,
                 @RawRes var audio: Int)
