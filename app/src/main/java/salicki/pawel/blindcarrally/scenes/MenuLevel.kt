package salicki.pawel.blindcarrally.scenes

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

class MenuLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts : HashMap<String, String> = HashMap()

    private var menuSelectionDataData = arrayListOf<MenuSelectionData>()
    private var menuIterator: Int = 0

    // swipe
    private var x1 = 0f
    private var x2 = 0f
    private val MIN_DISTANCE = 150

    // double tap
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 200

    init {
        isFocusable = true

        menuSelectionDataData.add(MenuSelectionData(LevelType.GAME, "MENU_PLAY"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.SETTINGS, "MENU_SETTINGS"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.LANGUAGE, "MENU_LANGUAGE"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.CREDITS, "MENU_CREDITS"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.QUIT, "MENU_QUIT"))
    }

    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.menu_tts, Settings.languageTTS))
        TextToSpeechManager.speakNow(texts["MENU_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["MENU_PLAY"].toString())
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

                    if (x2 < x1) {
                        menuIterator++

                        if(menuIterator >= menuSelectionDataData.size){
                            menuIterator = 0
                        }
                    } else {
                        menuIterator--
                        if(menuIterator < 0) {
                            menuIterator = menuSelectionDataData.size - 1
                        }
                    }

                    texts[menuSelectionDataData[menuIterator].textKey]?.let {
                        TextToSpeechManager.speakNow(
                            it
                        )
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
                            LevelManager.changeLevel(menuSelectionDataData[menuIterator].levelType)
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