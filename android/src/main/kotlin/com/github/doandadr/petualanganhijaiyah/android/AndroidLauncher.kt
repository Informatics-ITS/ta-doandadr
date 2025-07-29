package com.github.doandadr.petualanganhijaiyah.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.github.doandadr.petualanganhijaiyah.Main
import com.github.doandadr.petualanganhijaiyah.android.ml.AndroidRecognition
import com.github.doandadr.petualanganhijaiyah.ml.Model

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = Model.newInstance(applicationContext)
        val recognition = AndroidRecognition(model)

        initialize(Main(AndroidRecognition(model)), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
