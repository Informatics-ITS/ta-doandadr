package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.ImageTextButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SPACE_HIJAIYAH_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.widget.HijaiyahBox
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.horizontalGroup
import ktx.scene2d.imageButton
import ktx.scene2d.imageTextButton
import ktx.scene2d.label

class MCQVoiceStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {
    private var answerVoiceButton: ImageButton
    private var horiGroup: HorizontalGroup
    private val hijaiyahEntries = Hijaiyah.entries.take(28)
    private val choiceBoxes = mutableListOf<HijaiyahBox>()
    private lateinit var currentLetters: List<Hijaiyah>
    private lateinit var correctAnswer: Hijaiyah
    private val skipButton: ImageTextButton

    init {
        background = skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable)

        label("Yang manakah...", Labels.SECONDARY_BORDER.style) {
            it.padTop(PADDING_INNER_SCREEN).expand().align(Align.bottom)
        }

        row()
        this@MCQVoiceStage.answerVoiceButton = imageButton(ImageButtons.VOICE.style) {
            it.spaceTop(20f)
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@MCQVoiceStage.audioService.play(SoundAsset.TOUCH)
            }
            onChange {
                this@MCQVoiceStage.apply {
                    audioService.play(correctAnswer.audio)
                }
            }
        }

        row()
        this@MCQVoiceStage.horiGroup = horizontalGroup {
            it.spaceTop(50f).expand()
            space(SPACE_HIJAIYAH_MEDIUM)
        }

        row()
        this@MCQVoiceStage.skipButton = imageTextButton("   Lewati", ImageTextButtons.SKIP.style) {
            it.padBottom(PADDING_INNER_SCREEN).align(Align.bottom).expand()
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@MCQVoiceStage.audioService.play(SoundAsset.BUTTON_POP)
            }
            onChange {
                this@MCQVoiceStage.loadStage()
            }
        }

        loadStage()
        setTutorials()
    }

    private fun setTutorials() {
        Gdx.app.postRunnable{
            gameEventManager.dispatchShowTutorialEvent(answerVoiceButton, TutorialType.VOICE_START)
            gameEventManager.dispatchShowTutorialEvent(horiGroup.children.first(), TutorialType.VOICE_OPTION)
        }
    }

    private fun handleAnswer(index: Int) {
        if (currentLetters[index] == correctAnswer) {
            choiceBoxes[index].setState(HijaiyahBox.State.CORRECT)
            audioService.play(choiceBoxes[index].hijaiyah.audio)
            gameEventManager.dispatchAnswerCorrectEvent(true)
        } else {
            choiceBoxes[index].setState(HijaiyahBox.State.INCORRECT)
            audioService.play(choiceBoxes[index].hijaiyah.audio)
            gameEventManager.dispatchAnswerIncorrectEvent(true)
        }
    }

    private fun pickRandomEntries(amount: Int): List<Hijaiyah> = hijaiyahEntries.shuffled().take(amount)

    private fun loadStage() {
        // get random letters
        currentLetters = pickRandomEntries(ENTRY_COUNT)
        // set correct answer
        correctAnswer = currentLetters.random()
        // clear choice boxes
        choiceBoxes.clear()

        // foreach random letters
        // add boxes with listeners
        horiGroup.clearChildren()
        currentLetters.forEachIndexed { index, letter ->
            choiceBoxes += HijaiyahBox(letter, HijaiyahBox.Size.MEDIUM, assets)
            choiceBoxes[index].apply {
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@MCQVoiceStage.audioService.play(SoundAsset.TOUCH)
                }
                onChange {
                    this@MCQVoiceStage.handleAnswer(index)
                }
            }
            horiGroup.addActor(choiceBoxes[index])
        }

        audioService.play(correctAnswer.audio)
    }


    companion object {
        private val log = logger<MCQVoiceStage>()
        private const val ENTRY_COUNT = 3
    }
}

inline fun <S> KWidget<S>.mcqVoiceStage(
    assets: AssetStorage,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: MCQVoiceStage.(S) -> Unit = {}
) = actor(
    MCQVoiceStage(
        assets,
        audioService,
        gameEventManager,
    ), init
)
