package com.github.doandadr.petualanganhijaiyah.audio

import com.badlogic.gdx.utils.Pool
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset


internal class SoundRequest : Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 1f

    override fun reset() {
        volume = 1f
    }
}

