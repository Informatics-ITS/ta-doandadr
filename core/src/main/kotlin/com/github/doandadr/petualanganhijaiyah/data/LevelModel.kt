package com.github.doandadr.petualanganhijaiyah.data

data class LevelModel(
    val number: Int,
    val name: String,
    val isTimed: Boolean,
    val timerSeconds: Float,
    val isHealthCounted: Boolean,
    val maxHealth: Int,
    val isScored: Boolean,
    val stages: List<StageModel>,
    val bgIndex: Int,
    val musicIndex: Int,
)
