package salicki.pawel.blindcarrally

import android.content.Context

object Settings {
    var SCREEN_WIDTH : Int = 0
    var SCREEN_HEIGHT : Int = 0

    var CONTEXT : Context? = null

    var languageTTS: LanguageTTS = LanguageTTS.ENGLISH

    var vibrations: Boolean = true
    var display: Boolean = true
    var lector: Float = 1F
    var sounds: Float = 1F
}