package com.github.doandadr.petualanganhijaiyah.audio

import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset

interface AudioService {
    var enabled: Boolean
    var soundVolume: Float
    var musicVolume: Float
    fun play(soundAsset: SoundAsset, volume: Float = soundAsset.volume) = Unit
    fun play(musicAsset: MusicAsset, volume: Float = musicAsset.volume, loop: Boolean = true) = Unit
    fun randomize(volume: Float, min: Float, max: Float): Float
    fun pause() = Unit
    fun resume() = Unit
    fun stop(clearSounds: Boolean = true) = Unit
    fun stopSound(sound: SoundAsset) = Unit
    fun update() = Unit
}
