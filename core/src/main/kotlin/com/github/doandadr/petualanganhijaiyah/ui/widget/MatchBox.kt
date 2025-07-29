package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import ktx.actors.alpha
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.image

class MatchBox(
    val hijaiyah: Hijaiyah,
    private val state: State,
    private val assets: AssetStorage,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    val box: HijaiyahBox
    val rightCircle:  Container<Actor>
    val leftCircle: Container<Actor>
    val leftDot: Image
    val rightDot: Image

    init {
        this@MatchBox.leftCircle = container {
            background = skin.getDrawable(Drawables.CIRCLE_ORANGE.drawable)
            isVisible = this@MatchBox.state == State.RIGHT
            it.padRight(PADDING_IN)
            this@MatchBox.leftDot = image(Drawables.CIRCLE_BRUSH.drawable) {
                alpha = 0.1f
                log.debug { "leftDot isTouchable $touchable" }
            }
            log.debug { "leftCircle isTouchable $touchable" }
        }
        this@MatchBox.box = hijaiyahBox(
            hijaiyah,
            HijaiyahBox.Size.SMALL,
            assets
        ) {
            toBack()
        }
        this@MatchBox.rightCircle = container{
            background = skin.getDrawable(Drawables.CIRCLE_GREEN.drawable)
            isVisible = this@MatchBox.state == State.LEFT
            log.debug { "rightCircle isTouchable $touchable" }
            it.padLeft(PADDING_IN)
            this@MatchBox.rightDot = image(Drawables.CIRCLE_BRUSH.drawable) {
                alpha = 1f
                log.debug { "rightDot isTouchable $touchable" }
            }
        }
    }

    enum class State {
        LEFT,
        RIGHT
    }

    companion object {
        private val log = logger<MatchBox>()

        private const val PADDING_IN = -10f
    }
}
