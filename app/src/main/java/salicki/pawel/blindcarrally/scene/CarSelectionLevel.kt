package salicki.pawel.blindcarrally.scene

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.data.OptionSelectionData
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager

class CarSelectionLevel : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsCarsSelection: HashMap<String, String> = HashMap()

    private var soundManager: SoundManager = SoundManager()
    private var swipe: Boolean = false
    private var carsSelectionData = arrayListOf<OptionSelectionData>()
    private var carIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initCarSelectionOptions()
    }

    private fun initCarSelectionOptions() {
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_1,
                "CAR_1",
                "Argentyna",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_2,
                "CAR_2",
                "Argentyna",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_3,
                "CAR_3",
                "Argentyna",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_4,
                "CAR_4",
                "Argentyna",
                false
            )
        )
        carsSelectionData.add(
            OptionSelectionData(
                CarEnum.CAR_5,
                "CAR_5",
                "Argentyna",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(Resources.swapSound)
        soundManager.addSound(Resources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsCarsSelection.putAll(OpenerCSV.readData(R.raw.car_selection_tts, Settings.languageTTS))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsCarsSelection["CAR_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsCarsSelection["CAR_1"].toString())
    }

    override fun updateState(deltaTime: Int) {
        if (carsSelectionData[carIterator].selected && lastOption != carIterator) {
            textsCarsSelection[carsSelectionData[carIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = carIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsCarsSelection["CAR_TUTORIAL"].toString())

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
            GestureType.SWIPE_RIGHT -> {
                soundManager.playSound(Resources.swapSound)
                carIterator++

                if (carIterator >= carsSelectionData.size) {
                    carIterator = 0
                }

                swipe = true
            }
            GestureType.SWIPE_LEFT -> {
                soundManager.playSound(Resources.swapSound)
                carIterator--
                if (carIterator < 0) {
                    carIterator = carsSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureType.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(Resources.acceptSound)
                changeLevel(carIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 5 -> {
                    carIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 2 -> {
                    carIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 3 -> {
                    carIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 4 -> {
                    carIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 5 -> {
                    carIterator = 4
                }
            }
        }

        carsSelectionData.forEach {
            it.selected = false
        }

        carsSelectionData[carIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        GameOptions.car = carsSelectionData[option].levelType as CarEnum
        LevelManager.stackLevel(CarPerformanceLevel())
    }

    override fun redrawState(canvas: Canvas) {

    }
}