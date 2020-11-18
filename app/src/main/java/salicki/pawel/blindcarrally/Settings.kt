package salicki.pawel.blindcarrally

import android.content.Context

object Settings {
    var SCREEN_WIDTH: Int = 0
    var SCREEN_HEIGHT: Int = 0
    var SCREEN_SCALE: Float = 0F

    var CONTEXT: Context? = null
    var rotation: Int = -1

    var languageTTS: LanguageTTS = LanguageTTS.ENGLISH

    var vibrations: Boolean = true
    var display: Boolean = true
    var introduction: Boolean = true
    var reader: Int = 10
    var sounds: Int = 10

    var globalSounds = SoundManager()

    init{
        globalSounds.initSoundManager()
    }
}