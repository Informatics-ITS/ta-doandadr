package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.Timer
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAtlasAsset
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SPLASH
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.table


class SplashScreen(game: Main) : BaseScreen(game) {
    private lateinit var bgSplash: Texture

    override fun show() {
        super.show()
        val old = System.currentTimeMillis()

        val assetRefs = gdxArrayOf(
            TextureAsset.entries.map {
                game.assets.loadAsync(it.descriptor)
            },
            TextureAtlasAsset.entries.map { game.assets.loadAsync(it.descriptor) },
            SoundAsset.entries.map { game.assets.loadAsync(it.descriptor) }
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            log.debug { "Time for assets to be loaded: ${System.currentTimeMillis() - old} ms" }
            if (Gdx.app.logLevel == Application.LOG_DEBUG) {
                loadScreens()
                return@launch
            }
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    loadScreens()
                }
            }, SPLASH_DELAY_SECONDS)

        }

        setupUI()
    }

    private fun setupUI() {
        bgSplash = Texture(Gdx.files.internal("graphics/bg/bg-home.png"))

        stage.actors {
            image(bgSplash) {
                setFillParent(true)
                setScaling(Scaling.fill)
            }
            table {
                setFillParent(true)

                image(Drawables.BISMILLAH_WHITE.drawable) {
                    setOrigin(Align.center)
                }

                row()
                label("PETUALANGAN\nHIJAIYAH", Labels.PRIMARY_GREEN_WHITE_BORDER.style) {
                    setAlignment(Align.center)
                    setFontScale(SCALE_FONT_SPLASH)
                    it.spaceTop(20f)
                }

                row()
                label(
                    "ayo berjelajah sambil belajar hijaiyah!",
                    Labels.SECONDARY_GREEN_WHITE_BORDER.style
                ) {
                    setAlignment(Align.center)
                    setFontScale(SCALE_FONT_SMALL)
                    it.padBottom(300f)
                }
            }
        }
    }

    private fun loadScreens() {
        game.addScreen(HomeScreen(game))
        game.addScreen(MapScreen(game))
        game.addScreen(LevelScreen(game))
        game.addScreen(PracticeScreen(game))
        game.addScreen(StartScreen(game))
        game.addScreen(FinishScreen(game))

        transitionOut<HomeScreen>()
    }

    override fun dispose() {

        bgSplash.disposeSafely()
    }

    companion object {
        private val log = logger<SplashScreen>()
        private const val SPLASH_DELAY_SECONDS = 0.3f
    }
}
