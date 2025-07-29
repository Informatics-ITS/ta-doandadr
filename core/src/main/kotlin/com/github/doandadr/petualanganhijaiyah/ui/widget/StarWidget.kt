package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.actor
import ktx.scene2d.image
import ktx.scene2d.scene2d


class StarWidget(
    starOff1: Image = scene2d.image(Drawables.ICON_STARGREY_MEDIUM.drawable),
    starOff2: Image = scene2d.image(Drawables.ICON_STARGREY_LARGE.drawable),
    starOff3: Image = scene2d.image(Drawables.ICON_STARGREY_MEDIUM.drawable),
    private val starOn1: Image = scene2d.image(Drawables.ICON_STAR_MEDIUM.drawable),
    private val starOn2: Image = scene2d.image(Drawables.ICON_STAR_LARGE.drawable),
    private val starOn3: Image = scene2d.image(Drawables.ICON_STAR_MEDIUM.drawable),
) : WidgetGroup(starOff1, starOff2, starOff3, starOn1, starOn2, starOn3), KGroup {
    private lateinit var audioService: AudioService
    private var isAnimated = false

    constructor(audioService: AudioService) : this() {
        this.audioService = audioService
    }

    init {
        isTransform = true
        setOrigin(Align.center)
        touchable = Touchable.disabled
        listOf(starOn1, starOn2, starOn3).forEach { it.setOrigin(Align.center) }

        val bumpY = 20f
        val gapX = -10f
        starOn2.setPosition(starOn1.width + gapX, bumpY)
        starOn3.setPosition(starOn1.width + starOn2.width + 2 * gapX, starOn1.y)
        starOff2.setPosition(starOff1.width + gapX, bumpY)
        starOff3.setPosition(starOff1.width + starOff2.width + 2 * gapX, starOff1.y)

        setState(StarState.ZERO)
    }

    fun setAnimated(animated: Boolean) {
        isAnimated = animated
    }

    fun setState(state: StarState) {
        if (state == StarState.HIDDEN) {
            isVisible = false
            return
        }
        isVisible = true

        listOf(starOn1, starOn2, starOn3).forEach { it.clearActions(); it.isVisible = false }

        when (state) {
            StarState.ZERO -> {}

            StarState.ONE -> {
                if (isAnimated)
                    starOn1 += Actions.run { audioService.play(SoundAsset.STAR_1) } + Animations.star()
                else
                    starOn1.isVisible = true
            }

            StarState.TWO -> {
                if (isAnimated) starOn1 +=
                    Actions.run { audioService.play(SoundAsset.STAR_1) } + Animations.star()
                else
                    starOn1.isVisible = true
                if (isAnimated)
                    starOn2 += DelayAction(0.5f) +
                        Actions.run { audioService.play(SoundAsset.STAR_2) } +
                        Animations.star()
                else
                    starOn2.isVisible = true
            }

            StarState.THREE -> {
                if (isAnimated)
                    starOn1 += Actions.run { audioService.play(SoundAsset.STAR_1) } + Animations.star()
                else
                    starOn1.isVisible = true
                if (isAnimated)
                    starOn2 += DelayAction(0.5f) +
                        Actions.run { audioService.play(SoundAsset.STAR_2) } +
                        Animations.star()
                else
                    starOn2.isVisible = true
                if (isAnimated)
                    starOn3 += DelayAction(1f) +
                        Actions.run { audioService.play(SoundAsset.STAR_3) } +
                        Animations.star()
                else
                    starOn3.isVisible = true
            }

            else -> {}
        }
    }

    enum class StarState {
        HIDDEN,
        ZERO,
        ONE,
        TWO,
        THREE
    }
}

inline fun <S> KWidget<S>.starWidget(
    audioService: AudioService,
    init: StarWidget.(S) -> Unit = {}
) = actor(
    StarWidget(audioService), init
)
