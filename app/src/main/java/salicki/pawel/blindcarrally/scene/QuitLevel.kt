package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class QuitLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var exit: Boolean = true
    private var soundManager: SoundManager = SoundManager()

    init{
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["QUIT_YES"].toString())
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.quit_tts, Settings.languageTTS))
    }

    override fun updateState(deltaTime: Int) {

    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.doubleTapDetect(event)) {
            GestureType.SWIPE_LEFT, GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                exit = !exit
                if (exit) {
                    texts["QUIT_YES"]?.let { TextToSpeechManager.speakNow(it) }
                } else {
                    texts["QUIT_NO"]?.let { TextToSpeechManager.speakNow(it) }
                }
            }
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
                if (!exit) {
                    LevelManager.changeLevel(MenuLevel())
                } else {
                    (Settings.CONTEXT as MainActivity).exit()
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}