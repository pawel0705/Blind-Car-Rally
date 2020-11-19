package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class CreditsScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager =
        SoundManager()

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.credits_tts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["CREDITS_AUTHOR"].toString())
        TextToSpeechManager.speakQueue(texts["CREDITS_BACK"].toString())
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
            TextToSpeechManager.speakNow(texts["CREDITS_AUTHOR"].toString())
            TextToSpeechManager.speakQueue(texts["CREDITS_BACK"].toString())
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
    }

    override fun redrawState(canvas: Canvas) {

    }
}