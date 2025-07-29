package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.HijaiyahJoined
import com.github.doandadr.petualanganhijaiyah.asset.ImageTextButtons
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SPACE_BETWEEN_BUTTONS
import com.github.doandadr.petualanganhijaiyah.ui.widget.JoinBox
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import com.github.doandadr.petualanganhijaiyah.ui.widget.joinBox
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.imageTextButton
import ktx.scene2d.verticalGroup


class MCQJoinStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {
    private var answerBox: JoinBox
    private var vertGroup: VerticalGroup
    private val skipButton: ImageTextButton
    private val joinEntries = HijaiyahJoined.entries
    private lateinit var currentEntry : HijaiyahJoined

    init {
        background = skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable)

        this@MCQJoinStage.answerBox = joinBox(HijaiyahJoined.J23_LA_A, JoinBox.Type.QUESTION, JoinBox.Content.ANSWER, assets) {
            it.padTop(PADDING_INNER_SCREEN).expand()
        }

        row()
        this@MCQJoinStage.vertGroup = verticalGroup {
            space(SPACE_BETWEEN_BUTTONS)
            it.spaceTop(50f)
        }

        row()
        this@MCQJoinStage.skipButton = imageTextButton("   Lewati", ImageTextButtons.SKIP.style) {
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@MCQJoinStage.audioService.play(SoundAsset.BUTTON_POP)
            }
            onChange {
                this@MCQJoinStage.loadStage()
            }
            it.padBottom(PADDING_INNER_SCREEN).align(Align.bottom).expand()
        }

        loadStage()
        setTutorials()
    }

    private fun setTutorials() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(answerBox, TutorialType.JOIN_START)
            gameEventManager.dispatchShowTutorialEvent(vertGroup.children.first(), TutorialType.JOIN_OPTION)
        }
    }

    private fun loadStage() {
        currentEntry = joinEntries.random()

        vertGroup.clearChildren()
        val choices = listOf(JoinBox.Content.ANSWER, JoinBox.Content.OPTION_1, JoinBox.Content.OPTION_2)
        choices.shuffled().forEach { content ->
            val box = JoinBox(currentEntry, JoinBox.Type.OPTION, content, assets)
            box.apply {
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@MCQJoinStage.audioService.play(SoundAsset.TOUCH)
                }
                onChange {
                    this@MCQJoinStage.handleAnswer(this)
                }
            }
            vertGroup.addActor(box)
        }

        answerBox.updateJoinedCharacters(currentEntry)
    }

    private fun handleAnswer(option: JoinBox) {
        if (option.content == JoinBox.Content.ANSWER) {
            option.setState(JoinBox.State.CORRECT)
            gameEventManager.dispatchAnswerCorrectEvent(true)
        } else {
            option.setState(JoinBox.State.INCORRECT)
            gameEventManager.dispatchAnswerIncorrectEvent(true)
        }
    }

    companion object {
        private val log = logger<MCQJoinStage>()
    }
}

inline fun <S> KWidget<S>.mcqJoin(
    assets: AssetStorage,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: MCQJoinStage.(S) -> Unit = {}
) = actor(
    MCQJoinStage(
        assets,
        audioService,
        gameEventManager,
    ), init
)
