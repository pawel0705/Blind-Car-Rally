package salicki.pawel.blindcarrally.scenes

import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import kotlin.math.abs

class LanguageLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var languageSelectionData : LinkedHashMap<LanguageTTS, LanguageSelectionData> = LinkedHashMap()
    private var languageTypeData = arrayListOf<LanguageTTS>(LanguageTTS.ENGLISH, LanguageTTS.POLISH)
    private var languageIterator: Int = 0

    private var swipe : Boolean = false

    init {
        isFocusable = true

        languageSelectionData[LanguageTTS.ENGLISH] = (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTTS.ENGLISH)))
        languageSelectionData[LanguageTTS.POLISH] = (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTTS.POLISH)))
    }

    override fun initState() {
        languageSelectionData[Settings.languageTTS]?.texts?.get("LANGUAGE_TUTORIAL")?.let {
            TextToSpeechManager.speakQueue(
                it
            )
        }
        languageSelectionData[Settings.languageTTS]?.texts?.get("SELECTED_LANGUAGE")
            ?.let { TextToSpeechManager.speakQueue(it) }
    }

    override fun respondTouchState(event: MotionEvent) {

        when(GestureManager.gestureDetect(event)){
            GestureType.SWIPE_LEFT->{
                SoundManager.playSound(R.raw.swoosh)
                languageIterator++

                if(languageIterator >= languageTypeData.size){
                    languageIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_RIGHT->{
                SoundManager.playSound(R.raw.swoosh)
                languageIterator--
                if(languageIterator < 0) {
                    languageIterator = languageTypeData.size - 1
                }

                swipe = true
            }
            GestureType.DOUBLE_TAP->{
                SoundManager.playSound(R.raw.accept)
                LevelManager.changeLevel(LevelType.MENU)
            }
        }
    }

    override fun updateState() {
        if(swipe){
            TextToSpeechManager.stop()
            Settings.languageTTS = languageTypeData[languageIterator]
            languageSelectionData[languageTypeData[languageIterator]]?.texts?.get("SELECTED_LANGUAGE")
                ?.let { TextToSpeechManager.speakNow(it) }

            swipe = false
        }
    }

    override fun destroyState() {

    }

    override fun redrawState(canvas: Canvas) {

    }
}