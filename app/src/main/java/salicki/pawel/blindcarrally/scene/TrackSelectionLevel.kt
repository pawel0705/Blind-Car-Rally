package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel

class TrackSelectionLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.track_selection_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["TRACK_SELECTION_TUTORIAL"].toString())
    }

    override fun updateState(deltaTime: Int) {

    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)

            }
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)

            }
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                soundManager.playSound(Resources.acceptSound)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}