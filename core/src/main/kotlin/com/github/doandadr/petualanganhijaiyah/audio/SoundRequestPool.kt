package com.github.doandadr.petualanganhijaiyah.audio

import com.badlogic.gdx.utils.Pool

internal class SoundRequestPool :
    Pool<SoundRequest>() {
    override fun newObject(): SoundRequest = SoundRequest()
}
