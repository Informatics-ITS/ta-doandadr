package com.github.doandadr.petualanganhijaiyah.data

data class StageModel(
    val type: StageType,
    val choices: Int = 3,
    val rounds: Int = 5,
)
