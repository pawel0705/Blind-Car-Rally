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

    // swipe
    private var x1 = 0f
    private var x2 = 0f
    private val MIN_DISTANCE = 150

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 200

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
        var swap = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX = x2 - x1
                if (abs(deltaX) > MIN_DISTANCE) {
                    swap = true

                    SoundManager.playSound(R.raw.swoosh)

                    // Left to Right swipe action
                    if (x2 > x1) {
                        languageIterator++

                        if(languageIterator >= languageTypeData.size){
                            languageIterator = 0
                        }
                    } else {
                        languageIterator--
                        if(languageIterator < 0) {
                            languageIterator = languageTypeData.size - 1
                        }

                    }
                    //TextToSpeechManager.setLanguage(languageTypeData[languageIterator])
                    TextToSpeechManager.stop()
                    Settings.languageTTS = languageTypeData[languageIterator]
                    languageSelectionData[languageTypeData[languageIterator]]?.texts?.get("SELECTED_LANGUAGE")
                        ?.let { TextToSpeechManager.speakNow(it) }

                    duration = 0
                } else {
                    swap = false
                }
            }
        }

        if(!swap){
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    startTime = System.currentTimeMillis()
                    clickCount++
                }
                MotionEvent.ACTION_UP -> {
                    val time: Long = System.currentTimeMillis() - startTime
                    duration += time

                    Log.d("CZAS", duration.toString())

                    if (clickCount >= 2) {
                        if (duration <= MAX_DURATION) {
                            SoundManager.playSound(R.raw.accept)
                            LevelManager.changeLevel(LevelType.MENU)
                        }
                        clickCount = 0
                        duration = 0
                    }
                }
            }
        } else {
            clickCount = 0
            duration = 0
        }
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun redrawState(canvas: Canvas) {

    }
}