package com.github.doandadr.petualanganhijaiyah.ml

interface Recognition {
    fun predict(input: Array<FloatArray>): FloatArray
}
