package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextButtons
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import com.github.doandadr.petualanganhijaiyah.ui.widget.popup.characterSelectPopup
import com.github.doandadr.petualanganhijaiyah.ui.widget.popup.nameChangePopup
import com.github.doandadr.petualanganhijaiyah.ui.widget.popup.settingsPopup
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.preferences.get
import ktx.scene2d.actors
import ktx.scene2d.container
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import ktx.scene2d.verticalGroup
import kotlin.system.exitProcess


class HomeScreen(game: Main) : BaseScreen(game) {
    private lateinit var exitButton: TextButton
    private lateinit var settingButton: TextButton
    private lateinit var startButton: TextButton
    private lateinit var nameButton: TextButton
    private lateinit var bookButton: ImageButton
    private var popup: Table = Table()
    private var layout: Table = Table()

    enum class PopupState {
        NONE,
        SETTING,
        CHARACTER,
        NAME,
    }

    override fun show() {
        super.show()

        setupAudio()
        setupUI()
        transitionIn()
        showTutorial()
    }

    private fun setupAudio() {
        audioService.musicVolume = preferences[PrefKey.MUSIC_VOLUME.key, 1f]
        audioService.soundVolume = preferences[PrefKey.SOUND_VOLUME.key, 1f]
        audioService.play(MusicAsset.HOME)
    }

    private fun setupUI() {
        val bgHome = TextureRegionDrawable(assets[TextureAsset.HOME.descriptor])
        val bgDim = TextureRegionDrawable(assets[TextureAsset.DIM.descriptor])

        stage.actors {
            image(bgHome) {
                setFillParent(true)
                setScaling(Scaling.fill)
            }

            layout = table {
                setFillParent(true)

                label("PETUALANGAN\nHIJAIYAH", Labels.PRIMARY_GREEN_WHITE_BORDER.style) {
                    it.padLeft(30.0f).padTop(30.0f).expandX().align(Align.topLeft)
                    setAlignment(Align.center)
                    setOrigin(Align.center)
                    setFontScale(SCALE_FONT_MEDIUM)
                }
                bookButton = imageButton(ImageButtons.BOOK.style) {
                    it.padRight(30.0f).padTop(30.0f).expandX().align(Align.topRight)
                    isTransform = true
                    setOrigin(Align.center)
                    onTouchDown {
                        this.clearActions()
                        this += Animations.pulse()
                        audioService.play(SoundAsset.BUTTON_POP)
                    }
                    onChange {
                        transitionOut<PracticeScreen>()
                    }
                }

                row()
                verticalGroup {
                    it.expand().colspan(2)
                    space(40f)
                    startButton = textButton("MULAI", TextButtons.BOARD.style) {
                        setScale(SCALE_BTN_MEDIUM)
                        rotation = 10f
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse(initScale = SCALE_BTN_MEDIUM)
                            audioService.play(SoundAsset.BUTTON_POP)
                        }
                        onChange {
                            transitionOut<MapScreen>()
                        }
                    }
                    settingButton = textButton("PENGATURAN", TextButtons.BOARD.style) {
                        this.label.setFontScale(SCALE_FONT_SMALL)
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            audioService.play(SoundAsset.BUTTON_POP)
                        }
                        onChange {
                            setPopup(PopupState.SETTING)
                        }
                    }
                    exitButton = textButton("KELUAR", TextButtons.BOARD.style) {
                        this.label.setFontScale(SCALE_FONT_SMALL)
                        setScale(SCALE_BTN_SMALL)
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse(initScale = SCALE_BTN_SMALL)
                            audioService.play(SoundAsset.BUTTON_POP)
                        }
                        onChange {
                            Gdx.app.exit()
                            exitProcess(0)
                        }
                    }
                }

                row()
                verticalGroup {
                    it.padLeft(30.0f).expandX().align(Align.bottomLeft).colspan(2)
                    container {
                        isTransform = true
                        setOrigin(Align.center)
                        rotation = 5f
                        label("Selamat\nDatang!", Labels.PRIMARY_GREEN_WHITE_BORDER.style) {
                            setFontScale(SCALE_FONT_MEDIUM)
                        }
                    }
                    nameButton = textButton("Default", TextButtons.SIGN.style) {
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            audioService.play(SoundAsset.BUTTON_POP)
                        }
                        onChange {
                            setPopup(HomeScreen.PopupState.NAME)
                        }
                    }
                }
            }



            popup = table {
                setFillParent(true)
                background = bgDim
                align(Align.center)
                isVisible = false
            }
        }

        val player = preferences[PrefKey.PLAYER.key, PlayerModel()]
        nameButton.setText(player.name)
        setPopup(PopupState.NONE)
    }

    private fun showTutorial() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(nameButton, TutorialType.HOME_NAME)
            gameEventManager.dispatchShowTutorialEvent(bookButton, TutorialType.HOME_PRACTICE)
            gameEventManager.dispatchShowTutorialEvent(startButton, TutorialType.HOME_START)
        }
    }

    private fun setPopup(state: PopupState) {
        if (state != PopupState.NONE) {
            layout.touchable = Touchable.disabled
            popup.clear()
            popup += Animations.appear()
        } else {
            layout.touchable = Touchable.childrenOnly
            popup += Animations.disappear() + Actions.run { popup.clear() }
        }

        when (state) {
            PopupState.SETTING -> {
                popup.add(scene2d.settingsPopup(preferences, audioService, gameEventManager))
            }

            PopupState.CHARACTER -> {
                popup.add(scene2d.characterSelectPopup(preferences, audioService, gameEventManager))
            }

            PopupState.NAME -> {
                popup.add(scene2d.nameChangePopup(preferences, audioService, gameEventManager))
            }

            PopupState.NONE -> {}
        }
    }

    override fun setHomePopupState(state: PopupState) {
        setPopup(state)
        log.debug { "Changing popup to $state" }
    }

    override fun playerChanged(player: PlayerModel) {
        nameButton.setText(player.name)
        log.debug { "Changing player name to ${player.name}" }
    }

    override fun debugMode() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hide()
            show()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            // SHOW settings popup
            setPopup(PopupState.SETTING)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            // SHOW character popup
            setPopup(PopupState.CHARACTER)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            // SHOW name popup
            setPopup(PopupState.NAME)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            // HIDE popup
            setPopup(PopupState.NONE)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            // Go to Finish Screen
            transitionOut<FinishScreen>()
        }
    }

    companion object {
        private val log = logger<HomeScreen>()
    }
}
