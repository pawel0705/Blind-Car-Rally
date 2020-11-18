package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.datas.StageResultData
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

class RaceOverScene(raceData: StageResultData) : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsRaceOver: HashMap<String, String> = HashMap()
    private var raceData: StageResultData = raceData
    private var raceOverSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager =
        SoundManager()
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
                LevelTypeEnum.MENU,
                "MENU",
                "Wznów",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.GAME,
                "RACE_AGAIN",
                "Do menu",
                false
            )
        )
        raceOverSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MODE_DESCRIPTION,
                "READ_SCORE",
                "Do menu",
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
        textsRaceOver.putAll(
            OpenerCSV.readData(
                R.raw.race_over_tts,
                Settings.languageTtsEnum
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
    }

    override fun updateState() {
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
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                raceOverIterator++

                if (raceOverIterator >= raceOverSelectionData.size) {
                    raceOverIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                raceOverIterator--
                if (raceOverIterator < 0) {
                    raceOverIterator = raceOverSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
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
            LevelTypeEnum.MENU -> {
                LevelManager.changeLevel(MenuScene())
            }
            LevelTypeEnum.GAME -> {
                // ds na stos sa teraz 2+
                LevelManager.changeLevel(GameScene())
            }
            LevelTypeEnum.MODE_DESCRIPTION -> {
                scoreDescription()
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}