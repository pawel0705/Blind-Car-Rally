package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.AnswerEnum
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class ReturnMenuScene: SurfaceView(Settings.CONTEXT), ILevel {
    private var screenTexts: HashMap<String, String> = HashMap()
    private var textsReturnMenu: HashMap<String, String> = HashMap()
    private var returnMenuSelectionData= arrayListOf<OptionSelectionData>()

    private var soundManager: SoundManager =
        SoundManager()
    private var returnMenuImage: OptionImage = OptionImage()
    private var optionText: TextObject = TextObject()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()

    private var returnMenuIterator: Int = 0
    private var lastOption: Int = -1

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()
        returnMenuImage.setFullScreenImage(DrawableResources.yesNoView)
        screenTexts.putAll(OpenerCSV.readData(RawResources.returnMenu_TXT, Settings.languageTtsEnum))
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initTrackSelectionOptions() {
        returnMenuSelectionData.add(
            OptionSelectionData(
                AnswerEnum.YES,
                "YES",
                "YES",
                false
            )
        )
        returnMenuSelectionData.add(
            OptionSelectionData(
                AnswerEnum.NO,
                "NO",
                "NO",
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
        textsReturnMenu.putAll(
            OpenerCSV.readData(
              RawResources.returnMenu_TTS,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsReturnMenu["RETURN_MENU"].toString())
        TextToSpeechManager.speakQueue(textsReturnMenu["YES"].toString())

        idleSpeak.initIdleString(textsReturnMenu["IDLE"].toString())
    }

    override fun updateState() {
        if (returnMenuSelectionData[returnMenuIterator].selected && lastOption != returnMenuIterator) {
            textsReturnMenu[returnMenuSelectionData[returnMenuIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = returnMenuIterator
        }

        idleSpeak.updateIdleStatus()
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                returnMenuIterator++

                if (returnMenuIterator >= returnMenuSelectionData.size) {
                    returnMenuIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                returnMenuIterator--
                if (returnMenuIterator < 0) {
                    returnMenuIterator = returnMenuSelectionData.size - 1
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP->{
                LevelManager.popLevel()
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN->{
                TextToSpeechManager.speakNow(textsReturnMenu["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(returnMenuIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                    returnMenuIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                    returnMenuIterator = 1
                }
            }
        }

        returnMenuSelectionData.forEach {
            it.selected = false
        }

        returnMenuSelectionData[returnMenuIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (returnMenuSelectionData[option].levelType) {
            AnswerEnum.YES -> {
                LevelManager.changeLevel(MenuScene())
            }
            AnswerEnum.NO -> {
                LevelManager.popLevel()
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        returnMenuImage.drawImage(canvas)

        screenTexts[returnMenuSelectionData[returnMenuIterator].textValue]?.let {
            optionText.drawText(
                canvas,
                it
            )
        }
    }
}