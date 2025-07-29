package com.github.doandadr.petualanganhijaiyah.screen

import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.data.PlayerModel
import com.github.doandadr.petualanganhijaiyah.data.PrefKey
import ktx.log.logger
import ktx.preferences.get

class StartScreen(game: Main): BaseScreen(game) {
    // properties
    private lateinit var  player: PlayerModel

    // show
    override fun show() {
        super.show()

        setupData()
        setupUI()
        transitionIn()
    }

    private fun setupData() {
        player = preferences[PrefKey.PLAYER.key, PlayerModel()]
    }

    private fun setupUI() {

    }

    // data [player name]
    // ui [start bg, popup bubble, etc]
    // render

    companion object {
        private val log = logger<StartScreen>()
    }
}
