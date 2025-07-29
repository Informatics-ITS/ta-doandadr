package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.ImageTextButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAtlasAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ml.PreprocessingHelper
import com.github.doandadr.petualanganhijaiyah.ml.Recognition
import com.github.doandadr.petualanganhijaiyah.ml.TensorFlowUtils
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.values.SIZE_DRAWING_BOARD
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
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.imageTextButton
import ktx.scene2d.label
import ktx.scene2d.table
import space.earlygrey.shapedrawer.ShapeDrawer
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable


class DrawingStage(
    assets: AssetStorage,
    private val audioService: AudioService,
    private val batch: Batch,
    private val gameEventManager: GameEventManager,
    private val recognition: Recognition,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {
    private val drawArea: Image
    private val resetButton: ImageButton
    private val submitButton: ImageTextButton
    private val drawingBoard: Table
    private val skipButton: ImageButton
    private val hijaiyahText: Label

    private val hijaiyahEntries = Hijaiyah.entries.take(28)
    private lateinit var currentEntry: Hijaiyah
    private val textAtlas = assets[TextureAtlasAsset.HIJAIYAH.descriptor]
    private lateinit var drawer : ShapeDrawer

    // List to store the points of the drawing
    private val segments = mutableListOf<MutableList<Vector2>>()
    private lateinit var resultImageArray: Array<FloatArray>
    private var drawNow: Boolean = false

    init {
        initShapeDrawer()

        background(skin.getDrawable(Drawables.BOX_ORANGE_ROUNDED.drawable))

        label("Tulislah...", Labels.SECONDARY_BORDER.style) {
            it.padTop(PADDING_INNER_SCREEN).colspan(3)
        }

        row()
        this@DrawingStage.hijaiyahText = label("", Labels.TEXTBOX_GREEN_SQUARE_LARGE.style) {
            setAlignment(Align.center)
            setFontScale(SCALE_BTN_MEDIUM)
            it.spaceTop(20f).colspan(3)
        }

        row()
        this@DrawingStage.drawingBoard = table {
            it.padLeft(PADDING_INNER_SCREEN).padRight(PADDING_INNER_SCREEN).size(SIZE_DRAWING_BOARD).spaceTop(30f)
                .colspan(3)
            background(skin.getDrawable(Drawables.BOX_WHITE_ROUNDED.drawable))
            touchable = Touchable.enabled

            this@DrawingStage.drawArea = image(object : ShapeDrawerDrawable(this@DrawingStage.drawer) {
                override fun drawShapes(shapeDrawer: ShapeDrawer?, x: Float, y: Float, width: Float, height: Float) {
                    this@DrawingStage.drawer.update()

                    // Draw each segment
                    for (segment in this@DrawingStage.segments) {
                        if (segment.isNotEmpty()) {
                            for (i in 0 until segment.size - 1) {
                                this@DrawingStage.drawer.filledCircle(segment[i].x, segment[i].y, 8f, Color.BLACK)
                                this@DrawingStage.drawer.line(segment[i], segment[i + 1], Color.BLACK, 16f)
                            }
                        }
                    }
//                    this@DrawingStage.drawImageFromInput()
                }
            })
        }

        row()
        horizontalGroup {
            it.padBottom(PADDING_INNER_SCREEN).expand().align(Align.bottom)

            this@DrawingStage.resetButton = imageButton(ImageButtons.REPEAT.style) {
                setScale(SCALE_BTN_SMALL)
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    setScale(SCALE_BTN_SMALL)
                    this.clearActions()
                    this += Animations.pulse(initScale = SCALE_BTN_SMALL)
                    this@DrawingStage.audioService.play(SoundAsset.BUTTON_POP)
                }
                onChange {
                    this@DrawingStage.resetDrawingBoard()
                }
            }
            this@DrawingStage.skipButton = imageButton(ImageButtons.SKIP.style) {
                setScale(SCALE_BTN_SMALL)
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    setScale(SCALE_BTN_SMALL)
                    this.clearActions()
                    this += Animations.pulse(initScale = SCALE_BTN_SMALL)
                    this@DrawingStage.audioService.play(SoundAsset.BUTTON_POP)
                }
                onChange {
                    this@DrawingStage.loadStage()
                }
            }
            this@DrawingStage.submitButton = imageTextButton("   Jawab", ImageTextButtons.SUBMIT.style) {
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@DrawingStage.audioService.play(SoundAsset.BUTTON_POP)
                }
                onChange {
                    this@DrawingStage.handleSubmission()
                }
            }
        }

        loadStage()
        setTutorials()
    }

    private fun loadStage() {
        currentEntry = pickRandomEntries(1).first()

        val isNumber = currentEntry.fileId.toInt() in 100..110
        hijaiyahText.setText("${if (isNumber) "(${currentEntry.fileId.takeLast(2).toInt()}) " else ""}${currentEntry.reading.uppercase()}")

        segments.clear()

        drawingBoard.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val coords = drawingBoard.localToStageCoordinates(Vector2(x, y))
                segments.add(mutableListOf(coords, coords))
                audioService.play(listOf(SoundAsset.DRAW_HEAVY, SoundAsset.DRAW_LIGHT).random())
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                segments.last().add(drawingBoard.localToStageCoordinates(Vector2(x, y)))
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                audioService.stopSound(SoundAsset.DRAW_LIGHT)
                audioService.stopSound(SoundAsset.DRAW_HEAVY)
            }
        })
    }

    private fun setTutorials() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(drawingBoard, TutorialType.DRAW_START)
            gameEventManager.dispatchShowTutorialEvent(resetButton, TutorialType.DRAW_CLEAR)
            gameEventManager.dispatchShowTutorialEvent(skipButton, TutorialType.DRAW_SKIP)
            gameEventManager.dispatchShowTutorialEvent(submitButton, TutorialType.DRAW_ANSWER)
        }
    }

    private fun initShapeDrawer() {
        // Create a 1x1 white texture
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        val whiteTexture = TextureRegion(Texture(pixmap))
        pixmap.dispose()
        // Initialize ShapeDrawer
        drawer = ShapeDrawer(batch, whiteTexture)
    }

    private fun resetDrawingBoard() {
        segments.clear()
    }

    private fun handleSubmission() {
        // Preprocess the drawing
        val preprocessingHelper = PreprocessingHelper()
        val boardPos = drawingBoard.localToStageCoordinates(Vector2())
        resultImageArray = preprocessingHelper.preProcessDrawing(segments, boardPos,  SIZE_DRAWING_BOARD)

        // Classify
        val predictions = recognition.predict(resultImageArray)
        val sortedPredictions = TensorFlowUtils.sortPredictions(predictions)
        val topPredictions = sortedPredictions.take(currentEntry.detectionSlack)

        log.debug { topPredictions.joinToString(", ") { "${it.first}:${hijaiyahEntries.find { entry -> entry.id == it.first }}" } }

        // Answer correct if answer in top prediction list
        val isCorrect = currentEntry.id in topPredictions.map { it.first }
        if (isCorrect) {
            gameEventManager.dispatchAnswerCorrectEvent(true)
        } else {
            gameEventManager.dispatchAnswerIncorrectEvent(true)
        }

        // Clear drawing from screen
        segments.clear()
    }

    private fun pickRandomEntries(amount: Int): List<Hijaiyah> = hijaiyahEntries.shuffled().take(amount)

    private fun drawImageFromInput() {
        if (drawNow) {
            val imageSize = 64
            val pixelSize = 10f
            for (y in 0 until imageSize) {
                for (x in 0 until imageSize) {
                    val pixelValue = resultImageArray[x][y]
                    val color = Color(pixelValue, pixelValue, pixelValue, 1f)
                    this@DrawingStage.drawer.setColor(color)
                    this@DrawingStage.drawer.filledRectangle(
                        50 + x * pixelSize,
                        900 + (imageSize - 1 - y) * pixelSize, pixelSize, pixelSize
                    )
                }
            }
        }
    }

    companion object {
        private val log = logger<DragAndDropStage>()
    }
}

inline fun <S> KWidget<S>.drawingStage(
    assets: AssetStorage,
    audioService: AudioService,
    batch: Batch,
    gameEventManager: GameEventManager,
    recognition: Recognition,
    init: DrawingStage.(S) -> Unit = {}
) = actor(
    DrawingStage(
        assets,
        audioService,
        batch,
        gameEventManager,
        recognition,
    ), init
)
