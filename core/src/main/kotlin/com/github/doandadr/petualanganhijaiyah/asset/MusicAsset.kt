package com.github.doandadr.petualanganhijaiyah.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music

enum class MusicAsset(
    fileName: String,
    val volume: Float = 0.6f,
    directory: String = "audio/music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName", Music::class.java)
) {
    DESERT("desert.mp3", 0.15f), // TODO copyrighted
    DESERT_ALT("desert-2.mp3",0.2f),
    FIELD("forest.mp3"),
    HOME("home.mp3", 0.5f),
    MAP("map.mp3"),
    SNOW("snow.mp3"),
    VICTORY("victory.mp3", 0.3f),
}
