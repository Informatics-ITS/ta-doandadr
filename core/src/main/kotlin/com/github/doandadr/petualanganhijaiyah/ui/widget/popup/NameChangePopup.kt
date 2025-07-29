package com.github.doandadr.petualanganhijaiyah.ui.widget.popup

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextButtons
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.screen.HomeScreen.PopupState
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import ktx.actors.onChangeEvent
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.textButton
import ktx.scene2d.textField

class NameChangePopup(
    private val preferences: Preferences,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    private val nameField: TextField
    private val confirmButton: TextButton

    init {
        label("Ubah Nama", Labels.BOARD.style) {
            setAlignment(Align.center)
        }
        row()

        this@NameChangePopup.nameField = textField {
            it.spaceTop(50.0f).spaceBottom(50.0f).prefWidth(480.0f)
            alignment = Align.center
            messageText = "Nama"
        }
        row()

        this@NameChangePopup.confirmButton = textButton("OK", TextButtons.GREEN_LARGE.style) {
            isTransform = true
            setOrigin(Align.center)
            onTouchDown {
                this.clearActions()
                this += Animations.pulse()
                this@NameChangePopup.audioService.play(SoundAsset.BUTTON_POP)
            }
        }

        setupListeners()
        showTutorial()
    }

    private fun showTutorial() {
        Gdx.app.postRunnable {
            gameEventManager.dispatchShowTutorialEvent(nameField, TutorialType.NAME_CHANGE)
        }
    }

    private fun setupListeners() {
        confirmButton.onChangeEvent {
            val player = preferences[PrefKey.PLAYER.key, PlayerModel()]
            player.name = nameField.text
            preferences.flush {
                this[PrefKey.PLAYER.key] = player
            }
            gameEventManager.dispatchPlayerChangedEvent(player)
            gameEventManager.dispatchSetHomePopupStateEvent(PopupState.CHARACTER)
        }
    }
}

inline fun <S> KWidget<S>.nameChangePopup(
    preferences: Preferences,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: NameChangePopup.(S) -> Unit = {}
) = actor(
    NameChangePopup(
        preferences,
        audioService,
        gameEventManager,
    ), init
)
