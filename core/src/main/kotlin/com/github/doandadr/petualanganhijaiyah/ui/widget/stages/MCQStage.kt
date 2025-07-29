package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.ImageTextButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.values.SPACE_HIJAIYAH_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.widget.HijaiyahBox
import com.github.doandadr.petualanganhijaiyah.ui.widget.hijaiyahBox
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
import ktx.scene2d.imageTextButton
import ktx.scene2d.label
import ktx.scene2d.stack

class MCQStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    private var answerBox: Stack
    private val hijaiyahEntries = Hijaiyah.entries.take(28)
    private lateinit var currentEntries: List<Hijaiyah>
    private val choiceBoxes = mutableListOf<HijaiyahBox>()
    private lateinit var correctAnswer: Hijaiyah

    private var horiGroup: HorizontalGroup
    private val answerLatin: Label
    private var answerArabic: HijaiyahBox
    private val skipButton: ImageTextButton
    private var state: State = State.LATIN

    init {
        background = skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable)

        label("Yang manakah...", Labels.SECONDARY_BORDER.style) {
            it.padTop(PADDING_INNER_SCREEN).expand().align(Align.bottom)
        }

        row()
        this@MCQStage.answerBox = stack {
            it.spaceTop(30f)
            this@MCQStage.answerLatin = label("", Labels.TEXTBOX_WHITE_SQUARE_LARGE.style) {
                setAlignment(Align.center)
                setFontScale(SCALE_FONT_MEDIUM)
            }
            this@MCQStage.answerArabic = hijaiyahBox(Hijaiyah.ALIF, HijaiyahBox.Size.MEDIUM, this@MCQStage.assets) {
                touchable = Touchable.disabled
            }
        }

        row()
        this@MCQStage.horiGroup = horizontalGroup {
            it.spaceTop(100f)
            space(SPACE_HIJAIYAH_MEDIUM)
        }

        row()
        this@MCQStage.skipButton = imageTextButton("   Lewati", ImageTextButtons.SKIP.style) {
            it.padBottom(PADDING_INNER_SCREEN).expand().align(Align.bottom)
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@MCQStage.audioService.play(SoundAsset.BUTTON_POP)
            }
            onChange {
                this@MCQStage.loadStage()
            }
        }

        loadStage()
    }

    private fun handleAnswer(index: Int) {
        if (currentEntries[index] == correctAnswer) {
            choiceBoxes[index].setState(HijaiyahBox.State.CORRECT)
            audioService.play(choiceBoxes[index].hijaiyah.audio)
            gameEventManager.dispatchAnswerCorrectEvent(true)
        } else {
            choiceBoxes[index].setState(HijaiyahBox.State.INCORRECT)
            audioService.play(choiceBoxes[index].hijaiyah.audio)
            gameEventManager.dispatchAnswerIncorrectEvent(true)
        }
    }

    private fun pickRandomEntries(count: Int): List<Hijaiyah> = hijaiyahEntries.shuffled().take(count)

    private fun loadStage() {
        // ramdomize whether stage is arabic to latin or vice versa
        state = State.entries.random()
        // get random letters
        currentEntries = pickRandomEntries(ENTRY_COUNT)
        // set correct answer
        correctAnswer = currentEntries.random()
        // clear choice boxes
        choiceBoxes.clear()

        // for each random letters
        // add boxes with listeners
        horiGroup.clearChildren()
        currentEntries.forEachIndexed { index, letter ->
            val box = HijaiyahBox(letter, HijaiyahBox.Size.MEDIUM, assets)
            box.setType(if (state == State.ARABIC) HijaiyahBox.Type.DEFAULT else HijaiyahBox.Type.TEXT)
            box.apply {
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@MCQStage.audioService.play(SoundAsset.TOUCH)
                }
                onChange {
                    this@MCQStage.handleAnswer(index)
                }
            }
            choiceBoxes += box
            horiGroup.addActor(box)
        }
        answerArabic.isVisible = state == State.LATIN
        answerLatin.isVisible = state == State.ARABIC

        answerLatin.setText(correctAnswer.reading.uppercase())
        answerArabic.updateLetter(correctAnswer)
    }

    enum class State {
        ARABIC,
        LATIN
    }

    companion object {
        private val log = logger<MCQStage>()
        private const val ENTRY_COUNT = 3
    }
}

inline fun <S> KWidget<S>.mcqStage(
    assets: AssetStorage,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: MCQStage.(S) -> Unit = {}
) = actor(
    MCQStage(
        assets,
        audioService,
        gameEventManager,
    ), init
)
