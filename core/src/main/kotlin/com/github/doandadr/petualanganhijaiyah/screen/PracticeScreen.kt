package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.practiceStage
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table


class PracticeScreen(game: Main) : BaseScreen(game) {

    override fun show() {
        super.show()
        log.debug { "Practice Screen is shown" }
        setupUI()
        transitionIn()
    }

    private fun setupUI() {
        val bgPractice = assets[TextureAsset.PRACTICE.descriptor]

        stage.actors {
            table {
                background(TextureRegionDrawable(bgPractice))
                setFillParent(true)

                add().expand()
                row()

                label("Ayo Belajar!", Labels.TEXTBOX_WHITE_SQUARE_LARGE.style) {
                    setAlignment(Align.center)
                    setFontScale(SCALE_BTN_MEDIUM)
                }
                row()

                practiceStage(assets, audioService, gameEventManager) {
                    it.grow()
                }

                row()
                imageButton(ImageButtons.BACK.style) {
                    it.expandX().align(Align.bottomRight).padBottom(PADDING_INNER_SCREEN).padRight(PADDING_INNER_SCREEN)
                    isTransform = true
                    setOrigin(Align.center)
                    onTouchDown {
                        this.clearActions()
                        this += Animations.pulse()
                        audioService.play(SoundAsset.BUTTON_POP)
                    }
                    onChange {
                        transitionOut<HomeScreen>()
                    }
                }
            }
        }
    }

    companion object {
        private val log = logger<PracticeScreen>()
    }
}
