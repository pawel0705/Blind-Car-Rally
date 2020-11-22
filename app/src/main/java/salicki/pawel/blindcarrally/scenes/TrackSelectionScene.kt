package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class TrackSelectionScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsTrackSelection: HashMap<String, String> = HashMap()
    private var textsNations: HashMap<String, String> = HashMap()
    private var trackSelectionImage: OptionImage = OptionImage()
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var trackSelectionData = arrayListOf<OptionSelectionData>()
    private var trackIterator: Int = 0
    private var lastOption: Int = -1

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()

        trackSelectionImage.setFullScreenImage(R.drawable.select_nation)
    }

    private fun initTrackSelectionOptions() {
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.ARGENTINA,
                "ARGENTINA",
                "Argentyna",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.AUSTRALIA,
                "AUSTRALIA",
                "Australia",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.POLAND,
                "POLAND",
                "Polska",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.SPAIN,
                "SPAIN",
                "Hiszpania",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.NEW_ZEALAND,
                "NEW_ZEALAND",
                "Nowa Zelania",
                false
            )
        )
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        textsTrackSelection.putAll(
            OpenerCSV.readData(
                R.raw.track_selection_tts,
                Settings.languageTtsEnum
            )
        )
        textsNations.putAll(OpenerCSV.readData(R.raw.tracks_tts, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTrackSelection["TRACK_SELECTION_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsNations["ARGENTINA"].toString())
    }

    override fun updateState() {
        if (trackSelectionData[trackIterator].selected && lastOption != trackIterator) {
            textsNations[trackSelectionData[trackIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = trackIterator
        }

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(textsTrackSelection["TRACK_SELECTION_TUTORIAL"].toString())

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
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                trackIterator++

                if (trackIterator >= trackSelectionData.size) {
                    trackIterator = 0
                }

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                trackIterator--
                if (trackIterator < 0) {
                    trackIterator = trackSelectionData.size - 1
                }

                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                TextToSpeechManager.stop()
                Settings.globalSounds.playSound(RawResources.acceptSound)
                changeLevel(trackIterator)
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 5 -> {
                    trackIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 2 -> {
                    trackIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 3 -> {
                    trackIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 4 -> {
                    trackIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 5 * 5 -> {
                    trackIterator = 4
                }
            }
        }

        trackSelectionData.forEach {
            it.selected = false
        }

        trackSelectionData[trackIterator].selected = true
    }

    private fun changeLevel(option: Int) {
        when (trackSelectionData[option].levelType) {

            NationEnum.ARGENTINA -> {
                GameOptions.nation = NationEnum.ARGENTINA
                LevelManager.changeLevel(StageSelectionScene(NationEnum.ARGENTINA))
            }
            NationEnum.AUSTRALIA -> {
                GameOptions.nation = NationEnum.AUSTRALIA
                LevelManager.changeLevel(StageSelectionScene(NationEnum.AUSTRALIA))
            }
            NationEnum.POLAND -> {
                GameOptions.nation = NationEnum.POLAND
                LevelManager.changeLevel(StageSelectionScene(NationEnum.POLAND))
            }
            NationEnum.SPAIN -> {
                GameOptions.nation = NationEnum.SPAIN
                LevelManager.changeLevel(StageSelectionScene(NationEnum.SPAIN))
            }
            NationEnum.NEW_ZEALAND -> {
                GameOptions.nation = NationEnum.NEW_ZEALAND
                LevelManager.changeLevel(StageSelectionScene(NationEnum.NEW_ZEALAND))
            }
        }
    }

    override fun redrawState(canvas: Canvas) {
        trackSelectionImage.drawImage(canvas)
    }
}