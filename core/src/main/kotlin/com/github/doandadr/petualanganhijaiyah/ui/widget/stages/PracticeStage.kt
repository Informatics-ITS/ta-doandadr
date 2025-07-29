package com.github.doandadr.petualanganhijaiyah.ui.widget.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Harakat
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAtlasAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.scene2d.KImageButton
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table

class PracticeStage(
    private val assets: AssetStorage,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {
    private var harakatGroup: HorizontalGroup
    private var voiceDhommah: KImageButton
    private var voiceKasrah: KImageButton
    private var voiceFathah: KImageButton
    private val kasrahHij: Image
    private val kasrah: Image
    private val fathahHij: Image
    private val fathah: Image
    private val dhommahHij: Image
    private val dhommah: Image

    private val hijaiyahEntries = Hijaiyah.entries
    private var currentEntry: Hijaiyah = Hijaiyah.ALIF
    private val prevButton: ImageButton
    private val nextButton: ImageButton
    private val voiceButton: ImageButton
    private val hijaiyahImage: Image
    private val hijaiyahText: Label

    private val textAtlas = assets[TextureAtlasAsset.HIJAIYAH.descriptor]

    init {
        this@PracticeStage.prevButton = imageButton(ImageButtons.PREVIOUS.style) {
            it.padRight(-50f).align(Align.right)
            isTransform = true
            setOrigin(Align.center)
            setScale(SCALE_BTN_MEDIUM)
            onTouchDown {
                setScale(SCALE_BTN_MEDIUM)
                this.clearActions()
                this += Animations.pulse(initScale = SCALE_BTN_MEDIUM)
                this@PracticeStage.audioService.play(SoundAsset.TOUCH)
            }
        }
        container {
            it.size(357f)
            toBack()
            background = skin.getDrawable(Drawables.HIJAIYAH_FRAME_LARGE.drawable)
            prefSize(215f)
            this@PracticeStage.hijaiyahImage = image {
                setScaling(Scaling.fit)
            }
        }
        this@PracticeStage.nextButton = imageButton(ImageButtons.NEXT.style) {
            it.padLeft(-50f).align(Align.left)
            isTransform = true
            setOrigin(Align.center)
            setScale(SCALE_BTN_MEDIUM)
            onTouchDown {
                setScale(SCALE_BTN_MEDIUM)
                this.clearActions()
                this += Animations.pulse(initScale = SCALE_BTN_MEDIUM)
                this@PracticeStage.audioService.play(SoundAsset.TOUCH)
            }
        }

        row()
        this@PracticeStage.voiceButton = imageButton(ImageButtons.VOICE.style) {
            it.padTop(-50f).colspan(3)
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                setScale(1f)
                this.clearActions()
                this += Animations.pulse()
                this@PracticeStage.audioService.play(SoundAsset.TOUCH)
            }
        }

        row()
        this@PracticeStage.harakatGroup = horizontalGroup {
            it.spaceTop(20f).colspan(3)
            space(20f)
            table {
                table {
                    it.prefWidth(150f).prefHeight(200f)
                    background = skin.getDrawable(Drawables.ICONBUTTON_WHITE_ROUNDED.drawable)
                    container { this@PracticeStage.dhommah = image { setScaling(Scaling.fit) } }.prefWidth(50f)
                        .prefHeight(50f)
                    row()
                    container { this@PracticeStage.dhommahHij = image { setScaling(Scaling.fit) } }.prefWidth(100f)
                        .prefHeight(100f)
                }
                row()
                this@PracticeStage.voiceDhommah = imageButton(ImageButtons.VOICE.style) {
                    it.spaceTop(20f)
                    isTransform = true
                    setOrigin(Align.center)
                    onTouchDown {
                        setScale(1f)
                        this.clearActions()
                        this += Animations.pulse()
                        this@PracticeStage.audioService.play(SoundAsset.TOUCH)
                    }
                }
            }
            table {
                table {
                    it.prefWidth(150f).prefHeight(200f)
                    background = skin.getDrawable(Drawables.ICONBUTTON_WHITE_ROUNDED.drawable)
                    container { this@PracticeStage.kasrahHij = image { setScaling(Scaling.fit) } }.prefWidth(100f)
                        .prefHeight(100f)
                    row()
                    container { this@PracticeStage.kasrah = image { setScaling(Scaling.fit) } }.prefWidth(50f)
                        .prefHeight(50f)
                }
                row()
                this@PracticeStage.voiceKasrah = imageButton(ImageButtons.VOICE.style) {
                    it.spaceTop(20f)
                    isTransform = true
                    setOrigin(Align.center)
                    onTouchDown {
                        setScale(1f)
                        this.clearActions()
                        this += Animations.pulse()
                        this@PracticeStage.audioService.play(SoundAsset.TOUCH)
                    }
                }
            }
            table {
                table {
                    it.prefWidth(150f).prefHeight(200f)
                    background = skin.getDrawable(Drawables.ICONBUTTON_WHITE_ROUNDED.drawable)
                    container { this@PracticeStage.fathah = image { setScaling(Scaling.fit) } }.prefWidth(50f)
                        .prefHeight(50f)
                    row()
                    container { this@PracticeStage.fathahHij = image { setScaling(Scaling.fit) } }.prefWidth(100f)
                        .prefHeight(100f)
                }
                row()
                this@PracticeStage.voiceFathah = imageButton(ImageButtons.VOICE.style) {
                    it.spaceTop(20f)
                    isTransform = true
                    setOrigin(Align.center)
                    onTouchDown {
                        setScale(1f)
                        this.clearActions()
                        this += Animations.pulse()
                        this@PracticeStage.audioService.play(SoundAsset.TOUCH)
                    }
                }
            }
        }

        row()
        this@PracticeStage.hijaiyahText = label(currentEntry.reading, Labels.TEXTBOX_GREEN_SQUARE_LARGE.style) {
            it.spaceTop(50f).colspan(3)
            setAlignment(Align.center)
            setFontScale(SCALE_BTN_MEDIUM)
            width = 400f
        }

        voiceButton.onChange {
            this@PracticeStage.apply {
                audioService.play(currentEntry.audio)
            }
        }
        voiceFathah.onChange {
            this@PracticeStage.apply {
                currentEntry.audioFathah?.let { audioService.play(it) }
            }
        }
        voiceKasrah.onChange {
            this@PracticeStage.apply {
                currentEntry.audioKasrah?.let { audioService.play(it) }
            }
        }
        voiceDhommah.onChange {
            this@PracticeStage.apply {
                currentEntry.audioDhommah?.let { audioService.play(it) }
            }
        }
        prevButton.onChange {
            this@PracticeStage.apply {
                updateEntry(hijaiyahEntries[hijaiyahEntries.indexOf(currentEntry) - 1])
            }
        }
        nextButton.onChange {
            this@PracticeStage.apply {
                updateEntry(hijaiyahEntries[hijaiyahEntries.indexOf(currentEntry) + 1])
            }
        }
        updateEntry(currentEntry)
    }

    private fun showTutorial() {
        Gdx.app.postRunnable {
            if (currentEntry == Hijaiyah.ALIF)
                gameEventManager.dispatchShowTutorialEvent(nextButton, TutorialType.PRACTICE_NEXT)
            if (currentEntry == Hijaiyah.BA)
                gameEventManager.dispatchShowTutorialEvent(prevButton, TutorialType.PRACTICE_PREVIOUS)
            gameEventManager.dispatchShowTutorialEvent(voiceButton, TutorialType.PRACTICE_VOICE)
        }
    }

    private fun updateEntry(hijaiyah: Hijaiyah) {
        currentEntry = hijaiyah
        showTutorial()

        prevButton.isVisible = currentEntry != Hijaiyah.ALIF
        nextButton.isVisible = currentEntry != Hijaiyah.N10

        val isNumber = currentEntry.fileId.toInt() in 100..110
        harakatGroup.isVisible = !isNumber
        if (isNumber) {
            val hijDrawable = TextureRegionDrawable(textAtlas.findRegion(currentEntry.imageNumber))
            hijaiyahImage.drawable = hijDrawable
            hijaiyahText.setText("(${currentEntry.fileId.takeLast(2).toInt()}) ${currentEntry.reading.uppercase()}")
            return
        }

        val hijDrawable = TextureRegionDrawable(textAtlas.findRegion(currentEntry.image))
        hijaiyahImage.drawable = hijDrawable
        hijaiyahText.setText(currentEntry.reading.uppercase())

        val fathahDrawable = TextureRegionDrawable(textAtlas.findRegion(Harakat.FATHAH.image))
        val kasrahDrawable = TextureRegionDrawable(textAtlas.findRegion(Harakat.KASRAH.image))
        val dhommahDrawable = TextureRegionDrawable(textAtlas.findRegion(Harakat.DHOMMAH.image))
        fathahHij.drawable = hijDrawable
        kasrahHij.drawable = hijDrawable
        dhommahHij.drawable = hijDrawable
        fathah.drawable = fathahDrawable
        kasrah.drawable = kasrahDrawable
        dhommah.drawable = dhommahDrawable
    }
}

inline fun <S> KWidget<S>.practiceStage(
    assets: AssetStorage,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: PracticeStage.(S) -> Unit = {}
) = actor(
    PracticeStage(
        assets,
        audioService,
        gameEventManager,
    ), init
)
