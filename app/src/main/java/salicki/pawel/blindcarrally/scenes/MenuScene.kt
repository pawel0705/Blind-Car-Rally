package salicki.pawel.blindcarrally.scenes

import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LanguageLevelFlowEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.SelectBoxManager
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class MenuScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var screenTexts:HashMap<String, String> = HashMap()
    private var soundManager: SoundManager =
        SoundManager()
    private var menuSelectionData = arrayListOf<OptionSelectionData>()
    private var menuIterator: Int = 0
    private var menuImage =
        OptionImage()
    private var selectBoxManager: SelectBoxManager =
        SelectBoxManager()

    private var swipe: Boolean = false

    private var optionText =
        TextObject()

    private var lastOption = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        initMenuOptions()
        readTTSTextFile()
        initSelectBoxModel()
        initTextOption()
        initMenuScreen()
    }

    private fun initMenuScreen() {
        menuImage.setFullScreenImage(R.drawable.main_menu)
    }

    private fun initTextOption(){
         optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initSelectBoxModel(){
        selectBoxManager.initSelectBoxModel(5)
    }


    private fun initMenuOptions() {
        menuSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.CALIBRATION,
                "MENU_PLAY",
                "PLAY",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.SETTINGS,
                "MENU_SETTINGS",
                "SETTINGS",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.LANGUAGE,
                "MENU_LANGUAGE",
                "LANGUAGE",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.CREDITS,
                "MENU_CREDITS",
                "AUTHORS",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.QUIT,
                "MENU_QUIT",
                "EXIT",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.menu_tts, Settings.languageTtsEnum))
        screenTexts.putAll(OpenerCSV.readData(R.raw.menu_texts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["MENU_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["MENU_PLAY"].toString())
    }

    override fun updateState() {
        if (menuSelectionData[menuIterator].selected && lastOption != menuIterator) {
            texts[menuSelectionData[menuIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = menuIterator
        }

        selectBoxManager.updateSelectBoxPosition(menuIterator)

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(texts["MENU_TUTORIAL"].toString())

            idleTimeSeconds = 0
        }

    }

    override fun destroyState() {
        isFocusable = false

        this.soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                menuIterator++

                if (menuIterator >= menuSelectionData.size) {
                    menuIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                menuIterator--
                if (menuIterator < 0) {
                    menuIterator = menuSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when(GestureManager.doubleTapDetect(event))
        {
            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(menuIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 5 -> {
                    menuIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 2 -> {
                    menuIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 3 -> {
                    menuIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 4 -> {
                    menuIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 5 -> {
                    menuIterator = 4
                }
            }

            idleTimeSeconds = 0
        }

        menuSelectionData.forEach {
            it.selected = false
        }

        menuSelectionData[menuIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (menuSelectionData[option].levelType) {
            LevelTypeEnum.CALIBRATION -> {
                LevelManager.changeLevel(GameModeScene())
            }
            LevelTypeEnum.SETTINGS -> {
                LevelManager.changeLevel(SettingsScene())
            }
            LevelTypeEnum.LANGUAGE -> {
                LevelManager.changeLevel(LanguageScene(LanguageLevelFlowEnum.MENU))
            }
            LevelTypeEnum.CREDITS -> {
                LevelManager.changeLevel(CreditsScene())
            }
            LevelTypeEnum.QUIT -> {
                LevelManager.stackLevel(QuitScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        menuImage.drawImage(canvas)

        screenTexts[menuSelectionData[menuIterator].textValue]?.let {
            optionText.drawText(canvas,
                it
            )
        }
    }
}