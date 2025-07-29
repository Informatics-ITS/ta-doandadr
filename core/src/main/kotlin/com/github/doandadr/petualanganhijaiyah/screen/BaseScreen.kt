package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventListener
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialWidget
import ktx.actors.alpha
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.assets.disposeSafely
import ktx.log.logger

abstract class BaseScreen(
    val game: Main,
    val uiViewport: Viewport = game.uiViewport,
    val assets: AssetStorage = game.assets,
    val audioService: AudioService = game.audioService,
    val stage: Stage = game.stage,
    val batch: Batch = game.batch,
    val gameEventManager: GameEventManager = game.gameEventManager,
    val preferences: Preferences = game.preferences,
) : KtxScreen, GameEventListener {
    private lateinit var tutorialView: TutorialWidget
    lateinit var transitionImg : Image

    override fun show() {
        log.debug { "Show ${this::class.simpleName}" }
        gameEventManager.addGameEventListener(this)

        tutorialView = TutorialWidget(preferences, audioService)
        tutorialView.toFront()
        Gdx.app.postRunnable {
            stage.addActor(tutorialView)
        }
    }

    override fun render(delta: Float) {
        audioService.update()
        stage.run {
            viewport.apply()
            act()
            draw()
        }
        if (Gdx.app.logLevel == LOG_DEBUG) {
            debugMode()
        }
    }

    open fun debugMode() {}

    override fun resize(width: Int, height: Int) {
        uiViewport.update(width, height, true)
    }

    override fun hide() {
        log.debug { "Hide ${this::class.simpleName}" }
        stage.clear()
        audioService.stop()
        gameEventManager.removeGameEventListener(this)
    }

    override fun dispose() {
        stage.disposeSafely()
    }

    fun transitionIn() {
        initTransitionImage()
        stage.addActor(transitionImg)
        transitionImg.run {
            isVisible = true
            alpha = 1f
            clearActions()
            this += Actions.fadeOut(0.5f) + Actions.hide()
        }
    }
    inline fun <reified Type : KtxScreen> transitionOut() {
        initTransitionImage()
        stage.addActor(transitionImg)
        transitionImg.run {
            isVisible = true
            alpha = 0f
            clearActions()
            this += Actions.fadeIn(0.5f) + Actions.run { game.setScreen<Type>() }
        }
    }
    fun initTransitionImage() {
        transitionImg = Image(assets[TextureAsset.WHITE.descriptor]).apply {
            setFillParent(true)
            setScaling(Scaling.fill)
        }
    }

    override fun showTutorial(actor: Actor, type: TutorialType) {
        tutorialView.addTutorial(actor, type, stage)
    }

    companion object {
        private val log = logger<BaseScreen>()
    }
}
