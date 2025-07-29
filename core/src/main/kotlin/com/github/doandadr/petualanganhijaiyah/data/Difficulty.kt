package com.github.doandadr.petualanganhijaiyah.data

enum class Difficulty(
    val timerSeconds: Float,
    val maxHealth: Int,
    val choices: Int,
    val choicesMatch: Int = choices + 2
) {
    EASY(240f, 5, 3),
    MEDIUM(180f, 4, 4),
    HARD(120f, 3, 5)
}
