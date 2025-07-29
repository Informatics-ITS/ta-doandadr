package com.github.doandadr.petualanganhijaiyah.event

import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.screen.HomeScreen
import com.github.doandadr.petualanganhijaiyah.ui.widget.TutorialType

interface GameEventListener {
    fun answerCorrect(isContinue: Boolean) {}
    fun answerIncorrect(isContinue: Boolean){}
    fun levelComplete(score: Float, stars: Int, time: Float) {}
    fun levelFailed() {}
    fun setHomePopupState(state: HomeScreen.PopupState) {}
    fun playerChanged(player: PlayerModel) {}
    fun showTutorial(actor: Actor, type: TutorialType)
}
