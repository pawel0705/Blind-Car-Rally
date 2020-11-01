package salicki.pawel.blindcarrally.scene

import android.gesture.Gesture
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.LanguageSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class LanguageLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var soundManager: SoundManager = SoundManager()
    private var languageSelectionData: LinkedHashMap<LanguageTTS, LanguageSelectionData> =
        LinkedHashMap()
    private var languageTypeData: ArrayList<LanguageTTS> =
        arrayListOf(LanguageTTS.ENGLISH, LanguageTTS.POLISH)

    private var languageIterator: Int = 0

    private var selectBoxManager = SelectBoxManager()

    private var swipe: Boolean = false
    private var changeLanguage: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initSelectBoxModel()
    }

    private fun initSelectBoxModel(){
        selectBoxManager.initSelectBoxModel(2)
    }

    private fun readTTSTextFile() {
        languageSelectionData[LanguageTTS.ENGLISH] =
            (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTTS.ENGLISH)))
        languageSelectionData[LanguageTTS.POLISH] =
            (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTTS.POLISH)))
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    override fun initState() {
        languageSelectionData[Settings.languageTTS]?.texts?.get("LANGUAGE_TUTORIAL")
            ?.let { TextToSpeechManager.speakQueue(it) }

        languageSelectionData[Settings.languageTTS]?.texts?.get("SELECTED_LANGUAGE")
            ?.let { TextToSpeechManager.speakQueue(it) }
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when (GestureManager.swipeDetect(event)){
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                languageIterator++

                if (languageIterator >= languageTypeData.size) {
                    languageIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                languageIterator--
                if (languageIterator < 0) {
                    languageIterator = languageTypeData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
                SharedPreferencesManager.saveConfiguration("language", languageIterator.toString())
                LevelManager.changeLevel(MenuLevel())
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                    languageIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                    languageIterator = 1
                }
            }
        }

        selectBoxManager.updateSelectBoxPosition(languageIterator)
    }

    override fun updateState(deltaTime: Int) {
        if (swipe) {
            TextToSpeechManager.stop()
            Settings.languageTTS = languageTypeData[languageIterator]
            TextToSpeechManager.changeLanguage(languageTypeData[languageIterator])
            languageSelectionData[languageTypeData[languageIterator]]?.texts?.get("SELECTED_LANGUAGE")
                ?.let { TextToSpeechManager.speakNow(it) }

            swipe = false
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun redrawState(canvas: Canvas) {
        selectBoxManager.drawSelectBox(canvas)
    }
}