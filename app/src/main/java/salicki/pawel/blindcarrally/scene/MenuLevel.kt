package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.MenuSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import kotlin.collections.HashMap

class MenuLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var SoundManager: SoundManager = SoundManager()
    private var menuSelectionDataData = arrayListOf<MenuSelectionData>()
    private var menuIterator: Int = 0

    private var swipe: Boolean = false

    init {
        isFocusable = true
        SoundManager.initSoundManager()
        menuSelectionDataData.add(MenuSelectionData(LevelType.CALIBRATION, "MENU_PLAY"))
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

    override fun updateState(deltaTime: Int) {
        if (swipe) {
            texts[menuSelectionDataData[menuIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }
            swipe = false
        }
    }

    override fun destroyState() {

    }

    override fun respondTouchState(event: MotionEvent) {

        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                SoundManager.playSound(R.raw.swoosh)
                menuIterator++

                if (menuIterator >= menuSelectionDataData.size) {
                    menuIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_RIGHT -> {
                SoundManager.playSound(R.raw.swoosh)
                menuIterator--
                if (menuIterator < 0) {
                    menuIterator = menuSelectionDataData.size - 1
                }

                swipe = true
            }
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                SoundManager.playSound(R.raw.accept)
                LevelManager.changeLevel(menuSelectionDataData[menuIterator].levelType)
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}