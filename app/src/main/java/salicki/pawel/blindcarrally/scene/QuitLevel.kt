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
    private var exitIterator: Int = 0
    private var lastOption: Int = 0
    private var swipe: Boolean = false
    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

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
        if(exitIterator != lastOption){
            if (exit) {
                texts["QUIT_YES"]?.let { TextToSpeechManager.speakNow(it) }
            } else {
                texts["QUIT_NO"]?.let { TextToSpeechManager.speakNow(it) }
            }

            lastOption = exitIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())

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
            GestureType.SWIPE_LEFT, GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                exit = !exit
                exitIterator = if (exit) {
                    0
                } else {
                    1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureType.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(Resources.acceptSound)
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

            idleTimeSeconds = 0
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}