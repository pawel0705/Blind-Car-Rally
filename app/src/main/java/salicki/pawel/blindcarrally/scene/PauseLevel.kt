package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class PauseLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsPause: HashMap<String, String> = HashMap()

    private var pauseSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var pauseIterator: Int = 0
    private var lastOption: Int = -1

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()
    }

    private fun initTrackSelectionOptions() {
        pauseSelectionData.add(
            OptionSelectionData(
                LevelType.GAME,
                "RESUME",
                "Wznów",
                false
            )
        )
        pauseSelectionData.add(
            OptionSelectionData(
               LevelType.MENU,
                "MENU",
                "Do menu",
                false
            )
        )
        pauseSelectionData.add(
            OptionSelectionData(
                LevelType.QUIT,
                "EXIT",
                "Wyjdz",
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
        textsPause.putAll(
            OpenerCSV.readData(
                R.raw.pause_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsPause["PAUSE"].toString())
        TextToSpeechManager.speakQueue(textsPause["RESUME"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (pauseSelectionData[pauseIterator].selected && lastOption != pauseIterator) {
            textsPause[pauseSelectionData[pauseIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = pauseIterator
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
                pauseIterator++

                if (pauseIterator >= pauseSelectionData.size) {
                    pauseIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                pauseIterator--
                if (pauseIterator < 0) {
                    pauseIterator = pauseSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(pauseIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    pauseIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    pauseIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    pauseIterator = 2
                }
            }
        }

        pauseSelectionData.forEach {
            it.selected = false
        }

        pauseSelectionData[pauseIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (pauseSelectionData[option].levelType) {
            LevelType.GAME -> {
                LevelManager.popLevel()
            }
            LevelType.MENU -> {
                // ds na stos sa teraz 2+
                LevelManager.changeLevel(MenuLevel())
            }
            LevelType.QUIT -> {
                LevelManager.stackLevel(QuitLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}