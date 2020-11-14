package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager

class StageSelectionLevel(nation: NationEnum) : SurfaceView(Settings.CONTEXT), ILevel {

    private val stageName = nation.toString()

    private var textsStageSelection: HashMap<String, String> = HashMap()
    private var textsNations: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var stageSelectionData = arrayListOf<OptionSelectionData>()
    private var stageIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initStageSelectionOptions()
    }

    private fun initStageSelectionOptions() {

       // Log.d("TRST", stageName.toString())

        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_1,
                stageName + "_1",
                "Argentyna",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_2,
                stageName + "_2",
                "Australia",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_3,
                stageName + "_3",
                "Polska",
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
        textsStageSelection.putAll(
            OpenerCSV.readData(
                R.raw.stage_selection_tts,
                Settings.languageTTS
            )
        )
        textsNations.putAll(OpenerCSV.readData(R.raw.nation_roads_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsStageSelection["STAGE_SELECTION_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsNations[stageName + "_1"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (stageSelectionData[stageIterator].selected && lastOption != stageIterator) {
            textsNations[stageSelectionData[stageIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = stageIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsStageSelection["STAGE_SELECTION_TUTORIAL"].toString())

            idleTimeSeconds = 0
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
                stageIterator++

                if (stageIterator >= stageSelectionData.size) {
                    stageIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                stageIterator--
                if (stageIterator < 0) {
                    stageIterator = stageSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(stageIterator)
                idleTimeSeconds = 0
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 3 -> {
                    stageIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 2 -> {
                    stageIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 3 * 3 -> {
                    stageIterator = 2
                }
            }
            idleTimeSeconds = 0
        }

        stageSelectionData.forEach {
            it.selected = false
        }

        stageSelectionData[stageIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        GameOptions.stage = stageSelectionData[option].levelType as StageEnum
        LevelManager.changeLevel(CarSelectionLevel())
    }

    override fun redrawState(canvas: Canvas) {

    }
}