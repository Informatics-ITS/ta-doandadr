package com.github.doandadr.petualanganhijaiyah.ui.widget.popup

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Buttons
import com.github.doandadr.petualanganhijaiyah.asset.Colors
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.screen.HomeScreen.PopupState
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
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
import ktx.scene2d.button
import ktx.scene2d.label
import ktx.scene2d.verticalGroup

class CharacterSelectPopup(
    private val preferences: Preferences,
    private val audioService: AudioService,
    private val gameEventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    private val girlButton: Button
    private val boyButton: Button

    private val player = preferences[PrefKey.PLAYER.key, PlayerModel()]

    init {
        verticalGroup {
            space(50f)
            label("PILIH KARAKTER", Labels.BOARD.style).setAlignment(Align.center)
            this@CharacterSelectPopup.girlButton = button(Buttons.GIRL_SELECT.style) {
                color = skin.getColor(if (this@CharacterSelectPopup.player.character == GIRL) Colors.LIGHT_GREEN.color else Colors.WHITE.color)
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@CharacterSelectPopup.audioService.play(SoundAsset.BUTTON_POP)
                }
            }
            this@CharacterSelectPopup.boyButton = button(Buttons.BOY_SELECT.style) {
                color = skin.getColor(if (this@CharacterSelectPopup.player.character == BOY) Colors.LIGHT_GREEN.color else Colors.WHITE.color)
                isTransform = true
                setOrigin(Align.center)
                onTouchDown {
                    this.clearActions()
                    this += Animations.pulse()
                    this@CharacterSelectPopup.audioService.play(SoundAsset.BUTTON_POP)
                }
            }
        }

        setupListeners()
    }

    private fun handleSelected(selected: String) {
        girlButton.color = skin.getColor(if (selected == GIRL) Colors.LIGHT_GREEN.color else Colors.WHITE.color)
        boyButton.color = skin.getColor(if (selected == BOY) Colors.LIGHT_GREEN.color else Colors.WHITE.color)
    }

    private fun setupListeners() {
        girlButton.onChangeEvent {
            preferences.flush {
                player.character = GIRL
                this[PrefKey.PLAYER.key] = player
            }
            handleSelected(GIRL)
            gameEventManager.dispatchSetHomePopupStateEvent(PopupState.NONE)
        }
        boyButton.onChangeEvent {
            preferences.flush {
                player.character = BOY
                this[PrefKey.PLAYER.key] = player
            }
            handleSelected(BOY)
            gameEventManager.dispatchSetHomePopupStateEvent(PopupState.NONE)
        }
    }

    companion object {
        private const val GIRL = "girl"
        private const val BOY = "boy"
    }
}

inline fun <S> KWidget<S>.characterSelectPopup(
    preferences: Preferences,
    audioService: AudioService,
    gameEventManager: GameEventManager,
    init: CharacterSelectPopup.(S) -> Unit = {}
) = actor(
    CharacterSelectPopup(
        preferences,
        audioService,
        gameEventManager,
    ), init
)
