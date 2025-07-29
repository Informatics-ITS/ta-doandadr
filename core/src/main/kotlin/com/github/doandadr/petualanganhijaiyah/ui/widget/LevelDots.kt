package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import ktx.scene2d.KGroup
import ktx.scene2d.Scene2DSkin


class LevelDots(
    val skin: Skin = Scene2DSkin.defaultSkin,
    private val dot1: Image = Image(skin.getDrawable(Drawables.CIRCLE_GREY.drawable)),
    private val dot2: Image = Image(skin.getDrawable(Drawables.CIRCLE_GREY.drawable)),
    private val dot3: Image = Image(skin.getDrawable(Drawables.CIRCLE_GREY.drawable)),
    private val dot4: Image = Image(skin.getDrawable(Drawables.CIRCLE_GREY.drawable))
) : WidgetGroup(dot1, dot2, dot3 ,dot4), KGroup {
    init {
        setInitPosition()
        setScale(0.9f)
        touchable = Touchable.disabled
    }

    private fun setInitPosition() {
        val gapY = 70f
        val gapX = 20f
        dot2.setPosition(1 * gapX, 1 * gapY)
        dot3.setPosition(3 * gapX, 1.9f * gapY)
        dot4.setPosition(6 * gapX, 2.7f * gapY)
        dot4.isVisible = false
    }

    fun flipHorizontal() {
        val gapY = 70f
        val gapX = 20f
        dot2.setPosition(-1 * gapX, 1 * gapY)
        dot3.setPosition(-3 * gapX, 1.9f * gapY)
        dot4.setPosition(-6 * gapX, 2.7f * gapY)
        dot4.isVisible = false
    }

    fun setState(state: DotState) {
        if (state == DotState.HIDDEN) {
            isVisible = false
            return
        }
        isVisible = true

        when (state) {
            DotState.INACCESSIBLE -> setDrawables(Drawables.CIRCLE_GREY.drawable)
            DotState.AVAILABLE -> setDrawables(Drawables.CIRCLE_GREEN.drawable)
            DotState.PASSED -> setDrawables(Drawables.CIRCLE_ORANGE.drawable)
            else -> {}
        }
    }

    private fun setDrawables(drawable: String) {
        dot1.setDrawable(skin, drawable)
        dot2.setDrawable(skin, drawable)
        dot3.setDrawable(skin, drawable)
    }

    enum class DotState {
        HIDDEN,
        INACCESSIBLE,
        AVAILABLE,
        PASSED
    }

    // TODO animate change state one by one
}
