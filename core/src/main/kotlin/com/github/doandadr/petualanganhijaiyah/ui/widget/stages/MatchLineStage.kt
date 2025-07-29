package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
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
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.widget.HijaiyahBox
import com.github.doandadr.petualanganhijaiyah.ui.widget.MatchBox
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import ktx.actors.alpha
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.horizontalGroup
import ktx.scene2d.image
import ktx.scene2d.imageTextButton
import ktx.scene2d.table
import ktx.scene2d.verticalGroup
import space.earlygrey.shapedrawer.ShapeDrawer
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable

private data class Line(var sX: Float, var sY: Float, var eX: Float, var eY: Float)

class MatchLineStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    batch: Batch,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin
) : Stack(), KGroup {
    private val leftGroup: VerticalGroup
    private val rightGroup: VerticalGroup
    private val skipButton: ImageTextButton
    private var drawArea: Image

    private val hijaiyahEntries = Hijaiyah.entries.take(28)
    private lateinit var leftEntries: List<Hijaiyah>
    private lateinit var rightEntries: List<Hijaiyah>
    private var correctCount: Int = 0

    private val dragAndDrop = DragAndDrop()
    private lateinit var dragged: Image
    private val drawer = ShapeDrawer(batch, skin.getRegion(Drawables.CIRCLE_BRUSH.drawable))
    private val savedLines: MutableList<Line> = mutableListOf()
    private var currentLine: Line? = null

    init {
        table {
            setFillParent(true)

            background(skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable))
            horizontalGroup {
                space(150f)
                this@MatchLineStage.leftGroup = verticalGroup {
                    space(25f)
                }
                this@MatchLineStage.rightGroup = verticalGroup {
                    space(25f)
                }
                it.padTop(PADDING_INNER_SCREEN).expandY()
            }

            row()
            this@MatchLineStage.skipButton = imageTextButton("   Lewati", ImageTextButtons.SKIP.style) {
                isTransform = true
                setOrigin(Align.center)
                setScale(SCALE_BTN_SMALL)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse(initScale = SCALE_BTN_SMALL)
                    this@MatchLineStage.audioService.play(SoundAsset.BUTTON_POP)
                }
                onChange {
                    this@MatchLineStage.loadStage()
                }
                it.padBottom(PADDING_INNER_SCREEN).align(Align.bottom).expand()
            }

            row()

        }

        this@MatchLineStage.drawArea = image(object : ShapeDrawerDrawable(drawer) {
            override fun drawShapes(shapeDrawer: ShapeDrawer?, x: Float, y: Float, width: Float, height: Float) {
                // Draw saved lines
                savedLines.forEach { line ->
                    drawer.line(line.sX, line.sY, line.eX, line.eY, Color.BLACK, 10f)
                }
                // Draw the current line if it exists
                currentLine?.let { line ->
                    drawer.line(line.sX, line.sY, line.eX, line.eY, Color.BLACK, 10f)
                }
                drawer.update()
            }

        })
        {
            setFillParent(true)
            touchable = Touchable.disabled
        }

        loadStage()
        setTutorials()
    }

    private fun setTutorials() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(
                (leftGroup.children.first() as MatchBox),
                TutorialType.MATCH_START
            )
            gameEventManager.dispatchShowTutorialEvent((rightGroup.children[1] as MatchBox), TutorialType.MATCH_END)
        }
    }

    private fun pickRandomEntries(amount: Int): List<Hijaiyah> = hijaiyahEntries.shuffled().take(amount)

    private fun loadStage() {
        leftGroup.clearChildren()
        leftEntries = pickRandomEntries(5)
        leftEntries.shuffled().forEachIndexed { _, hijaiyah ->
            val box = MatchBox(hijaiyah, MatchBox.State.LEFT, assets)
            box.rightDot.userObject = box
            addDragSource(box)
            addDragTarget(box)
            leftGroup.addActor(box)
        }

        rightGroup.clearChildren()
        rightEntries = leftEntries.shuffled()
        rightEntries.shuffled().forEachIndexed { index, hijaiyah ->
            val box = MatchBox(hijaiyah, MatchBox.State.RIGHT, assets)
            box.box.setType(HijaiyahBox.Type.TEXT)
            box.leftDot.userObject = box

            addDropTarget(box)
            rightGroup.addActor(box)
        }

        savedLines.clear()
        currentLine = null
        correctCount = 0
    }

    private fun addDragSource(box: MatchBox) {
        log.debug { "Adding drag source" }
        dragAndDrop.addSource(object : DragAndDrop.Source(box) {
            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                log.debug { "Start dragging source" }
                val payload: DragAndDrop.Payload = DragAndDrop.Payload()
                val draggedDot = (actor as MatchBox).rightDot
                draggedDot.alpha = 1f
                dragged = draggedDot
                payload.dragActor = draggedDot

                // Save the starting point of the drag
                val stageCoordinates =
                    draggedDot.localToStageCoordinates(Vector2(draggedDot.width / 2, draggedDot.height / 2))
                currentLine = Line(stageCoordinates.x, stageCoordinates.y, stageCoordinates.x, stageCoordinates.y)

                stage.addActor(draggedDot)
                dragAndDrop.setDragActorPosition(draggedDot.width / 2, -draggedDot.height / 2)
                audioService.play(SoundAsset.STRETCH)

                return payload
            }

            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                // Update the end point of the current line
                Gdx.app.postRunnable {
                    currentLine?.let {
                        val stageCoordinates =
                            dragged.localToStageCoordinates(Vector2(dragged.width / 2, dragged.height / 2))
                        it.eX = stageCoordinates.x
                        it.eY = stageCoordinates.y
                    }
                }
            }

            override fun dragStop(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                payload: DragAndDrop.Payload?,
                target: DragAndDrop.Target?
            ) {
                log.debug { "Stop dragging source" }

                if (payload != null) {
                    if (target == null) {
                        box.rightCircle.actor = payload.dragActor
                        audioService.play(SoundAsset.CANCEL)
                    } else if ((target.actor as MatchBox).hijaiyah != box.hijaiyah) {
                        box.rightCircle.actor = payload.dragActor
                    }
                    currentLine = null
                }
            }
        })
    }

    private fun addDragTarget(frame: MatchBox) {

        log.debug { "Adding drag target" }
        dragAndDrop.addTarget(object : DragAndDrop.Target(frame) {
            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                log.debug { "Dragging payload over drag target" }
                return true
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                log.debug { "Dropping payload on drag target" }
                if (payload != null) {
                    val payloadDot = payload.dragActor as Image

                    if (frame.rightCircle.actor == null) {
                        frame.rightCircle.actor = payloadDot
                        audioService.play(SoundAsset.DROP)
                    } else if (frame.hijaiyah == (payloadDot.userObject as MatchBox).hijaiyah) {
                        frame.rightCircle.actor = payloadDot
                    }
                    currentLine = null
                }
            }

        })
    }

    private fun addDropTarget(frame: MatchBox) {
        log.debug { "Adding drop target" }
        dragAndDrop.addTarget(object : DragAndDrop.Target(frame) {
            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                log.debug { "Dragging payload over drop target" }
                return true
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                log.debug { "Dropping payload on drop target" }
                if (payload != null) {
                    val payloadDot = payload.dragActor as Image
                    val isCorrect = (payloadDot.userObject as MatchBox).hijaiyah == frame.hijaiyah

                    if (isCorrect) {
                        frame.box.setState(HijaiyahBox.State.CORRECT)
                        (source?.actor as MatchBox).box.run {
                            setState(HijaiyahBox.State.CORRECT)
                            touchable = Touchable.disabled
                        }

                        frame.leftCircle.actor = payloadDot
                        audioService.play(SoundAsset.DROP)

                        // Save the line coordinates
                        val leftDot = frame.leftCircle
                        val stageCoordinates =
                            leftDot.localToStageCoordinates(Vector2(leftDot.width / 2, leftDot.height / 2))
                        currentLine?.let {
                            val newLine =
                                Line(currentLine!!.sX, currentLine!!.sY, stageCoordinates.x, stageCoordinates.y)
                            savedLines.add(newLine)
                            currentLine = null
                        }

                        correctCount++
                        log.debug { "Answered correct $correctCount times" }
                        if (correctCount >= ENTRY_COUNT) {
                            gameEventManager.dispatchAnswerCorrectEvent(true)
                            correctCount = 0
                        } else {
                            gameEventManager.dispatchAnswerCorrectEvent(false)
                        }
                    } else {
                        gameEventManager.dispatchAnswerIncorrectEvent(false)
                    }
                }
            }
        })
    }

    companion object {
        private val log = logger<MatchLineStage>()
        private const val ENTRY_COUNT = 5
    }
}


inline fun <S> KWidget<S>.matchLineStage(
    assets: AssetStorage,
    audioService: AudioService,
    batch: Batch,
    gameEventManager: GameEventManager,
    init: MatchLineStage.(S) -> Unit = {}
) = actor(
    MatchLineStage(
        assets,
        audioService,
        batch,
        gameEventManager,
    ), init
)
