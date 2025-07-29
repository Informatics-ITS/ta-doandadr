package com.github.doandadr.petualanganhijaiyah.ui.widget

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.hide
import com.badlogic.gdx.scenes.scene2d.actions.Actions.show
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.github.doandadr.petualanganhijaiyah.asset.Labels
import com.github.doandadr.petualanganhijaiyah.asset.SoundAsset
import com.github.doandadr.petualanganhijaiyah.audio.AudioService
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import com.github.doandadr.petualanganhijaiyah.ui.values.SCALE_FONT_SMALL
import com.github.doandadr.petualanganhijaiyah.ui.widget.Tutorial.Orientation
import com.kotcrab.vis.ui.layout.FloatingGroup
import ktx.actors.onClick
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.math.plus
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.KGroup
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import java.util.LinkedList
import java.util.Queue

enum class TutorialType(
    val pages: List<String>,
) {
    HOME_NAME(listOf("Perkenalkan diri yuk!")),
    HOME_PRACTICE(listOf("Belajar huruf hijaiyah disini!")),
    HOME_START(listOf("Mulai petualanganmu!")),

    NAME_CHANGE(listOf("Ketikkan namamu disini!")),

    MAP_LEVEL1(listOf("Klik Level 1 untuk\nmemulai permainan!")),
    MAP_LEVEL9(listOf("Semangat! sebentar lagi\nkamu dapat menyelesaikan\npetualangan")),

    PRACTICE_NEXT(listOf("Lanjut")),
    PRACTICE_PREVIOUS(listOf("Kembali")),

    MATCH_START(listOf("Tarik garis dari\n kotak hijaiyah,")),
    MATCH_END(listOf("Lalu arahkan\nsesuai ejaannya!")),

    DRAW_START(
        listOf(
            "Tulislah huruf hijaiyah\ndi dalam kotak putih,",
            "sesuai dengan\nyang tertulis\ndi kotak hijau."
        )
    ),
    VOICE_START(listOf("Klik disini untuk\nmendengarkan bunyi\nhuruf hijaiyah nya")),
    VOICE_OPTION(
        listOf(
            "Lalu, pilihlah salah\nsatu huruf hijaiyah\nyang sesuai dengan\nbunyi yang kamu dengar!"
        )
    ),
    DRAG_DROP_START(listOf("Pilih huruf hijaiyah")),
    DRAG_DROP_END(listOf("Lalu geret ke\ncetakan yang sesuai!")),

    JOIN_START(listOf("Perhatikan huruf hijaiyah\ntersambung berikut!")),
    JOIN_OPTION(listOf("Pilih salah satu\nyang merupakan\nhuruf-huruf bagiannya\njika terpisah!")),

    DRAW_CLEAR(listOf("Klik tombol ini\nuntuk menghapus tulisan!")),
    DRAW_SKIP(listOf("Klik tombol ini\nuntuk ganti huruf!")),
    DRAW_ANSWER(listOf("Setelah menulis,\njawab di sini!")),

    PRACTICE_VOICE(listOf("Klik untuk\nmendengarkan suara!")),

    LEVEL_FINISH_MENU(listOf("Kembali ke peta")),
    LEVEL_FINISH_REPEAT(listOf("Ulangi level")),
    LEVEL_FINISH_NEXT(listOf("Ke level selanjutnya")),
}

class Tutorial(
    val type: TutorialType,
    actor: Actor,
    stage: Stage
) {
    private var targetPos: Vector2
    private var targetSize: Vector2
    private var stageSize: Vector2

    lateinit var position: Vector2
    lateinit var offset: Vector2
    lateinit var orientation: Orientation

    init {
        targetPos = actor.localToStageCoordinates(Vector2())
        targetSize = Vector2(actor.width, actor.height)
        stageSize = Vector2(stage.width, stage.height)
        log.debug { "targetPos:$targetPos ,targetSize:$targetSize ,stageSize:$stageSize" }

        calculate()
    }

    private fun calculate() {
        orientation = calculateOrientation(targetPos, stageSize)
        offset = calculateOffset(orientation, targetSize)
        position = calculatePosition(offset, targetPos)
        log.debug { "Calculated orientation:$orientation, offset:$offset, position:$position" }
    }

    private fun calculateOrientation(actorPos: Vector2, stageSize: Vector2): Orientation {
        return when {
            actorPos.x < stageSize.x / 2 && actorPos.y < stageSize.y / 2 -> Orientation.LEFT_DOWN
            actorPos.x < stageSize.x / 2 && actorPos.y >= stageSize.y / 2 -> Orientation.LEFT_UP
            actorPos.x >= stageSize.x / 2 && actorPos.y < stageSize.y / 2 -> Orientation.RIGHT_DOWN
            else -> Orientation.RIGHT_UP
        }
    }

    private fun calculateOffset(orientation: Orientation, actorSize: Vector2): Vector2 {
        return when (orientation) {
            Orientation.LEFT_DOWN -> Vector2(0f, actorSize.y)
            Orientation.LEFT_UP -> Vector2(0f, -actorSize.y)
            Orientation.RIGHT_DOWN -> Vector2(actorSize.x, actorSize.y)
            Orientation.RIGHT_UP -> Vector2(actorSize.x, -actorSize.y)
        }
    }

    private fun calculatePosition(offset: Vector2, targetPos: Vector2): Vector2 {
        return targetPos + offset
    }

    enum class Orientation(
        val style: String,
    ) {
        LEFT_UP(Labels.TUTORIAL_LEFT_UP.style),
        LEFT_DOWN(Labels.TUTORIAL_LEFT_DOWN.style),
        RIGHT_UP(Labels.TUTORIAL_RIGHT_UP.style),
        RIGHT_DOWN(Labels.TUTORIAL_RIGHT_DOWN.style)
    }

    companion object {
        private val log = logger<Tutorial>()
    }
}

class TutorialWidget(
    private val preferences: Preferences,
    private val audioService: AudioService,
    private val skin: Skin = Scene2DSkin.defaultSkin,
) : FloatingGroup(), KGroup {
    private var tutorialQueue: Queue<Tutorial> = LinkedList()
    private lateinit var tutorial: Tutorial
    private var currentPageIndex = 0
    private var isStart = false

    private var tutorialLabel: Label = label("") {
        setAlignment(Align.center)
        setFontScale(SCALE_FONT_SMALL)
    }

    init {
        setFillParent(true)
        isVisible = false
        touchable = Touchable.enabled
        onClick { nextLine() }
    }

    private fun loadTutorial() {
        val tutorialText = tutorial.type.pages[currentPageIndex]
        tutorialLabel.setText(tutorialText)
        log.debug { "Showing tutorial $tutorialText" }
        tutorialLabel.pack()

        tutorialLabel.style = skin.get(tutorial.orientation.style, Label.LabelStyle::class.java)

        tutorialLabel.pack()
        var coords = tutorial.position
        if (tutorial.orientation in listOf(Orientation.RIGHT_UP, Orientation.RIGHT_DOWN))
            coords += Vector2(-tutorialLabel.width, 0f)
        tutorialLabel.pack()

        tutorialLabel.setPosition(coords.x, coords.y)
        log.debug { "Label position(${coords.x}, ${coords.y}), label size(${tutorialLabel.width}, ${tutorialLabel.height})" }
        tutorialLabel.pack()
    }

    private fun nextLine() {
        if (isStart) {
            isStart = false
            tutorialQueue.poll()
        }
        if (tutorial.type.pages.getOrNull(currentPageIndex + 1) != null) {
            currentPageIndex++
            loadTutorial()
        } else {
            currentPageIndex = 0
            setTutorialFromQueue()
        }
    }

    private fun hideTutorial(fadeTime: Float = 0.2f) {
        this.clearActions()
        this += fadeOut(fadeTime) + hide()
        audioService.play(SoundAsset.MIN_LOW)
    }

    private fun showTutorial(delayTime: Float = 0.2f) {
        if (tutorialQueue.peek() != null) {
            this.clearActions()
            this += delay(delayTime) + show() + fadeIn(0.2f)
            audioService.play(SoundAsset.MAX_LOW)
            loadTutorial()
        }
    }

    fun addTutorial(actor: Actor, type: TutorialType, stage: Stage) {
        registerTutorial(type, actor, stage)
        isStart = true
        showTutorial()
    }

    private fun registerTutorial(type: TutorialType, actor: Actor, stage: Stage) {
        val player = preferences[PrefKey.PLAYER.key, PlayerModel()]
        if (player.tutorials.add(type.ordinal)) {
            tutorialQueue.add(Tutorial(type, actor, stage))
            tutorial = tutorialQueue.peek()
            currentPageIndex = 0
            preferences.flush {
                this[PrefKey.PLAYER.key] = player
            }
        }
    }

    private fun setTutorialFromQueue() {
        val entry = tutorialQueue.poll()
        if (entry != null) {
            tutorial = entry
            loadTutorial()
        } else {
            hideTutorial()
        }
    }

    companion object {
        private val log = logger<TutorialWidget>()
    }
}
