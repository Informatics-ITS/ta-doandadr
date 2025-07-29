package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_BTN_MEDIUM
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SPLASH
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.preferences.get
import ktx.scene2d.KImageButton
import ktx.scene2d.actors
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table

class FinishScreen(game: Main): BaseScreen(game) {
    private lateinit var portrait: Image
    private lateinit var bubbleText: Label
    private lateinit var congratsText: Label
    private lateinit var homeButton: KImageButton
    private lateinit var totalScore: Label
    private lateinit var totalStar: Label
    private lateinit var player: PlayerModel

    override fun show() {
        super.show()

        setupData()
        setupAudio()
        setupUI()
        transitionIn()
    }

    private fun setupData() {
        player = preferences[PrefKey.PLAYER.key, PlayerModel()]
        log.debug { player.toString() }
    }

    private fun setupAudio() {
        audioService.play(MusicAsset.VICTORY)
        audioService.play(SoundAsset.CHEER_BIG)
    }

    private fun setupUI() {
        val bgFinish = assets[TextureAsset.FINISH.descriptor]

        stage.actors {
            image(bgFinish) {
                setFillParent(true)
                setScaling(Scaling.fill)
            }

            table {
                setFillParent(true)

                table {
                    it.padLeft(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expandX().align(Align.topLeft)
                    image(Drawables.ICON_STAR_SMALL.drawable) {
                        it.padRight(-40f)
                    }
                    totalStar = label(player.totalStar.toString(), Labels.TEXTBOX_ORANGE_ROUNDED.style) {
                        toBack()
                        setAlignment(Align.center)
                    }
                }
                table {
                    it.padRight(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expandX().align(Align.topRight)
                    image(Drawables.ICON_DIAMOND.drawable) {
                        it.padRight(-40f)
                    }
                    totalScore = label(player.totalScore.toInt().toString(), Labels.TEXTBOX_BLUE_ROUNDED.style) {
                        toBack()
                        setAlignment(Align.center)
                    }
                }

                row()
                congratsText = label("SELAMAT\n" +
                    "${player.name.uppercase()}!", Labels.PRIMARY_GREEN_WHITE_BORDER.style) {
                    it.padLeft(PADDING_INNER_SCREEN).padTop(50f).expandX().align(Align.bottomLeft).colspan(2)
                    setFontScale(SCALE_FONT_SPLASH)
                }

                row()
                bubbleText = label("Alhamdulillah, ${player.name}!\n" +
                    "kamu sudah berhasil\n" +
                    "menamatkan\n" +
                    "petualangan hijaiyah!", Labels.BUBBLE.style) {
                    it.expandX().padRight(PADDING_INNER_SCREEN).align(Align.topRight).padTop(-50f).colspan(2)
                    setFontScale(SCALE_FONT_SMALL)
                    setAlignment(Align.center)
                    toBack()
                }

                row()
                portrait = image(if (player.character == "girl") Drawables.GIRL.drawable else Drawables.BOY.drawable) {
                    it.padLeft(PADDING_INNER_SCREEN).padBottom(PADDING_INNER_SCREEN).expand().align(Align.left)
                    setOrigin(Align.left)
                    setScale(SCALE_BTN_MEDIUM)
                }

                homeButton = imageButton(ImageButtons.HOME.style) {
                    it.padRight(PADDING_INNER_SCREEN).padBottom(PADDING_INNER_SCREEN).expandX().align(Align.bottomRight)
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
        private val log = logger<FinishScreen>()
    }
}
