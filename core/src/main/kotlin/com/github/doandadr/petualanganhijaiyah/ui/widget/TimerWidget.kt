package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.event.GameEventManager
import com.github.doandadr.petualanganhijaiyah.ui.values.SPACE_BETWEEN_BUTTONS
import ktx.log.logger
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor

class TimerWidget(
    private val audioService: AudioService,
    private val eventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
) : Table(skin), KTable {
    private var isTicking: Boolean = false
    private var deltaTimerCount: Float = 0f
    private var maxSeconds: Float = DEFAULT_MAX_TIME
    var remainingSeconds: Float = DEFAULT_MAX_TIME
    var elapsedSeconds: Float = 0.0f

    private val counter: TimerCounter
    private var state: State = State.STOP
    val bar: TimerBar

    init {
        this@TimerWidget.bar = timerBar(DEFAULT_MAX_TIME) {
            it.align(Align.topLeft)
        }

        row()
        this@TimerWidget.counter = timerCounter(DEFAULT_MAX_TIME) {
            it.spaceTop(SPACE_BETWEEN_BUTTONS).align(Align.topLeft)
        }
    }

    fun loadWidget(maxTime: Float) {
        maxSeconds = maxTime
        remainingSeconds = maxSeconds
        elapsedSeconds = 0f
        setState(State.START)
        bar.maxSeconds = maxSeconds
        counter.maxSeconds = maxSeconds
    }

    fun update(deltaTime: Float) {
        when (state) {
            State.DISABLED -> return
            State.STOP -> return
            State.START -> {
                deltaTimerCount += deltaTime
                if (deltaTimerCount >= 1) {
                    setState(State.RESUME)
                    deltaTimerCount = 0f
                }
                counter.resetTimer(maxSeconds)
                bar.resetTimer(maxSeconds)
            }
            State.RESUME -> {
                deltaTimerCount += deltaTime
                if (deltaTimerCount >= 1) {
                    remainingSeconds -= 1
                    elapsedSeconds += 1
                    counter.updateTime(remainingSeconds)
                    bar.updateTime(remainingSeconds)
                    deltaTimerCount = 0f

                    if (remainingSeconds == 10f && !isTicking) {
                        audioService.play(SoundAsset.TIMER)
                        isTicking = true
                    }
                }
            }
            State.STOPWATCH -> {
                deltaTimerCount += deltaTime
                if (deltaTimerCount >= 1) {
                    elapsedSeconds += 1
                    counter.updateTime(elapsedSeconds)
                    deltaTimerCount = 0f
                }
            }
        }

        if (remainingSeconds <= 0f) {
            setState(State.STOP)
            eventManager.dispatchLevelFailEvent()
        }
    }

    fun reset(time: Float) {
        maxSeconds = time
        bar.resetTimer(maxSeconds)
        counter.resetTimer(maxSeconds)
    }

    fun setState(timerState: State) {
        state = timerState
        bar.isVisible = (state != State.STOPWATCH)
        isVisible = (state != State.DISABLED)
    }

    fun startStopwatch() {
        bar.remove()
        setState(State.STOPWATCH)
    }

    fun start() {
        setState(State.START)
    }

    fun stop() {
        setState(State.STOP)
    }

    fun loseSeconds(time: Float) {
        remainingSeconds -= time
    }

    enum class State {
        STOP,
        RESUME,
        STOPWATCH,
        START,
        DISABLED,
    }

    companion object {
        private const val DEFAULT_MAX_TIME = 120f
        private val log = logger<TimerWidget>()
    }
}

inline fun <S> KWidget<S>.timerWidget(
    audioService: AudioService,
    eventManager: GameEventManager,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: TimerWidget.(S) -> Unit = {}
) = actor(
    TimerWidget(
        audioService,
        eventManager,
        skin
    ), init
)
