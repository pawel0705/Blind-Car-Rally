package salicki.pawel.blindcarrally.information

import android.content.Context
import salicki.pawel.blindcarrally.enums.LanguageTtsEnum
import salicki.pawel.blindcarrally.utils.SoundManager

object Settings {
    var SCREEN_WIDTH: Int = 0
    var SCREEN_HEIGHT: Int = 0
    var SCREEN_SCALE: Float = 0.0F

    var CONTEXT: Context? = null

    var languageTtsEnum: LanguageTtsEnum = LanguageTtsEnum.ENGLISH

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