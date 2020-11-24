package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class PauseScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsPause: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var pauseImage: OptionImage = OptionImage()
    private var pauseSelectionData= arrayListOf<OptionSelectionData>()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var pauseIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()

        screenTexts.putAll(OpenerCSV.readData(R.raw.pause_texts, Settings.languageTtsEnum))
        pauseImage.setFullScreenImage(R.drawable.pause)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initTrackSelectionOptions() {
        pauseSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.GAME,
                "RESUME",
                "RESUME",
                false
            )
        )
        pauseSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "MENU",
                "MENU",
                false
            )
        )
        pauseSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.QUIT,
                "EXIT",
                "EXIT",
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
        textsPause.putAll(
            OpenerCSV.readData(
                R.raw.pause_tts,
                Settings.languageTtsEnum
            )
        )
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsPause["PAUSE"].toString())
        TextToSpeechManager.speakQueue(textsPause["RESUME"].toString())
    }

    override fun updateState() {
        if (pauseSelectionData[pauseIterator].selected && lastOption != pauseIterator) {
            textsPause[pauseSelectionData[pauseIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = pauseIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsPause["PAUSE"].toString())

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
                pauseIterator++

                if (pauseIterator >= pauseSelectionData.size) {
                    pauseIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                pauseIterator--
                if (pauseIterator < 0) {
                    pauseIterator = pauseSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(pauseIterator)
                idleTimeSeconds = 0
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
            idleTimeSeconds = 0
        }

        pauseSelectionData.forEach {
            it.selected = false
        }

        pauseSelectionData[pauseIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (pauseSelectionData[option].levelType) {
            LevelTypeEnum.GAME -> {
                LevelManager.popLevel()
            }
            LevelTypeEnum.MENU -> {
                // ds na stos sa teraz 2+
                LevelManager.changeLevel(MenuScene())
            }
            LevelTypeEnum.QUIT -> {
                LevelManager.stackLevel(QuitScene())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        pauseImage.drawImage(canvas)

        screenTexts[pauseSelectionData[pauseIterator].textValue]?.let {
            optionText.drawText(
                canvas,
                it
            )
        }
    }
}