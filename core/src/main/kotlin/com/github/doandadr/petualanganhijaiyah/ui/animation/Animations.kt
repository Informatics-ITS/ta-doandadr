package com.github.doandadr.petualanganhijaiyah.ui.animation

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.hide
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel
import com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.actions.Actions.show
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import ktx.actors.plus

object Animations {
    fun star(duration: Float = 0.5f): SequenceAction {
        val showStart = show()
        val alphaStart = alpha(0f)
        val moveByStart = moveBy(-20f, -20f)
        val scaleToStart = scaleBy(4f, 4f)
        val rotateByStart = rotateBy(-360f)
        val start =
            parallel(showStart, alphaStart, moveByStart, scaleToStart, rotateByStart)

        val alpha = fadeIn(duration)
        val moveBy = moveBy(20f, 20f, duration, Interpolation.fade)
        val scaleBy = scaleBy(-4f, -4f, duration, Interpolation.fade)
        val rotateBy = rotateBy(360f, duration, Interpolation.fade)
        val animation = parallel(alpha, moveBy, scaleBy, rotateBy)

        return start + animation
    }

    fun pulse(
        scale: Float = 0.2f,
        initScale: Float = 1f,
        duration: Float = 0.3f
    ): SequenceAction {
        val scaleTo = scaleTo(initScale, initScale)
        val scaleUp = scaleBy(scale, scale, duration, Interpolation.fade)
        val scaleDown = scaleBy(-scale, -scale, duration, Interpolation.fade)

        return scaleTo + scaleUp + scaleDown
    }

    fun fadeInOut(
        fadeDuration: Float = 0.2f,
        stillDuration: Float = 0.5f
    ): SequenceAction {
        val fadeIn = fadeIn(fadeDuration)
        val delay = DelayAction(stillDuration)
        val fadeOut = fadeOut(fadeDuration)

        return fadeIn + delay + fadeOut
    }

    fun popIn(duration: Float = 0.3f, scale: Float = 1f): SequenceAction {
        val initScale = scaleTo(0.1f, 0.1f)
        val show = show()
        val popIn = scaleTo(scale, scale, duration, Interpolation.fade)

        return initScale + show + popIn
    }

    fun appear(duration: Float = 0.5f): SequenceAction {
        val show = show()
        val fadeIn = fadeIn(duration)

        return show + fadeIn
    }

    fun disappear(duration: Float = 0.5f): SequenceAction {
        val fadeOut = fadeOut(duration)
        val hide = hide()

        return fadeOut + hide
    }
}
