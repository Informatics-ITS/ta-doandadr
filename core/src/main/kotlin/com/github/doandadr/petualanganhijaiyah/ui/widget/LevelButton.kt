package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.data.LevelModel
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_BIG
import com.github.doandadr.petualanganhijaiyah.ui.widget.StarWidget.StarState
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor

class LevelButton(
    buttonStyle: String,
    private val skin: Skin = Scene2DSkin.defaultSkin,
    val label: Label = Label("", skin, Labels.PRIMARY_GREY_WHITE_BORDER.style),
    button: Button = Button(skin, buttonStyle),
    val text: Container<Label> = Container(label),
    val dots: LevelDots = LevelDots(),
    val starWidget: StarWidget = StarWidget(),
) : WidgetGroup(button, text, dots, starWidget), KGroup {
    private lateinit var levelModel: LevelModel

    init {
        button.isTransform = true
        button.setOrigin(Align.center)
        setState(LevelButtonState.INACCESSIBLE)
        label.setFontScale(SCALE_FONT_BIG)
        touchable = Touchable.enabled
        label.touchable = Touchable.disabled
    }

    fun rotateText(degrees: Float) {
        text.rotation = degrees
    }

    private fun setTitle(text: String) {
        label.setText(text)
    }

    fun setLevel(level: LevelModel) {
        levelModel = level
        setTitle(levelModel.name.uppercase())
    }

    fun setState(state: LevelButtonState) {
        if (state == LevelButtonState.HIDDEN) {
            starWidget.setState(StarState.HIDDEN)
        }
        when (state) {
            LevelButtonState.INACCESSIBLE -> label.style =
                skin.get(Labels.PRIMARY_GREY_WHITE_BORDER.style, LabelStyle::class.java)
            LevelButtonState.AVAILABLE -> label.style =
                skin.get(Labels.PRIMARY_GREEN_WHITE_BORDER.style, LabelStyle::class.java)
            LevelButtonState.PASSED -> label.style =
                skin.get(Labels.PRIMARY_ORANGE_WHITE_BORDER.style, LabelStyle::class.java)
            LevelButtonState.HIDDEN -> label.isVisible = false
        }
        when (state) {
            LevelButtonState.INACCESSIBLE -> dots.setState(LevelDots.DotState.INACCESSIBLE)
            LevelButtonState.AVAILABLE -> dots.setState(LevelDots.DotState.AVAILABLE)
            LevelButtonState.PASSED -> dots.setState(LevelDots.DotState.PASSED)
            LevelButtonState.HIDDEN -> dots.setState(LevelDots.DotState.HIDDEN)
        }
    }

    fun setStarCount(stars: Int) {
        when(stars) {
            0 -> starWidget.setState(StarState.ZERO)
            1 -> starWidget.setState(StarState.ONE)
            2 -> starWidget.setState(StarState.TWO)
            3 -> starWidget.setState(StarState.THREE)
            else -> {starWidget.setState(StarState.HIDDEN)}
        }
    }

    enum class LevelButtonState {
        HIDDEN,
        INACCESSIBLE,
        AVAILABLE,
        PASSED
    }
}

inline fun <S> KWidget<S>.levelButton(
    buttonStyle: String,
    init: LevelButton.(S) -> Unit = {}
) = actor(
    LevelButton(
        buttonStyle,
    ), init
)
