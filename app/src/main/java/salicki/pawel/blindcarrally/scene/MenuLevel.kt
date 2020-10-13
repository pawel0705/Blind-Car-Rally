package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.LanguageSelectionData
import salicki.pawel.blindcarrally.data.MenuSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import kotlin.collections.HashMap

class MenuLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var soundManager: SoundManager = SoundManager()
    private var menuSelectionDataData = arrayListOf<MenuSelectionData>()
    private var menuIterator: Int = 0

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        initMenuOptions()
        readTTSTextFile()
    }

    private fun initMenuOptions(){
        menuSelectionDataData.add(MenuSelectionData(LevelType.CALIBRATION, "MENU_PLAY"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.SETTINGS, "MENU_SETTINGS"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.LANGUAGE, "MENU_LANGUAGE"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.CREDITS, "MENU_CREDITS"))
        menuSelectionDataData.add(MenuSelectionData(LevelType.QUIT, "MENU_QUIT"))
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.menu_tts, Settings.languageTTS))
    }

    override fun initState() {
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
        isFocusable = false

        this.soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        when (GestureManager.gestureDetect(event)) {
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                menuIterator++

                if (menuIterator >= menuSelectionDataData.size) {
                    menuIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                menuIterator--
                if (menuIterator < 0) {
                    menuIterator = menuSelectionDataData.size - 1
                }

                swipe = true
            }
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                soundManager.playSound(Resources.acceptSound)
                changeLevel(menuIterator)
            }
        }
    }

    private fun changeLevel(option: Int) {
        when (menuSelectionDataData[option].levelType){
            LevelType.CALIBRATION ->{
                LevelManager.changeLevel(CalibrationLevel())
            }
            LevelType.SETTINGS -> {
                LevelManager.changeLevel(SettingsLevel())
            }
            LevelType.LANGUAGE -> {
                LevelManager.changeLevel(LanguageLevel())
            }
            LevelType.CREDITS -> {
                LevelManager.changeLevel(CreditsLevel())
            }
            LevelType.QUIT -> {
                LevelManager.changeLevel(QuitLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }
}