package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.SCREEN_W
import com.github.doandadr.petualanganhijaiyah.asset.Buttons
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.data.LevelSavedData
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.data.levelsData
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_MAP_STAR
import com.github.doandadr.petualanganhijaiyah.ui.widget.LevelButton
import com.github.doandadr.petualanganhijaiyah.ui.widget.StarWidget
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import com.github.doandadr.petualanganhijaiyah.ui.widget.levelButton
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.actors.onTouchDown
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.actors
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.scene2d.vis.floatingGroup


class MapScreen(game: Main) : BaseScreen(game) {
    private lateinit var scrollView: ScrollPane
    private lateinit var homeButton: ImageButton
    private lateinit var totalScore: Label
    private lateinit var totalStar: Label
    private val levelButtons: MutableList<LevelButton> = mutableListOf()

    private val levels = levelsData

    override fun show() {
        super.show()
        log.debug { "Map Screen is shown" }

        audioService.play(MusicAsset.MAP)

        setupUI()
        loadLevels()
        transitionIn()
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                showTutorials()
            }
        }, 0.3f)
    }

    private fun showTutorials() {
        val levelsSavedData =
            preferences[PrefKey.LEVEL_SAVE_DATA.key, mutableListOf<LevelSavedData>()].apply { this.sortBy { it.number } }
        if (levelsSavedData.find { it.number == 1 && !it.hasCompleted } != null) {
            gameEventManager.dispatchShowTutorialEvent(
                levelButtons.first().label,
                TutorialType.MAP_LEVEL1
            )
        }
        if (levelsSavedData.find { it.number == 8 && it.hasCompleted } != null
            && levelsSavedData.find { it.number == 9 && !it.hasCompleted } != null
        ) {
            gameEventManager.dispatchShowTutorialEvent(
                levelButtons[8].label,
                TutorialType.MAP_LEVEL9
            )
        }
    }

    private fun setupUI() {
        val bgMap = TextureRegionDrawable(assets[TextureAsset.MAP.descriptor])

        stage.actors {
            scrollView = scrollPane {
                setFillParent(true)
                setScrollingDisabled(true, false)
                scrollPercentY = 1f
                updateVisualScroll()

                table {
                    background(bgMap)
                    align(Align.bottomLeft)

                    floatingGroup {
                        setFillParent(true)
                        levelButtons.clear()
                        levelButtons += levelButton(Buttons.LEVEL1.style) {
                            setPosition(-30f, -40f)
                            text.setPosition(570f, 380f)
                            rotateText(5f)
                            dots.setPosition(centerX(width) - 20f, 100f)
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(480f, 200f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL2.style) {
                            setPosition(30f, 440f)
                            text.setPosition(200f, 150f)
                            rotateText(-5f)
                            dots.setPosition(centerX(width) - 10f, -80f)
                            dots.flipHorizontal()
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(30f, -50f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL3.style) {
                            setPosition(450f, 750f)
                            text.setPosition(100f, 230f)
                            rotateText(-8f)
                            dots.setPosition(-130f, -100f)
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(30f, -50f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL4.style) {
                            setPosition(-100f, 1000f)
                            text.setPosition(300f, 150f)
                            rotateText(5f)
                            dots.setPosition(centerX(width) + 120f, -50f)
                            dots.flipHorizontal()
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(180f, -50f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL5.style) {
                            setPosition(-10f, 1370f)
                            text.setPosition(370f, 300f)
                            dots.setPosition(centerX(width) - 100f, -100f)
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(280f, 150f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL6.style) {
                            setPosition(420f, 1850f)
                            text.setPosition(120f, 200f)
                            rotateText(5f)
                            dots.setPosition(-120f, -120f)
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(50f, -50f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL7.style) {
                            setPosition(30f, 2000f)
                            text.setPosition(200f, 200f)
                            rotateText(-10f)
                            dots.setPosition(centerX(width) - 30f, -30f)
                            dots.flipHorizontal()
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(50f, 0f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL8.style) {
                            setPosition(350f, 2400f)
                            text.setPosition(200f, 100f)
                            rotateText(8f)
                            dots.setPosition(0f, -180f)
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(130f, -50f)
                        }

                        levelButtons += levelButton(Buttons.LEVEL9.style) {
                            setPosition(50f, 2600f)
                            text.setPosition(100f, 100f)
                            rotateText(-5f)
                            dots.setPosition(centerX(width) - 40f, -120f)
                            dots.flipHorizontal()
                            starWidget.setScale(SCALE_MAP_STAR)
                            starWidget.setPosition(30f, -50f)
                        }
                    }
                }
                validate()
                scrollPercentY = 1f
            }

            table {
                setFillParent(true)
                table {
                    it.padLeft(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expandX()
                        .align(Align.topLeft)
                    image(Drawables.ICON_STAR_SMALL.drawable) {
                        it.padRight(-40f)
                    }
                    totalStar = label("", Labels.TEXTBOX_ORANGE_ROUNDED.style) {
                        toBack()
                        setAlignment(Align.center)
                    }
                }
                table {
                    it.padRight(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expandX()
                        .align(Align.topRight)
                    image(Drawables.ICON_DIAMOND.drawable) {
                        it.padRight(-40f)
                    }
                    totalScore = label("", Labels.TEXTBOX_BLUE_ROUNDED.style) {
                        toBack()
                        setAlignment(Align.center)
                    }
                }

                row()
                label("PETA", Labels.MAP.style) {
                    it.padTop(PADDING_INNER_SCREEN).align(Align.top).colspan(2)
                    setAlignment(Align.center)
                }

                row()
                add().expand()

                row()
                homeButton = imageButton(ImageButtons.HOME.style) {
                    it.padLeft(PADDING_INNER_SCREEN).padBottom(PADDING_INNER_SCREEN)
                        .align(Align.bottomLeft)
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

private fun loadLevels() {
    val levelsSavedData =
        preferences[PrefKey.LEVEL_SAVE_DATA.key, mutableListOf<LevelSavedData>()].apply { this.sortBy { it.number } }

    levelButtons.forEachIndexed { index, levelButton ->
        levelButton.setLevel(levels[index])
        val number = index + 1
        val level = levelsSavedData.find { it.number == number }
            ?: LevelSavedData(number = number).also {
                levelsSavedData.add(it)
            }

        when {
            level.hasCompleted -> {
                levelButton.setState(LevelButton.LevelButtonState.PASSED)
                levelButton.setStarCount(level.starCount)
                setListener(levelButton, index)
            }

            level.number == 1 || levelsSavedData.find { it.number == number - 1 }?.hasCompleted == true -> {
                levelButton.setState(LevelButton.LevelButtonState.AVAILABLE)
                levelButton.setStarCount(0)
                setListener(levelButton, index)
            }

            level.number == 2 || levelsSavedData.find { it.number == number - 2 }?.hasCompleted == true -> {
                levelButton.setState(LevelButton.LevelButtonState.INACCESSIBLE)
                levelButton.starWidget.setState(StarWidget.StarState.HIDDEN)
            }

            else -> {
                levelButton.setState(LevelButton.LevelButtonState.HIDDEN)
            }
        }

        log.debug { "Show ${levels[index].name}, completed? ${level.hasCompleted} with saved score:${level.highScore} star:${level.starCount} time:${level.recordTime}" }
    }

    totalStar.setText(levelsSavedData.fold(0) { sum, level -> sum + level.starCount }
        .toString())
    totalScore.setText(
        levelsSavedData.fold(0f) { sum, level -> sum + level.highScore }.toInt().toString()
    )

    scrollView.run {
        updateVisualScroll()
        scrollPercentY = 1 - (levelButtons[(levelsSavedData.findLast { it.hasCompleted }?.number
            ?: 1) - 1].y / stage.height)
        log.debug { "ScrollY percent: $scrollPercentY" }
    }

    preferences.flush {
        this[PrefKey.LEVEL_SAVE_DATA.key] = levelsSavedData
    }
}

private fun setListener(levelButton: LevelButton, index: Int) {
    levelButton.onTouchDown {
        this.clearActions()
        this += Animations.pulse(scale = 0.1f)
        audioService.play(SoundAsset.BUTTON_POP)
    }
    levelButton.onChangeEvent {
        preferences.flush { this[PrefKey.CURRENT_LEVEL.key] = levels[index].number }
        transitionOut<LevelScreen>()
    }
}

    override fun debugMode() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hide()
            show()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            // Unlock all levels
            val levelsSavedData =
                preferences[PrefKey.LEVEL_SAVE_DATA.key, mutableListOf<LevelSavedData>()].apply { this.sortBy { it.number } }
            levelsSavedData.forEach { it.hasCompleted = true }
            loadLevels()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            levelButtons.first().setState(LevelButton.LevelButtonState.AVAILABLE)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            levelButtons.first().setState(LevelButton.LevelButtonState.PASSED)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            scrollView.scrollPercentY = 0f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            scrollView.scrollPercentY = 1f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            levelButtons.first().starWidget.setState(StarWidget.StarState.HIDDEN)
            levelButtons.first().setState(LevelButton.LevelButtonState.HIDDEN)
        }
    }

    companion object {
        private val log = logger<MapScreen>()
        private fun centerX(x:Float) = SCREEN_W /2f-x/2f
    }
}
