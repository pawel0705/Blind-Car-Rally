package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.AnswerEnum
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class ReturnMenuLevel: SurfaceView(Settings.CONTEXT), ILevel {

    private var textsReturnMenu: HashMap<String, String> = HashMap()

    private var returnMenuSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager = SoundManager()
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

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsReturnMenu.putAll(
            OpenerCSV.readData(
                R.raw.return_menu_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsReturnMenu["RETURN_MENU"].toString())
        TextToSpeechManager.speakQueue(textsReturnMenu["YES"].toString())
    }

    override fun updateState(deltaTime: Int) {
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
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                returnMenuIterator++

                if (returnMenuIterator >= returnMenuSelectionData.size) {
                    returnMenuIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                returnMenuIterator--
                if (returnMenuIterator < 0) {
                    returnMenuIterator = returnMenuSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
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
                LevelManager.changeLevel(MenuLevel())
            }
            LevelType.MENU -> {
                LevelManager.popLevel()
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}