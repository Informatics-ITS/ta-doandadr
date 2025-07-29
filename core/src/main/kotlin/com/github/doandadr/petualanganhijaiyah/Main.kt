package com.github.doandadr.petualanganhijaiyah

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.audio.DefaultAudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ml.Recognition
import com.github.doandadr.petualanganhijaiyah.screen.SplashScreen
import com.ray3k.stripe.FreeTypeSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.log.logger
import ktx.scene2d.Scene2DSkin

const val SCREEN_W = 720f
const val SCREEN_H = 1280f
private const val PREF_NAME = "petualangan-hijaiyah"

class Main(val recognition: Recognition) : KtxGame<KtxScreen>() {
    val uiViewport = ExtendViewport(SCREEN_W, SCREEN_H)
    val batch: Batch by lazy { SpriteBatch() }
    val stage: Stage by lazy {
        // Set stage to process input
        val result = Stage(uiViewport, batch)
        Gdx.input.inputProcessor = result
        // Load the skin json file
        val skin = FreeTypeSkin(Gdx.files.internal("skin/skin.json"))
        Scene2DSkin.defaultSkin = skin
        result
    }
    val assets: AssetStorage by lazy {
        // Initiate storage
        KtxAsync.initiate()
        AssetStorage()
    }
    val audioService: AudioService by lazy { DefaultAudioService(assets) }
    val preferences: Preferences by lazy { Gdx.app.getPreferences(PREF_NAME) }
    val gameEventManager by lazy { GameEventManager() }

    override fun create() {
        KtxAsync.initiate()
        log.debug { "Create game instance" }

        addScreen(SplashScreen(this))
        setScreen<SplashScreen>()
    }

    override fun dispose() {
        log.debug { "Sprites in batch: ${(batch as SpriteBatch).maxSpritesInBatch}" }
        batch.disposeSafely()
        assets.disposeSafely()
        stage.disposeSafely()
        super.dispose()
    }

    companion object {
        private val log = logger<Main>()
    }
}
