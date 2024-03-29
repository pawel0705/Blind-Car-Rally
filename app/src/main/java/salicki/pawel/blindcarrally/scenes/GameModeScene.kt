package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.text.TextPaint
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.enums.RacingModeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class GameModeScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var modeSelectionData = arrayListOf<OptionSelectionData>()

    private var descriptionText: TextObject = TextObject()
    private var optionText: TextObject = TextObject()
    private var gameModeImage: OptionImage = OptionImage()
    private var soundManager: SoundManager =
        SoundManager()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()

    private var swipe: Boolean = false
    private var drawDescription: Boolean = false

    private var lastOption: Int = -1
    private var modeIterator: Int = 0

    init {
        isFocusable = true

        readTTSTextFile()
        initModeOptions()
        initSoundManager()

        screenTexts.putAll(OpenerCSV.readData(RawResources.gamemode_TXT, Settings.languageTtsEnum))
        gameModeImage.setFullScreenImage(DrawableResources.selectModeView)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
        screenTexts["MODE_DESCRIPTION_CLICKED"]?.let {
            descriptionText.initMultiLineText(
                R.font.montserrat,
                R.dimen.informationSizeSmall,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 20F,
                it
            )
        }
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun initModeOptions() {
        modeSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.SINGLE,
                "MODE_SINGLE",
                "MODE_SINGLE",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.TOURNAMENT,
                "MODE_TOURNAMENT",
                "MODE_TOURNAMENT",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MODE_DESCRIPTION,
                "MODE_DESCRIPTION",
                "MODE_DESCRIPTION",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "MODE_MENU",
                "MODE_MENU",
                false
            )
        )
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(RawResources.gamemode_TTS, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["MODE_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["MODE_SINGLE"].toString())

        idleSpeak.initIdleString(texts["IDLE"].toString())
    }

    override fun updateState() {
        if (modeSelectionData[modeIterator].selected && lastOption != modeIterator) {
            texts[modeSelectionData[modeIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            drawDescription = false
            lastOption = modeIterator
        }

        idleSpeak.updateIdleStatus()
    }

    override fun destroyState() {
        soundManager.destroy()

        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                modeIterator++

                if (modeIterator >= modeSelectionData.size) {
                    modeIterator = 0
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                modeIterator--
                if (modeIterator < 0) {
                    modeIterator = modeSelectionData.size - 1
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(MenuScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(texts["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(modeIterator)

                idleSpeak.resetIdleTimeSeconds()
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 4 -> {
                    modeIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 2 -> {
                    modeIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 3 -> {
                    modeIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 4 -> {
                    modeIterator = 3
                }
            }

            idleSpeak.resetIdleTimeSeconds()
        }

        modeSelectionData.forEach {
            it.selected = false
        }

        modeSelectionData[modeIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (modeSelectionData[option].levelType) {
            LevelTypeEnum.SINGLE -> {

                GameOptions.gamemode = RacingModeEnum.SINGLE_RACE
                LevelManager.changeLevel(TrackSelectionScene())
            }
            LevelTypeEnum.TOURNAMENT -> {
                GameOptions.gamemode = RacingModeEnum.TOURNAMENT_MODE

                LevelManager.changeLevel(TournamentScene())
            }
            LevelTypeEnum.MODE_DESCRIPTION -> {
                TextToSpeechManager.speakNow(texts["MODE_DESCRIPTION_CLICKED"].toString())
                drawDescription = true
            }
            LevelTypeEnum.MENU -> {
                LevelManager.changeLevel(MenuScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        if (!drawDescription) {
            gameModeImage.drawImage(canvas)
            screenTexts[modeSelectionData[modeIterator].textValue]?.let {
                optionText.drawText(
                    canvas,
                    it
                )
            }
        } else {
            descriptionText.drawMultilineText(canvas)
        }
    }
}