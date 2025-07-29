package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.ImageTextButtons
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SIZE_HIJAIYAH_MEDIUM
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
import ktx.scene2d.imageTextButton

class DragAndDropStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
):Table(skin), KTable {
    private var correctCount: Int = 0
    private val hijaiyahEntries = Hijaiyah.entries.take(28)
    private lateinit var dragEntries: List<Hijaiyah>
    lateinit var dropEntries: List<Hijaiyah>

    private val dragGroup: HorizontalGroup
    private val dropGroup: HorizontalGroup
    private val skipButton: ImageTextButton
    private val dragAndDrop = DragAndDrop()

    init {
        background = skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable)

        this@DragAndDropStage.dropGroup = horizontalGroup {
            space(50f)
            it.padTop(PADDING_INNER_SCREEN).padLeft(PADDING_INNER_SCREEN).padRight(PADDING_INNER_SCREEN).expand()
        }

        row()
        this@DragAndDropStage.dragGroup = horizontalGroup {
            space(50f)
            it.spaceTop(200.0f).expand()
        }

        row()
        this@DragAndDropStage.skipButton = imageTextButton("   Lewati", ImageTextButtons.SKIP.style) {
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@DragAndDropStage.audioService.play(SoundAsset.BUTTON_POP)
            }
            onChange {
                this@DragAndDropStage.loadStage()
            }
            it.padBottom(PADDING_INNER_SCREEN).align(Align.bottom).expand()
        }

        loadStage()
        setTutorial()
    }

    private fun setTutorial() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(dragGroup.children.first(), TutorialType.DRAG_DROP_START)
            gameEventManager.dispatchShowTutorialEvent(dropGroup.children.first(), TutorialType.DRAG_DROP_END)
        }
    }

    private fun pickRandomEntries(amount: Int): List<Hijaiyah> = hijaiyahEntries.shuffled().take(amount)

    private fun loadStage() {
        dragGroup.clearChildren()
        dragEntries = pickRandomEntries(ENTRY_COUNT)
        dragEntries.shuffled().forEachIndexed { _, letter ->
            val box = HijaiyahBox(letter, HijaiyahBox.Size.MEDIUM, assets)
            val frame = Container<HijaiyahBox>().size(SIZE_HIJAIYAH_MEDIUM)
            box.userObject = frame
            frame.actor = box

            addDragSource(box)
            addDragTarget(frame)

            dragGroup.addActor(frame)
        }

        dropGroup.clearChildren()
        dropEntries = dragEntries.shuffled()
        dropEntries.forEachIndexed { index, letter ->
            val box = HijaiyahBox(letter, HijaiyahBox.Size.MEDIUM, assets)
            val frame = Container<HijaiyahBox>().size(SIZE_HIJAIYAH_MEDIUM)
            box.setType(HijaiyahBox.Type.DROP)
            box.userObject = frame
            frame.background = skin.getDrawable(Drawables.ICONBUTTON_BACKGROUND_ROUNDED.drawable)
            frame.actor = box

            addDropTarget(frame)

            dropGroup.addActor(frame)
        }
        correctCount = 0
    }

    private fun addDragSource(box: HijaiyahBox) {
        dragAndDrop.addSource(object :DragAndDrop.Source(box) {
            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                val payload : DragAndDrop.Payload = DragAndDrop.Payload()
                payload.dragActor = actor
                stage.addActor(actor)
                dragAndDrop.setDragActorPosition(actor.width/2, -actor.height/2)

                audioService.play(SoundAsset.POP_LOW)

                return payload
            }

            override fun dragStop(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                payload: DragAndDrop.Payload?,
                target: DragAndDrop.Target?
            ) {
                val sourceBox = (box.userObject as Container<HijaiyahBox>)

                if (target == null ) {
                    sourceBox.actor = box
                    audioService.play(SoundAsset.CANCEL)
                } else if ((target.actor as Container<HijaiyahBox>).actor.hijaiyah != box.hijaiyah) {
                    // Answer is incorrect
                    sourceBox.actor = box
                    gameEventManager.dispatchAnswerIncorrectEvent(false)
                }
            }
        })
    }

    private fun addDragTarget(frame: Container<HijaiyahBox>) {
        dragAndDrop.addTarget(object : DragAndDrop.Target(frame) {
            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                return true
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                if (payload != null) {
                    val payloadBox = payload.dragActor as HijaiyahBox
                    val frameBox = frame.actor

                    if (frameBox == null) {
                        frame.actor = payloadBox
                        audioService.play(SoundAsset.DROP)
                    } else if(frameBox.hijaiyah == payloadBox.hijaiyah) {
                        frame.actor = payloadBox
                    }
                }
            }
        })
    }

    private fun addDropTarget(frame: Container<HijaiyahBox>) {
        dragAndDrop.addTarget(object : DragAndDrop.Target(frame) {
            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                return true
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                if (payload != null) {
                    val payloadBox = payload.dragActor as HijaiyahBox
                    val frameBox = frame.actor

                    val isCorrect = payloadBox.hijaiyah == frameBox.hijaiyah

                    if (isCorrect) {
                        payloadBox.setState(HijaiyahBox.State.CORRECT)
                        frame.actor = payloadBox
                        payload.dragActor.touchable = Touchable.disabled
                        audioService.play(SoundAsset.DROP)
                        correctCount++
                        log.debug { "Answered correct $correctCount times" }
                        if (correctCount >= ENTRY_COUNT) {
                            gameEventManager.dispatchAnswerCorrectEvent(true)
                            correctCount = 0
                        } else {
                            gameEventManager.dispatchAnswerCorrectEvent(false)
                        }
                    }
                }
            }
        })
    }

    companion object {
        private val log = logger<DragAndDropStage>()
        private const val ENTRY_COUNT = 3
    }
}

inline fun <S> KWidget<S>.dragAndDropStage(
    assets: AssetStorage,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: DragAndDropStage.(S) -> Unit = {}
) = actor(
    DragAndDropStage(
        assets,
        audioService,
        gameEventManager,
    ), init
)
