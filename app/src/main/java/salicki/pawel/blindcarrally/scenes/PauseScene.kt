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
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class PauseScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var textsPause: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var pauseSelectionData= arrayListOf<OptionSelectionData>()

    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var optionText: TextObject = TextObject()
    private var pauseImage: OptionImage = OptionImage()
    private var soundManager: SoundManager =
        SoundManager()

    private var pauseIterator: Int = 0
    private var lastOption: Int = -1

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()

        screenTexts.putAll(OpenerCSV.readData(RawResources.pause_TXT, Settings.languageTtsEnum))
        pauseImage.setFullScreenImage(DrawableResources.pauseView)
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
               RawResources.pause_TTS,
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
                pauseIterator++

                if (pauseIterator >= pauseSelectionData.size) {
                    pauseIterator = 0
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                pauseIterator--
                if (pauseIterator < 0) {
                    pauseIterator = pauseSelectionData.size - 1
                }

                swipe = true
                idleSpeak.resetIdleTimeSeconds()
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.popLevel()
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsPause["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(pauseIterator)
                idleSpeak.resetIdleTimeSeconds()
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
            idleSpeak.resetIdleTimeSeconds()
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