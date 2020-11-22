package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.enums.StageEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager
import java.security.cert.PKIXRevocationChecker

class StageSelectionScene(nation: NationEnum) : SurfaceView(Settings.CONTEXT), ILevel {

    private val stageName = nation.toString()
    private var stageImage: OptionImage = OptionImage()
    private var textsStageSelection: HashMap<String, String> = HashMap()
    private var textsNations: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()
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

        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)

        stageImage.setFullScreenImage(R.drawable.select_track)
    }

    private fun initStageSelectionOptions() {

       // Log.d("TRST", stageName.toString())

        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_1,
                stageName + "_1",
                stageName + "_1",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_2,
                stageName + "_2",
                stageName + "_2",
                false
            )
        )
        stageSelectionData.add(
            OptionSelectionData(
                StageEnum.STAGE_3,
                stageName + "_3",
                stageName + "_3",
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
        textsStageSelection.putAll(
            OpenerCSV.readData(
                R.raw.stage_selection_tts,
                Settings.languageTtsEnum
            )
        )
        textsNations.putAll(OpenerCSV.readData(R.raw.nation_roads_tts, Settings.languageTtsEnum))

        screenTexts.putAll(OpenerCSV.readData(R.raw.nation_roads_texts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsStageSelection["STAGE_SELECTION_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsNations[stageName + "_1"].toString())
    }

    override fun updateState() {
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
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                stageIterator++

                if (stageIterator >= stageSelectionData.size) {
                    stageIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                stageIterator--
                if (stageIterator < 0) {
                    stageIterator = stageSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
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
        LevelManager.changeLevel(CarSelectionScene())
    }

    override fun redrawState(canvas: Canvas) {
        stageImage.drawImage(canvas)

        screenTexts[ stageSelectionData[stageIterator].textValue]?.let {
            optionText.drawText(canvas,
                it
            )
        }
    }
}