package salicki.pawel.blindcarrally.scenes

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.app.ActivityCompat.finishAffinity
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.lang.System.exit
import kotlin.math.abs

class QuitLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts : HashMap<String, String> = HashMap()
    private var exit : Boolean = true

    // swipe
    private var x1 = 0f
    private var x2 = 0f
    private val MIN_DISTANCE = 150

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 200

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.quit_tss, Settings.languageTTS))
        TextToSpeechManager.speakNow(texts["QUIT_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["QUIT_YES"].toString())
    }

    override fun updateState() {

    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {
        var swap = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX = x2 - x1
                if (abs(deltaX) > MIN_DISTANCE) {
                    swap = true

                    SoundManager.playSound(R.raw.swoosh)

                    exit = !exit

                    if(exit){
                        texts["QUIT_YES"]?.let { TextToSpeechManager.speakNow(it) }
                    }
                    else {
                        texts["QUIT_NO"]?.let { TextToSpeechManager.speakNow(it) }
                    }

                    duration = 0
                } else {
                    swap = false
                }
            }
        }

        if(!swap){
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    startTime = System.currentTimeMillis()
                    clickCount++
                }
                MotionEvent.ACTION_UP -> {
                    val time: Long = System.currentTimeMillis() - startTime
                    duration += time

                    Log.d("CZAS", duration.toString())

                    if (clickCount >= 2) {
                        if (duration <= MAX_DURATION) {
                            SoundManager.playSound(R.raw.accept)

                            if(!exit){
                                LevelManager.changeLevel(LevelType.MENU)
                            }
                            else {
                                (Settings.CONTEXT as MainActivity).exit()
                            }
                        }
                        clickCount = 0
                        duration = 0
                    }
                }
            }
        } else {
            clickCount = 0
            duration = 0
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}