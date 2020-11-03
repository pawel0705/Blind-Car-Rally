package salicki.pawel.blindcarrally.scene

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class MenuLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()
    private var menuSelectionData = arrayListOf<OptionSelectionData>()
    private var menuIterator: Int = 0

    private var selectBoxManager: SelectBoxManager = SelectBoxManager()

    private var swipe: Boolean = false

    private var startImage = OptionImage()
    private var settingsImage = OptionImage()
    private var languageImage = OptionImage()
    private var authorsImage = OptionImage()
    private var exitImage = OptionImage()

    private var optionText = TextObject()

    private var lastOption = -1

    init {
        isFocusable = true

        initSoundManager()
        initMenuOptions()
        readTTSTextFile()
        initSelectBoxModel()
        initOptionImages()
        initTextOption()
    }

    private fun initTextOption(){
         optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initOptionImages(){
        startImage.setImage(R.drawable.start, Settings.SCREEN_WIDTH / 10, (Settings.SCREEN_HEIGHT / 1.5F).toInt(), R.dimen.optionSize)
        settingsImage.setImage(R.drawable.options, (Settings.SCREEN_WIDTH / 3.3F).toInt(), (Settings.SCREEN_HEIGHT / 1.5F).toInt(),R.dimen.optionSize)
        languageImage.setImage(R.drawable.language, Settings.SCREEN_WIDTH / 2, (Settings.SCREEN_HEIGHT / 1.5F).toInt(),R.dimen.optionSize)
        authorsImage.setImage(R.drawable.author, (Settings.SCREEN_WIDTH / 1.425F).toInt(), (Settings.SCREEN_HEIGHT / 1.5F).toInt(),R.dimen.optionSize)
        exitImage.setImage(R.drawable.exit, (Settings.SCREEN_WIDTH / 1.1F).toInt(), (Settings.SCREEN_HEIGHT / 1.5F).toInt(),R.dimen.optionSize)
    }

    private fun initSelectBoxModel(){
        selectBoxManager.initSelectBoxModel(5)
    }


    private fun initMenuOptions() {
        menuSelectionData.add(
            OptionSelectionData(
                LevelType.CALIBRATION,
                "MENU_PLAY",
                "Rozpocznij grę",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelType.SETTINGS,
                "MENU_SETTINGS",
                "Ustawienia",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelType.LANGUAGE,
                "MENU_LANGUAGE",
                "Zmień język",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelType.CREDITS,
                "MENU_CREDITS",
                "Autorzy",
                false
            )
        )
        menuSelectionData.add(
            OptionSelectionData(
                LevelType.QUIT,
                "MENU_QUIT",
                "Wyjdź z gry",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.menu_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["MENU_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["MENU_PLAY"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (menuSelectionData[menuIterator].selected && lastOption != menuIterator) {
            texts[menuSelectionData[menuIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = menuIterator
        }

        selectBoxManager.updateSelectBoxPosition(menuIterator)
    }

    override fun destroyState() {
        isFocusable = false

        this.soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                menuIterator++

                if (menuIterator >= menuSelectionData.size) {
                    menuIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                menuIterator--
                if (menuIterator < 0) {
                    menuIterator = menuSelectionData.size - 1
                }

                swipe = true
            }
        }

        when(GestureManager.doubleTapDetect(event))
        {
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
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
        }

        menuSelectionData.forEach {
            it.selected = false
        }

        menuSelectionData[menuIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (menuSelectionData[option].levelType) {
            LevelType.CALIBRATION -> {
                LevelManager.changeLevel(GameModeLevel())
            }
            LevelType.SETTINGS -> {
                LevelManager.changeLevel(SettingsLevel())
            }
            LevelType.LANGUAGE -> {
                LevelManager.changeLevel(LanguageLevel())
            }
            LevelType.CREDITS -> {
                LevelManager.changeLevel(CreditsLevel())
            }
            LevelType.QUIT -> {
                LevelManager.stackLevel(QuitLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        selectBoxManager.drawSelectBox(canvas)


        startImage.drawImage(canvas)
        settingsImage.drawImage(canvas)
        languageImage.drawImage(canvas)
        authorsImage.drawImage(canvas)
        exitImage.drawImage(canvas)

        optionText.drawText(canvas, menuSelectionData[menuIterator].textValue)
    }
}