package com.github.doandadr.petualanganhijaiyah.data

data class LevelSavedData(
    var number: Int = 0,
    var highScore: Float = 0f,
    var recordTime: Float = Float.MAX_VALUE,
    var starCount: Int = 0,
    var hasCompleted: Boolean = false,
)
