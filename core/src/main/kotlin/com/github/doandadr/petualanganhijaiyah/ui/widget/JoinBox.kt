package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.asset.Buttons
import com.github.doandadr.petualanganhijaiyah.asset.Hijaiyah
import com.github.doandadr.petualanganhijaiyah.asset.HijaiyahJoined
import com.github.doandadr.petualanganhijaiyah.asset.TextureAtlasAsset
import com.github.doandadr.petualanganhijaiyah.ui.values.SIZE_JOINED
import ktx.assets.async.AssetStorage
import ktx.scene2d.*

class JoinBox(
    private var hijaiyahJoined: HijaiyahJoined,
    private val type: Type,
    val content: Content,
    assets: AssetStorage,
    skin: Skin = Scene2DSkin.defaultSkin
) : Table(skin), KTable {
    private var optionCharacters: HorizontalGroup
    private val boxStack: Stack
    private var frame: Button
    private var image: Image
    private val textAtlas = assets[TextureAtlasAsset.HIJAIYAH.descriptor]

    init {
        this@JoinBox.boxStack = stack {
            it.prefHeight(SIZE_JOINED)

            this@JoinBox.frame = button(Buttons.JOIN.style) {
                isTransform = true
                setOrigin(Align.center)
                if (this@JoinBox.type == Type.QUESTION) {
                    scaleY = 1.3f
                    touchable = Touchable.disabled
                }
            }

            this@JoinBox.image = image {
                setScaling(Scaling.fit)
                touchable = Touchable.disabled
            }

            this@JoinBox.optionCharacters = horizontalGroup {
                space(10f)
                align(Align.center)
                touchable = Touchable.disabled
            }
        }

        loadWidget()
    }

    private fun loadWidget() {
        updateJoinedCharacters(hijaiyahJoined)
        setType(type)
        optionCharacters.clear()
        getCharacters(content).reversed().forEach { character ->
            val charImg = Image(textAtlas.findRegion(character.imageSmall))
            charImg.apply {
                setScaling(Scaling.fit)
            }
            optionCharacters.addActor(charImg)
        }
    }

    fun updateJoinedCharacters(characters: HijaiyahJoined) {
        hijaiyahJoined = characters
        image.drawable = TextureRegionDrawable(textAtlas.findRegion(hijaiyahJoined.image))
    }

    private fun getCharacters(content: Content): Array<Hijaiyah> {
        return when (content) {
            Content.ANSWER -> hijaiyahJoined.answer
            Content.OPTION_1 -> hijaiyahJoined.option1
            Content.OPTION_2 -> hijaiyahJoined.option2
        }
    }

    enum class Type {
        QUESTION,
        OPTION
    }

    enum class State {
        CORRECT,
        INCORRECT,
    }

    enum class Content {
        ANSWER,
        OPTION_1,
        OPTION_2,
    }

    private fun setType(type: Type) {
        when (type) {
            Type.QUESTION -> {
                optionCharacters.isVisible = false
                image.isVisible = true
            }

            Type.OPTION -> {
                optionCharacters.isVisible = true
                image.isVisible = false
            }
        }
    }

    fun setState(state: State) {
        when (state) {
            State.CORRECT -> {
                frame.apply {
                    style = skin.get(Buttons.JOIN_STATE.style, ButtonStyle::class.java)
                    isDisabled = false
                }
            }

            State.INCORRECT -> {
                frame.apply {
                    style = skin.get(Buttons.JOIN_STATE.style, ButtonStyle::class.java)
                    isDisabled = true
                }
            }
        }
    }
}

inline fun <S> KWidget<S>.joinBox(
    hijaiyahJoined: HijaiyahJoined,
    type: JoinBox.Type,
    content: JoinBox.Content,
    assets: AssetStorage,
    init: JoinBox.(S) -> Unit = {}
) = actor(
    JoinBox(
        hijaiyahJoined,
        type,
        content,
        assets,
    ), init
)
