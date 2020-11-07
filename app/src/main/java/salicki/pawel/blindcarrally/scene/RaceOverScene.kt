package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.data.StageResultData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class RaceOverScene(raceData: StageResultData) : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsRaceOver: HashMap<String, String> = HashMap()
    private var raceData: StageResultData = raceData
    private var raceOverSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var raceOverIterator: Int = 0
    private var lastOption: Int = -1

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()
    }

    private fun initTrackSelectionOptions() {
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelType.MENU,
                "MENU",
                "Wznów",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelType.GAME,
                "RACE_AGAIN",
                "Do menu",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelType.MODE_DESCRIPTION,
                "READ_SCORE",
                "Do menu",
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
        textsRaceOver.putAll(
            OpenerCSV.readData(
                R.raw.race_over_tts,
                Settings.languageTTS
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsRaceOver["STAGE_COMPLETED_1"].toString())
        scoreDescription()
        TextToSpeechManager.speakQueue(textsRaceOver["MENU"].toString())
    }

    private fun scoreDescription(){
        TextToSpeechManager.speakQueue(textsRaceOver["CAR_DAMAGE"].toString() + raceData.carDamage + "%.")
        TextToSpeechManager.speakQueue(textsRaceOver["TIME"].toString() + raceData.time + textsRaceOver["SECONDS"].toString())
        TextToSpeechManager.speakQueue(textsRaceOver["SCORE"].toString() + raceData.score + textsRaceOver["POINTS"].toString())
        TextToSpeechManager.speakQueue(textsRaceOver["SCORE"].toString() + raceData.score + textsRaceOver["POINTS"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (raceOverSelectionData[raceOverIterator].selected && lastOption != raceOverIterator) {
            textsRaceOver[raceOverSelectionData[raceOverIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = raceOverIterator
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
                raceOverIterator++

                if (raceOverIterator >= raceOverSelectionData.size) {
                    raceOverIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                raceOverIterator--
                if (raceOverIterator < 0) {
                    raceOverIterator = raceOverSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(raceOverIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    raceOverIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    raceOverIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    raceOverIterator = 2
                }
            }
        }

        raceOverSelectionData.forEach {
            it.selected = false
        }

        raceOverSelectionData[raceOverIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (raceOverSelectionData[option].levelType) {
            LevelType.MENU -> {
                LevelManager.changeLevel(MenuLevel())
            }
            LevelType.GAME -> {
                // ds na stos sa teraz 2+
                LevelManager.changeLevel(GameLevel())
            }
            LevelType.MODE_DESCRIPTION -> {
                scoreDescription()
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}