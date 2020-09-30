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

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.quit_tts, Settings.languageTTS))
        TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["QUIT_YES"].toString())
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT, GestureType.SWIPE_RIGHT -> {
                SoundManager.playSound(R.raw.swoosh)
                exit = !exit
                if (exit) {
                    texts["QUIT_YES"]?.let { TextToSpeechManager.speakNow(it) }
                } else {
                    texts["QUIT_NO"]?.let { TextToSpeechManager.speakNow(it) }
                }
            }
            GestureType.DOUBLE_TAP -> {
                SoundManager.playSound(R.raw.accept)
                if (!exit) {
                    LevelManager.changeLevel(LevelType.MENU)
                } else {
                    (Settings.CONTEXT as MainActivity).exit()
                }
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}