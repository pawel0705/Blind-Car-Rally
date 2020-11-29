package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
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

class CreditsScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var optionText: TextObject = TextObject()
    private var soundManager: SoundManager =
        SoundManager()

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        texts["INSTRUCTION"]?.let {
            optionText.initMultiLineText(
                R.font.montserrat,
                R.dimen.informationSize,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 10F,
                it
            )
        }
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.credits_tts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["INSTRUCTION"].toString())
    }

    override fun updateState() {
        if(TextToSpeechManager.isSpeaking()){
            idleTime = 0
        }

        idleTime++

        if(idleTime % 30 == 0){
            idleTimeSeconds++
        }

        if(idleTimeSeconds > 10 && !TextToSpeechManager.isSpeaking()){
            TextToSpeechManager.speakNow(texts["INSTRUCTION"].toString())
            idleTimeSeconds = 0
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)
                LevelManager.changeLevel(MenuScene())
            }
        }

        when(GestureManager.swipeDetect(event)){
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(MenuScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(texts["INSTRUCTION"].toString())
                idleTimeSeconds = 0
                Settings.globalSounds.playSound(RawResources.swapSound)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        optionText.drawMultilineText(canvas)
    }
}