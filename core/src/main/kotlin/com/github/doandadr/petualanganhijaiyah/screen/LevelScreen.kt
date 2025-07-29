package com.github.doandadr.petualanganhijaiyah.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.asset.Drawables
import com.github.doandadr.petualanganhijaiyah.asset.ImageButtons
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import com.github.doandadr.petualanganhijaiyah.data.LevelModel
import com.github.doandadr.petualanganhijaiyah.data.LevelSavedData
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.data.StageModel
import com.github.doandadr.petualanganhijaiyah.data.StageType
import com.github.doandadr.petualanganhijaiyah.data.levelsData
import com.github.doandadr.petualanganhijaiyah.ui.animation.Animations
import com.github.doandadr.petualanganhijaiyah.ui.values.PADDING_INNER_SCREEN
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_BIG
import com.github.doandadr.petualanganhijaiyah.ui.values.SPACE_BETWEEN_BUTTONS
import com.github.doandadr.petualanganhijaiyah.ui.values.STAGE_BOX_HEIGHT
import com.github.doandadr.petualanganhijaiyah.ui.values.STAGE_BOX_WIDTH
import com.github.doandadr.petualanganhijaiyah.ui.widget.LevelFinishView
import com.github.doandadr.petualanganhijaiyah.ui.widget.PlayerInfoWidget
import com.github.doandadr.petualanganhijaiyah.ui.widget.TimerWidget
import com.github.doandadr.petualanganhijaiyah.ui.widget.levelFinishView
import com.github.doandadr.petualanganhijaiyah.ui.widget.playerInfoWidget
import com.github.doandadr.petualanganhijaiyah.ui.widget.popup.AnswerPopup
import com.github.doandadr.petualanganhijaiyah.ui.widget.popup.answerPopup
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.dragAndDropStage
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.drawingStage
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.matchLineStage
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.mcqJoin
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.mcqStage
import com.github.doandadr.petualanganhijaiyah.ui.widget.stages.mcqVoiceStage
import com.github.doandadr.petualanganhijaiyah.ui.widget.timerWidget
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.onTouchDown
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.actors
import ktx.scene2d.horizontalGroup
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.stack
import ktx.scene2d.table

class LevelScreen(
    game: Main,
) : BaseScreen(game) {

    private lateinit var popup: Table
    private lateinit var layout: Table
    private lateinit var levelTitle: Label
    private lateinit var stageBoard: Table
    private lateinit var timer: TimerWidget
    private lateinit var playerInfo: PlayerInfoWidget
    private lateinit var backgroundImg: Image
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton

    private lateinit var levels: Array<LevelModel>

    private var currentScore = 0f
    private var currentStar = 0
    private var currentRecordTime = Float.MAX_VALUE
    private var currentRound = 1
    private var currentStageIndex = 0
    private lateinit var currentLevel: LevelModel
    private lateinit var currentStage: StageModel
    private var newBestScore = false
    private var newBestTime = false

    override fun show() {
        super.show()

        setupData()
        setupUI()
        loadLevel(preferences[PrefKey.CURRENT_LEVEL.key, 1])
        transitionIn()
    }

    private fun setupData() {
        levels = levelsData
        currentScore = 0f
        currentStar = 0
        currentRecordTime = Float.MAX_VALUE
        currentRound = 1
        currentStageIndex = 0
        newBestScore = false
        newBestTime = false
    }

    private fun setupUI() {
        val bgDim = TextureRegionDrawable(assets[TextureAsset.DIM.descriptor])

        stage.actors {
            backgroundImg = image {
                setFillParent(true)
                setScaling(Scaling.fill)
            }
            layout = table {
                setFillParent(true)

                timer = timerWidget(audioService, gameEventManager) {
                    it.padLeft(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expand()
                        .align(Align.topLeft)
                }

                playerInfo = playerInfoWidget(preferences) {
                    it.padRight(PADDING_INNER_SCREEN).padTop(PADDING_INNER_SCREEN).expand()
                        .align(Align.topRight)
                }

                row()
                table {
                    it.colspan(2)
                    levelTitle = label("", Labels.PRIMARY_GREEN_WHITE_BORDER.style) {
                        setAlignment(Align.center)
                        setFontScale(SCALE_FONT_BIG)
                    }

                    row()
                    image(Drawables.BISMILLAH.drawable)

                }

                row()
                stageBoard = table {
                    it.colspan(2).expand().growY().prefWidth(STAGE_BOX_WIDTH)
                        .prefHeight(STAGE_BOX_HEIGHT)
                }

                row()
                horizontalGroup {
                    it.padLeft(PADDING_INNER_SCREEN).padBottom(PADDING_INNER_SCREEN).expand()
                        .align(Align.bottomLeft)
                    space(SPACE_BETWEEN_BUTTONS)
                    homeButton = imageButton(ImageButtons.HOME.style) {
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
                horizontalGroup {
                    it.padRight(PADDING_INNER_SCREEN).padBottom(PADDING_INNER_SCREEN)
                        .align(Align.bottomRight)
                    space(SPACE_BETWEEN_BUTTONS)
                    backButton = imageButton(ImageButtons.BACK.style) {
                        isTransform = true
                        setOrigin(Align.center)
                        onTouchDown {
                            this.clearActions()
                            this += Animations.pulse()
                            audioService.play(SoundAsset.BUTTON_POP)
                        }
                        onChange {
                            transitionOut<MapScreen>()
                        }
                    }
                }
            }

            popup = table {
                setFillParent(true)
                background = bgDim
                isVisible = false
            }
        }

        setPopup(PopupState.NONE)
    }

    override fun render(delta: Float) {
        super.render(delta)
        timer.update(delta)
    }

    private fun setPopup(state: PopupState) {
        if (state != PopupState.NONE) {
            layout.touchable = Touchable.disabled

            popup.clear()
            popup += Animations.appear(0.2f)
        } else {
            layout.touchable = Touchable.childrenOnly

            popup += Animations.disappear(0.2f) + Actions.run { popup.clear() }
        }

        when (state) {
            PopupState.FINISH -> {
                popup.add(
                    scene2d.stack {
                        image(Drawables.EFFECT_CONFETTI.drawable) {
                            setScaling(Scaling.none)
                            setOrigin(Align.top)
                            setScale(2f)
                            setPosition(0f, -300f)
                        }
                        image(Drawables.EFFECT_SALUTE.drawable) {
                            setScaling(Scaling.none)
                            setOrigin(Align.center)
                            setScale(2.5f)
                        }
                        levelFinishView(
                            currentScore,
                            currentStar,
                            currentRecordTime,
                            LevelFinishView.State.FINISH,
                            audioService,
                            gameEventManager,
                            newBestScore,
                            newBestTime
                        ) {
                            menuButton.onChangeEvent {
                                transitionOut<MapScreen>()
                            }
                            repeatButton.onChangeEvent {
                                loadLevel(currentLevel.number)
                                setPopup(PopupState.NONE)
                            }
                            nextButton.onChangeEvent {
                                nextLevel()
                                setPopup(PopupState.NONE)
                            }
                        }
                    }
                )
            }

            PopupState.FAILED -> {
                popup.add(
                    scene2d.levelFinishView(
                        currentScore,
                        currentStar,
                        currentRecordTime,
                        LevelFinishView.State.FAILED,
                        audioService,
                        gameEventManager
                    ) {
                        menuButton.onChangeEvent {
                            transitionOut<MapScreen>()
                        }
                        repeatButton.onChangeEvent {
                            loadLevel(currentLevel.number)
                            setPopup(PopupState.NONE)
                        }
                        nextButton.isVisible = false
                        nextButton.onChangeEvent {
                            nextLevel()
                            setPopup(PopupState.NONE)
                        }
                    })
            }

            PopupState.CORRECT -> {
                popup.add(scene2d.answerPopup(AnswerPopup.State.CORRECT, preferences) {
                    this.clearActions()
                    this += Animations.fadeInOut() + Actions.run {
                        setPopup(PopupState.NONE)
                        nextRound()
                    }
                    onClick {
                        this.clearActions()
                        setPopup(PopupState.NONE)
                        nextRound()
                        this.touchable = Touchable.disabled
                    }
                })
            }

            PopupState.INCORRECT -> {
                popup.add(scene2d.answerPopup(AnswerPopup.State.INCORRECT, preferences) {
                    this.clearActions()
                    this += Actions.sequence(Animations.fadeInOut(), Actions.run {
                        setPopup(PopupState.NONE)
                        loadStage(currentStage)
                    })
                    onClick {
                        this@answerPopup.clearActions()
                        setPopup(PopupState.NONE)
                        loadStage(currentStage)
                        this@answerPopup.touchable = Touchable.disabled
                    }
                })
            }

            else -> {}
        }
    }

    private fun loadLevel(levelNumber: Int) {
        layout.touchable = Touchable.childrenOnly

        val level = levels.find { it.number == levelNumber }
            ?: throw KotlinNullPointerException("Trying to load a level that does not exist")
        currentLevel = level

        if (level.isHealthCounted) {
            playerInfo.loadWidget(level.maxHealth)
            playerInfo.isVisible = true
        } else {
            playerInfo.isVisible = false
        }

        if (level.isTimed) {
            timer.loadWidget(level.timerSeconds)
            timer.start()
            timer.isVisible = true
        } else {
            timer.stop()
            timer.isVisible = false
        }

        levelTitle.setText(level.name.uppercase())
        backgroundImg.drawable =
            TextureRegionDrawable(assets[TextureAsset.entries[level.bgIndex].descriptor])
        audioService.play(MusicAsset.entries[level.musicIndex])

        loadStage(level.stages.first())
    }

    private fun loadStage(stage: StageModel) {
        currentStage = stage
        val newStage = scene2d {
            when (stage.type) {
                StageType.MCQ -> mcqStage(assets, audioService, gameEventManager)
                StageType.MCQ_JOIN -> mcqJoin(assets, audioService, gameEventManager)
                StageType.MCQ_VOICE -> mcqVoiceStage(assets, audioService, gameEventManager)
                StageType.DRAG_AND_DROP -> dragAndDropStage(assets, audioService, gameEventManager)
                StageType.MATCH_LINE -> matchLineStage(assets, audioService, batch, gameEventManager)
                StageType.DRAWING -> drawingStage(assets, audioService, batch, gameEventManager, game.recognition)
            }
        }
        newStage.setFillParent(true)
        stageBoard.clear()
        stageBoard.addActor(newStage)
    }

    private fun nextLevel() {
        val nextLevelNumber = currentLevel.number + 1

        val nextLevel = levels.find { it.number == nextLevelNumber }
        if (nextLevel != null) {
            log.debug { "Next level $nextLevelNumber" }
            preferences[PrefKey.CURRENT_LEVEL.key] = nextLevelNumber
            loadLevel(nextLevelNumber)
        } else {
            log.debug { "Adventure finished" }
            transitionOut<FinishScreen>()
        }
    }

    private fun nextStage() {
        currentStageIndex++
        if (currentStageIndex < currentLevel.stages.size) {
            log.debug { "Next stage $currentStageIndex" }
            currentStage = currentLevel.stages[currentStageIndex]
            loadStage(currentStage)
        } else {
            currentStageIndex = 0
            currentRound = 1
            levelComplete(timer.bar.levelScore, timer.bar.levelStars, timer.elapsedSeconds)
        }
    }

    private fun nextRound() {
        currentRound++
        if (currentRound <= currentStage.rounds) {
            log.debug { "Next round $currentRound" }
            loadStage(currentStage)
        } else {
            currentRound = 1
            nextStage()
        }
    }

    private fun isEndOfLevel(): Boolean =
        currentStageIndex + 1 >= currentLevel.stages.size && currentRound >= currentStage.rounds

    private fun updateLevelData(
        level: LevelModel,
        newScore: Float,
        newStars: Int,
        newRecordTime: Float
    ) {
        log.debug { "Updating level data of ${level.name}" }

        val levelsSavedData =
            preferences[PrefKey.LEVEL_SAVE_DATA.key, mutableListOf<LevelSavedData>()]
        val player = preferences[PrefKey.PLAYER.key, PlayerModel()]

        if (levelsSavedData.isNotEmpty() && level.isScored) {
            var levelData = levelsSavedData.find { it.number == level.number }

            if (levelData == null) {
                levelData = LevelSavedData()
                levelsSavedData.add(levelData)
            }

            with(levelData) {
                number = level.number
                recordTime = newRecordTime.takeIf { it <= recordTime }?.also { newBestTime = true } ?: recordTime.also { newBestTime = false }
                highScore = newScore.takeIf { it >= highScore }?.also { newBestScore = true } ?: highScore.also { newBestScore = false }
                starCount = maxOf(newStars, starCount)
                hasCompleted = true
            }

            player.totalScore = levelsSavedData.fold(0f) { sum, data -> sum + data.highScore }
            player.totalStar = levelsSavedData.fold(0) { sum, data -> sum + data.starCount }

            preferences.flush {
                this[PrefKey.LEVEL_SAVE_DATA.key] = levelsSavedData
                this[PrefKey.PLAYER.key] = player
            }
        }
        setPopup(PopupState.FINISH)
    }

    override fun answerCorrect(isContinue: Boolean) {
        log.debug { "Answer is correct, continue? $isContinue" }
        audioService.play(if (isContinue) SoundAsset.CORRECT_BLING else SoundAsset.CORRECT_DING)

        if (isEndOfLevel() && isContinue) {
            nextRound()
            return
        }
        if (isContinue) {
            setPopup(PopupState.CORRECT)
        }
    }

    override fun answerIncorrect(isContinue: Boolean) {
        log.debug { "Answer is wrong, continue? $isContinue" }
        audioService.play(if (isContinue) SoundAsset.INCORRECT_BIG else SoundAsset.INCORRECT)

        timer.loseSeconds(10f)
        if (playerInfo.loseHealth() <= 0) {
            levelFailed()
            return
        }
        if (isContinue) {
            setPopup(PopupState.INCORRECT)
        }
    }

    override fun levelComplete(score: Float, stars: Int, time: Float) {
        log.debug { "Level completed with score:$score, stars:$stars, time:$time" }
        currentScore = score
        currentStar = stars
        currentRecordTime = time

        timer.stop()
        audioService.play(SoundAsset.COMPLETE)
        audioService.play(SoundAsset.CHEER_SMALL)
        audioService.stopSound(SoundAsset.TIMER)
        updateLevelData(currentLevel, score, stars, time)
        layout.touchable = Touchable.disabled
    }

    override fun levelFailed() {
        log.debug { "Level failed" }
        currentScore = 0f
        currentStar = 0
        currentRound = 1
        currentStageIndex = 0
        currentRecordTime = timer.elapsedSeconds

        audioService.play(SoundAsset.FAILURE)
        audioService.stopSound(SoundAsset.TIMER)
        setPopup(PopupState.FAILED)
        layout.touchable = Touchable.disabled
    }

    override fun debugMode() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // refresh
            hide()
            show()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            // debug lines
            stage.isDebugAll = !stage.isDebugAll
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            // finish level
            levelComplete(timer.bar.levelScore, timer.bar.levelStars, timer.elapsedSeconds)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            // restart level
            loadLevel(currentLevel.number)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            // go to the next level
            nextLevel()
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            // load drag and drop level
            loadLevel(levels[6].number)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            // load join level
            loadLevel(levels[3].number)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            // load match line level
            loadLevel(levels[2].number)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            timer.remainingSeconds = 15f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            timer.remainingSeconds *= 0.30f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            timer.remainingSeconds *= 0.60f
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            timer.remainingSeconds *= 0.80f
        }
    }

    enum class PopupState {
        NONE,
        FINISH,
        FAILED,
        CORRECT,
        INCORRECT,
    }

    companion object {
        private val log = logger<LevelScreen>()
    }
}
