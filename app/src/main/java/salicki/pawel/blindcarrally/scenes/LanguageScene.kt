package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.LanguageSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LanguageLevelFlowEnum
import salicki.pawel.blindcarrally.enums.LanguageTtsEnum
import salicki.pawel.blindcarrally.gameresources.SelectBoxManager
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager

class LanguageScene(flow: LanguageLevelFlowEnum) : SurfaceView(Settings.CONTEXT), ILevel {

    private val languageLevelFlow = flow

    private var soundManager: SoundManager =
        SoundManager()
    private var languageSelectionData: LinkedHashMap<LanguageTtsEnum, LanguageSelectionData> =
        LinkedHashMap()
    private var languageTypeData: ArrayList<LanguageTtsEnum> =
        arrayListOf(LanguageTtsEnum.ENGLISH, LanguageTtsEnum.POLISH)

    private var languageIterator: Int = Settings.languageTtsEnum.ordinal
    private var lastOption: Int = Settings.languageTtsEnum.ordinal
    private var selectBoxManager =
        SelectBoxManager()

    private var swipe: Boolean = false

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

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
        languageSelectionData[LanguageTtsEnum.ENGLISH] =
            (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTtsEnum.ENGLISH)))
        languageSelectionData[LanguageTtsEnum.POLISH] =
            (LanguageSelectionData(OpenerCSV.readData(R.raw.language_tts, LanguageTtsEnum.POLISH)))
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    override fun initState() {
        languageSelectionData[Settings.languageTtsEnum]?.texts?.get("LANGUAGE_TUTORIAL")
            ?.let { TextToSpeechManager.speakQueue(it) }

        languageSelectionData[Settings.languageTtsEnum]?.texts?.get("SELECTED_LANGUAGE")
            ?.let { TextToSpeechManager.speakQueue(it) }
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when (GestureManager.swipeDetect(event)){
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                languageIterator++

                if (languageIterator >= languageTypeData.size) {
                    languageIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                languageIterator--
                if (languageIterator < 0) {
                    languageIterator = languageTypeData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)
                SharedPreferencesManager.saveConfiguration("language", languageIterator.toString())

                if(languageLevelFlow == LanguageLevelFlowEnum.INTRODUCTION && Settings.introduction){
                    LevelManager.changeLevel(InformationScene())
                }
                else {
                    LevelManager.changeLevel(MenuScene())
                }
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

            idleTimeSeconds = 0
        }

        selectBoxManager.updateSelectBoxPosition(languageIterator)
    }

    override fun updateState() {
        if (lastOption != languageIterator) {
            TextToSpeechManager.stop()
            Settings.languageTtsEnum = languageTypeData[languageIterator]
            TextToSpeechManager.changeLanguage(languageTypeData[languageIterator])
            languageSelectionData[languageTypeData[languageIterator]]?.texts?.get("SELECTED_LANGUAGE")
                ?.let { TextToSpeechManager.speakNow(it) }

            lastOption = languageIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            languageSelectionData[Settings.languageTtsEnum]?.texts?.get("LANGUAGE_TUTORIAL")
                ?.let { TextToSpeechManager.speakNow(it) }

            idleTimeSeconds = 0
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