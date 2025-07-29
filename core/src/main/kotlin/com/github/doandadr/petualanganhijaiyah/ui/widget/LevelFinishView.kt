package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.IntAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.BANNER_TOP_PADDING
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_MEDIUM
import com.kotcrab.vis.ui.layout.FloatingGroup
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.horizontalGroup
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.vis.floatingGroup

class LevelFinishView(
    private val score: Float,
    private val star: Int,
    private val time: Float,
    private val state: State,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    private val isBestScore: Boolean,
    private val isBestTime: Boolean,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {

    private var bestScoreImg: Image
    private var bestTimeImg: Image
    private var timeView: Label
    val nextButton: ImageButton
    val repeatButton: ImageButton
    val menuButton: ImageButton
    private val scoreView: Label
    private val stars: StarWidget

    private val starStack: FloatingGroup

    private val starState: StarWidget.StarState = run {
        when (star) {
            1 -> StarWidget.StarState.ONE
            2 -> StarWidget.StarState.TWO
            3 -> StarWidget.StarState.THREE
            else -> StarWidget.StarState.ZERO
        }
    }

    init {
        label(if (state == State.FINISH) "BERHASIL" else "GAGAL", Labels.BANNER_ORANGE.style) {
            it.padBottom(BANNER_TOP_PADDING)
            setAlignment(Align.center)
            setFontScale(SCALE_FONT_MEDIUM)
        }

        row()
        table {
            it.prefWidth(POPUP_WIDTH).prefHeight(POPUP_HEIGHT)
            background = skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable)
            toBack()

            this@LevelFinishView.starStack = floatingGroup {
                it.padTop(80.0f).padBottom(30.0f).grow().colspan(3).align(Align.center)
                image(Drawables.LEVEL_FINISH_STARBOX.drawable) {
                    setScaling(Scaling.none)
                    setPosition(65f, 0f)
                }
                this@LevelFinishView.stars = starWidget(this@LevelFinishView.audioService) {
                    setPosition(78f, 0f)
                }
            }

            row()
            image(Drawables.ICON_DIAMOND.drawable) {
                it.padRight(-40.0f).spaceBottom(30.0f).align(Align.right)
                toFront()
                setScaling(Scaling.none)
            }
            this@LevelFinishView.scoreView = label("", Labels.TEXTBOX_BLUE_ROUNDED.style) {
                it.spaceBottom(30.0f).fillX()
                toBack()
                setAlignment(Align.center)
            }
            this@LevelFinishView.bestScoreImg = image(Drawables.ICON_PRIZE.drawable) {
                it.padLeft(-40.0f).spaceBottom(30.0f).align(Align.topLeft)
                setScaling(Scaling.none)
            }

            row()
            image(Drawables.ICON_CLOCK.drawable) {
                it.padRight(-40.0f).align(Align.right)
                toFront()
                setScaling(Scaling.none)
            }
            this@LevelFinishView.timeView = label("", Labels.TEXTBOX_ORANGE_ROUNDED.style) {
                it.fillX()
                toBack()
                setAlignment(Align.center)
            }
            this@LevelFinishView.bestTimeImg = image(Drawables.ICON_PRIZE.drawable) {
                it.padLeft(-40.0f).align(Align.left)
                setScaling(Scaling.none)
            }

            row()
            horizontalGroup {
                it.padBottom(PADDING_INNER_SCREEN).padTop(-50f).expandY().align(Align.bottom).colspan(3)
                space(30f)
                this@LevelFinishView.menuButton =
                    imageButton(ImageButtons.MENU.style) {
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            this@LevelFinishView.audioService.play(SoundAsset.BUTTON_POP)
                        }
                    }
                this@LevelFinishView.repeatButton =
                    imageButton(ImageButtons.REPEAT.style) {
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            this@LevelFinishView.audioService.play(SoundAsset.BUTTON_POP)
                        }
                    }
                this@LevelFinishView.nextButton =
                    imageButton(ImageButtons.NEXT.style) {
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            this@LevelFinishView.audioService.play(SoundAsset.BUTTON_POP)
                        }
                    }
            }
        }

        loadWidget()
        showTutorial()
    }

    private fun showTutorial() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(menuButton, TutorialType.LEVEL_FINISH_MENU)
            gameEventManager.dispatchShowTutorialEvent(repeatButton, TutorialType.LEVEL_FINISH_REPEAT)
            if (nextButton.isVisible)
                gameEventManager.dispatchShowTutorialEvent(nextButton, TutorialType.LEVEL_FINISH_NEXT)
        }
    }

    private fun loadWidget() {
        stars.setAnimated(true)
        stars.setState(starState)
        scoreView.setText(score.toInt().toString())
        timeView.setText(formatMMSS(time))

        scoreView.clearActions()
        scoreView.addAction(IntAction(0, score.toInt(), 2f, Interpolation.fade))

        timeView.clearActions()
        timeView.addAction(IntAction(0, time.toInt(), 2f, Interpolation.fade))

        bestScoreImg.isVisible = isBestScore
        bestTimeImg.isVisible = isBestTime
    }

    private fun formatMMSS(time: Float): String {
        val min = (time / 60).toInt().toString().padStart(2, '0')
        val sec = (time % 60).toInt().toString().padStart(2, '0')
        return "$min:$sec"
    }

    enum class State {
        FINISH,
        FAILED,
    }

    companion object {
        private const val POPUP_WIDTH = 450f
        private const val POPUP_HEIGHT = 600f
    }
}

inline fun <S> KWidget<S>.levelFinishView(
    score: Float,
    star: Int,
    time: Float,
    state: LevelFinishView.State,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    bestScore: Boolean = false,
    bestTime: Boolean = false,
    init: LevelFinishView.(S) -> Unit = {}
) = actor(
    LevelFinishView(
        score,
        star,
        time,
        state,
        audioService,
        gameEventManager,
        bestScore,
        bestTime,
    ), init
)
