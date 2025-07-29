package com.github.doandadr.petualanganhijaiyah.data

data class PlayerModel(
    var name: String = " ",
    var character: String = "girl", // boy, girl
    var totalScore: Float = 0f,
    var totalStar: Int = 0,
    var tutorials: MutableSet<Int> = mutableSetOf()
)
