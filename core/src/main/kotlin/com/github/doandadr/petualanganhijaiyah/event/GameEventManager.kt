package com.github.doandadr.petualanganhijaiyah.event

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.screen.HomeScreen
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType
import ktx.app.KtxInputAdapter

class GameEventManager : KtxInputAdapter {
    // Game event related
    private val gameEventListeners = Array<GameEventListener>()

    fun addGameEventListener(listener: GameEventListener) = gameEventListeners.add(listener)

    fun removeGameEventListener(listener: GameEventListener) = gameEventListeners.removeValue(listener, true)

    fun dispatchAnswerCorrectEvent(isContinue: Boolean) =
        gameEventListeners.forEach { it.answerCorrect(isContinue) }

    fun dispatchAnswerIncorrectEvent(isContinue: Boolean) =
        gameEventListeners.forEach { it.answerIncorrect(isContinue) }

    fun dispatchLevelFailEvent() =
        gameEventListeners.forEach { it.levelFailed() }

    fun dispatchLevelCompleteEvent(score: Float, stars: Int, time: Float) =
        gameEventListeners.forEach { it.levelComplete(score, stars, time) }

    fun dispatchSetHomePopupStateEvent(state: HomeScreen.PopupState) =
        gameEventListeners.forEach { it.setHomePopupState(state) }

    fun dispatchPlayerChangedEvent(player: PlayerModel) =
        gameEventListeners.forEach { it.playerChanged(player) }

    fun dispatchShowTutorialEvent(actor: Actor, type: TutorialType) {
        gameEventListeners.forEach { it.showTutorial(actor, type) }
    }
}
