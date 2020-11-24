package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.AnswerEnum
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class ReturnMenuScene: SurfaceView(Settings.CONTEXT), ILevel {

    private var textsReturnMenu: HashMap<String, String> = HashMap()

    private var returnMenuSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var returnMenuIterator: Int = 0
    private var lastOption: Int = -1

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()
    }

    private fun initTrackSelectionOptions() {
        returnMenuSelectionData.add(
            OptionSelectionData(
                AnswerEnum.YES,
                "YES",
                "Tak",
                false
            )
        )
        returnMenuSelectionData.add(
            OptionSelectionData(
                AnswerEnum.NO,
                "NO",
                "Nie",
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
                R.raw.return_menu_tts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsReturnMenu["RETURN_MENU"].toString())
        TextToSpeechManager.speakQueue(textsReturnMenu["YES"].toString())
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
                // ds na stos sa teraz 2+
                LevelManager.changeLevel(MenuScene())
            }
            AnswerEnum.NO -> {
                LevelManager.popLevel()
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}