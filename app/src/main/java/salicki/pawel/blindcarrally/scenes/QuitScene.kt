package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
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

class QuitScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()

    private var quitImage: OptionImage = OptionImage()
    private var optionText: TextObject = TextObject()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var soundManager: SoundManager =
        SoundManager()

    private var exitIterator: Int = 0
    private var lastOption: Int = 0

    private var swipe: Boolean = false
    private var exit: Boolean = true

    init{
        isFocusable = true

        initSoundManager()
        readTTSTextFile()

        quitImage.setFullScreenImage(DrawableResources.yesNoView)
        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["QUIT_YES"].toString())

        idleSpeak.initIdleString(texts["IDLE"].toString())
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(RawResources.quit_TTS, Settings.languageTtsEnum))
        screenTexts.putAll(OpenerCSV.readData(RawResources.quit_TXT, Settings.languageTtsEnum))
    }

    override fun updateState() {
        if(exitIterator != lastOption){
            if (exit) {
                texts["QUIT_YES"]?.let { TextToSpeechManager.speakNow(it) }
            } else {
                texts["QUIT_NO"]?.let { TextToSpeechManager.speakNow(it) }
            }

            lastOption = exitIterator
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
            GestureTypeEnum.SWIPE_LEFT, GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                exit = !exit
                exitIterator = if (exit) {
                    0
                } else {
                    1
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
                TextToSpeechManager.speakNow(texts["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)
                if (!exit) {
                    LevelManager.popLevel()
                } else {
                    (Settings.CONTEXT as MainActivity).exit()
                }
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 2 -> {
                    exitIterator = 0
                    exit = true
                }
                holdPosition < Settings.SCREEN_WIDTH / 2 * 2 -> {
                    exitIterator = 1
                    exit = false
                }
            }

            idleSpeak.resetIdleTimeSeconds()
        }
    }

    override fun redrawState(canvas: Canvas) {
        quitImage.drawImage(canvas)

        if(exit){
            screenTexts["QUIT_YES"]?.let { optionText.drawText(canvas, it) }
        }
        else {
            screenTexts["QUIT_NO"]?.let { optionText.drawText(canvas, it) }
        }
    }
}