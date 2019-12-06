package ru.itis.homework

object SongsRepository {
    var songsList = mutableListOf(
        Song("The Open", "Alicks", 2015,R.drawable.open, R.raw.audio_open),
        Song("Otherside", "Red Hot Chili Peppers", 1999, R.drawable.otherside, R.raw.audio_otherside),
        Song("Blood Bank", "Bon Iver", 2009, R.drawable.bank, R.raw.audio_bank),
        Song("Green Eyes", "Coldplay", 2002, R.drawable.green, R.raw.audio_green),
        Song("Mothers", "Daughter", 2019, R.drawable.mothers, R.raw.audio_mothers),
        Song("Autumn", "Message To Bears", 2009, R.drawable.autumn, R.raw.audio_autumn),
        Song("Stay With Me", "NO", 2014, R.drawable.no, R.raw.audio_no),
        Song("Intro", "The XX", 2016, R.drawable.intro, R.raw.audio_intro)
    )
}
