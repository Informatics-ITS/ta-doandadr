package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_SMALL
import ktx.preferences.get
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.table

class PlayerInfoWidget(
    private val preferences: Preferences,
    skin: Skin = Scene2DSkin.defaultSkin,
): Table(skin), KTable {
    private val nameSign: Label
    private val portrait: Image
    private val hearts = mutableListOf<Image>()
    private val heartsView: Table
    private var health: Int = 5

    private val player = preferences[PrefKey.PLAYER.key, PlayerModel()]

    init {
        table {
            this@PlayerInfoWidget.nameSign = label(this@PlayerInfoWidget.player.name, Labels.SIGN_NAME.style) {
                it.padLeft(10f).padRight(-20f).minWidth(200f)
                setAlignment(Align.center)
                setFontScale(4f / MathUtils.clamp(this@PlayerInfoWidget.player.name.length, 4, 8))
                setScale(SCALE_BTN_SMALL)
            }
            row()
            this@PlayerInfoWidget.heartsView = table {
                it.padRight(10f).padTop(10f)
            }
        }
        this@PlayerInfoWidget.portrait = image {
            it.size(120f)
        }
    }

    fun loadWidget(maxHearts: Int) {
        health = maxHearts
        heartsView.clear()
        hearts.clear()
        for (i in  0 until maxHearts) {
            val heart = Image(skin.getDrawable(Drawables.ICON_HEART_EMPTY.drawable))
            heart.setOrigin(Align.center)
            hearts.add(heart)
            heartsView.add(hearts[i]).prefSize(40f)
        }
        setPlayerPortrait(player.character)
        setHeartCount(maxHearts)
    }

    private fun setHeartCount(hearts: Int) {
        this.hearts.forEach {
            it.drawable = skin.getDrawable(Drawables.ICON_HEART_EMPTY.drawable)
        }
        this.hearts.take(hearts).forEach { it.drawable = skin.getDrawable(Drawables.ICON_HEART_FULL.drawable) }
    }

    fun loseHealth() : Int {
        health -= 1
        setHeartCount(health)
        return health
    }

    private fun setPlayerPortrait(playerCharacter: String) {
        if (playerCharacter == "boy") {
            portrait.drawable = skin.getDrawable(Drawables.BOY_SELECT.drawable)
        } else {
            portrait.drawable = skin.getDrawable(Drawables.GIRL_SELECT.drawable)
        }
    }
}

inline fun <S> KWidget<S>.playerInfoWidget(
    preferences: Preferences,
    init: PlayerInfoWidget.(S) -> Unit = {}
) = actor(
    PlayerInfoWidget(
        preferences,
    ), init
)
