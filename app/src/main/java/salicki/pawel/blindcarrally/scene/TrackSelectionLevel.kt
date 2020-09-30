package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel

class TrackSelectionLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()

    private var swipe: Boolean = false

    init {
        isFocusable = true

    }

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.track_selection_tts, Settings.languageTTS))
        TextToSpeechManager.speakNow(texts["TRACK_SELECTION_TUTORIAL"].toString())
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {

        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                SoundManager.playSound(R.raw.swoosh)

            }
            GestureType.SWIPE_RIGHT -> {
                SoundManager.playSound(R.raw.swoosh)

            }
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                SoundManager.playSound(R.raw.accept)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}