package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.LanguageSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LanguageLevelFlowEnum
import salicki.pawel.blindcarrally.enums.LanguageTtsEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.*
import java.security.cert.PKIXRevocationChecker

class LanguageScene(flow: LanguageLevelFlowEnum) : SurfaceView(Settings.CONTEXT), ILevel {
    private var screenTexts: HashMap<String, String> = HashMap()
    private var languageSelectionData: LinkedHashMap<LanguageTtsEnum, LanguageSelectionData> =
        LinkedHashMap()
    private var languageTypeData: ArrayList<LanguageTtsEnum> =
        arrayListOf(LanguageTtsEnum.ENGLISH, LanguageTtsEnum.POLISH)

    private val languageLevelFlow = flow
    private var languageImage: OptionImage = OptionImage()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()

    private var languageIterator: Int = Settings.languageTtsEnum.ordinal
    private var lastOption: Int = Settings.languageTtsEnum.ordinal

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()

        languageImage.setFullScreenImage(DrawableResources.languageView)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }


    private fun readTTSTextFile() {
        languageSelectionData[LanguageTtsEnum.ENGLISH] =
            (LanguageSelectionData(OpenerCSV.readData(RawResources.language_TTS, LanguageTtsEnum.ENGLISH)))
        languageSelectionData[LanguageTtsEnum.POLISH] =
            (LanguageSelectionData(OpenerCSV.readData(RawResources.language_TTS, LanguageTtsEnum.POLISH)))

        screenTexts.putAll(OpenerCSV.readData(RawResources.language_TXT, Settings.languageTtsEnum))
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

        languageSelectionData[Settings.languageTtsEnum]?.texts?.get("IDLE")?.let {
            idleSpeak.initIdleString(
                it
            )
        }
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
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                languageIterator--
                if (languageIterator < 0) {
                    languageIterator = languageTypeData.size - 1
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(MenuScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN->{
                languageSelectionData[Settings.languageTtsEnum]?.texts?.get("IDLE")
                    ?.let { TextToSpeechManager.speakNow(it) }
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
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

            idleSpeak.resetIdleTimeSeconds()
        }
    }

    override fun updateState() {
        if (lastOption != languageIterator) {
            TextToSpeechManager.stop()
            Settings.languageTtsEnum = languageTypeData[languageIterator]
            TextToSpeechManager.changeLanguage(languageTypeData[languageIterator])
            languageSelectionData[languageTypeData[languageIterator]]?.texts?.get("SELECTED_LANGUAGE")
                ?.let { TextToSpeechManager.speakNow(it) }

            lastOption = languageIterator

            languageSelectionData[Settings.languageTtsEnum]?.texts?.get("IDLE")?.let {
                idleSpeak.initIdleString(
                    it
                )
            }
        }

        idleSpeak.updateIdleStatus()
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun redrawState(canvas: Canvas) {
        languageImage.drawImage(canvas)

        if(Settings.languageTtsEnum == LanguageTtsEnum.ENGLISH)
        {
            screenTexts["ENGLISH"]?.let { optionText.drawText(canvas, it) }
        } else {
            screenTexts["POLISH"]?.let { optionText.drawText(canvas, it) }
        }
    }
}