package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.text.TextPaint
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType

class GameModeLevel: SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var infoTextPaint = TextPaint()
    private var modeSelectionData = arrayListOf<OptionSelectionData>()
    private var modeIterator: Int = 0
    private var swipe: Boolean = false
    private var soundManager: SoundManager = SoundManager()
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        readTTSTextFile()
        initModeOptions()
        initSoundManager()
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun initModeOptions(){
        modeSelectionData.add(
            OptionSelectionData(
                LevelType.SINGLE,
                "MODE_SINGLE",
                "Pojedyńczy wyścig",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelType.TOURNAMENT,
                "MODE_TOURNAMENT",
                "Tryb turniejowy",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelType.MODE_DESCRIPTION,
                "MODE_DESCRIPTION",
                "Opis trybów",
                false
            )
        )
        modeSelectionData.add(
            OptionSelectionData(
                LevelType.MENU,
                "MODE_MENU",
                "Powrót",
                false
            )
        )
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.gamemode_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["MODE_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(texts["MODE_SINGLE"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (modeSelectionData[modeIterator].selected && lastOption != modeIterator) {
            texts[modeSelectionData[modeIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = modeIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(texts["MODE_TUTORIAL"].toString())

            idleTimeSeconds = 0
        }
    }

    override fun destroyState() {
        soundManager.destroy()

        isFocusable = false
    }

    override fun respondTouchState(event: MotionEvent) {
        swipe = false

        when (GestureManager.swipeDetect(event)) {
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                modeIterator++

                if (modeIterator >= modeSelectionData.size) {
                    modeIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                modeIterator--
                if (modeIterator < 0) {
                    modeIterator = modeSelectionData.size - 1
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when(GestureManager.doubleTapDetect(event))
        {
            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(modeIterator)

                idleTimeSeconds = 0
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 4 -> {
                    modeIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 2 -> {
                    modeIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 3 -> {
                    modeIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 4 * 4 -> {
                    modeIterator = 3
                }
            }

            idleTimeSeconds = 0
        }

        modeSelectionData.forEach {
            it.selected = false
        }

        modeSelectionData[modeIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (modeSelectionData[option].levelType) {
            LevelType.SINGLE -> {
                LevelManager.changeLevel(TrackSelectionLevel())
            }
            LevelType.TOURNAMENT -> {
                LevelManager.changeLevel(CalibrationLevel())
            }
            LevelType.MODE_DESCRIPTION -> {
                TextToSpeechManager.speakNow(texts["MODE_DESCRIPTION_CLICKED"].toString())
            }
            LevelType.MENU -> {
                LevelManager.changeLevel(MenuLevel())
            }
        }
    }

    override fun redrawState(canvas: Canvas) {

    }

}