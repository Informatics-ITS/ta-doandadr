package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.image
import ktx.scene2d.label

class TimerCounter(
    var maxSeconds: Float,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    private val colon: Image
    private val minute: Label
    private val second: Label

    init {
        minute = label("", Labels.TIMER_COUNTER.style) {
            setAlignment(Align.center)
        }
        colon = image(skin.getDrawable(Drawables.TIMER_COUNTER_DOTS.drawable))
        second = label("", Labels.TIMER_COUNTER.style) {
            setAlignment(Align.center)
        }

        resetTimer(maxSeconds)
    }

    fun updateTime(time: Float) {
        minute.setText(formatMM(time))
        second.setText(formatSS(time))
    }

    fun resetTimer(maxTime: Float) {
        updateTime(maxTime)
    }

    private fun formatMM(sec: Float): String = (sec / 60).toInt().toString().padStart(2, '0')

    private fun formatSS(sec: Float): String = (sec % 60).toInt().toString().padStart(2, '0')
}

inline fun <S> KWidget<S>.timerCounter(
    setSeconds: Float,
    init: TimerCounter.(S) -> Unit = {}
) = actor(
    TimerCounter(
        setSeconds
    ), init
)
