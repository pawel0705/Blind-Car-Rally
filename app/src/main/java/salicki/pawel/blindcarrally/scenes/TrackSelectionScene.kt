package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.NationEnum
import salicki.pawel.blindcarrally.gameresources.OptionImage
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.GameOptions
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.DrawableResources
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.IdleSpeakManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SoundManager

class TrackSelectionScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var textsTrackSelection: HashMap<String, String> = HashMap()
    private var textsNations: HashMap<String, String> = HashMap()
    private var screenTexts: HashMap<String, String> = HashMap()
    private var trackSelectionData = arrayListOf<OptionSelectionData>()

    private var optionText: TextObject = TextObject()
    private var trackSelectionImage: OptionImage = OptionImage()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var soundManager: SoundManager =
        SoundManager()

    private var trackIterator: Int = 0
    private var lastOption: Int = -1

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initTrackSelectionOptions()

        trackSelectionImage.setFullScreenImage(DrawableResources.selectNationView)
    }

    private fun initTrackSelectionOptions() {
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.ARGENTINA,
                "ARGENTINA",
                "ARGENTINA",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.AUSTRALIA,
                "AUSTRALIA",
                "AUSTRALIA",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.POLAND,
                "POLAND",
                "POLAND",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.SPAIN,
                "SPAIN",
                "SPAIN",
                false
            )
        )
        trackSelectionData.add(
            OptionSelectionData(
                NationEnum.NEW_ZEALAND,
                "NEW_ZEALAND",
                "NEW_ZEALAND",
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
                RawResources.track_selection_TTS,
                Settings.languageTtsEnum
            )
        )
        textsNations.putAll(OpenerCSV.readData(RawResources.tracks_TTS, Settings.languageTtsEnum))

        screenTexts.putAll(OpenerCSV.readData(RawResources.tracks_TXT, Settings.languageTtsEnum))
    }

    override fun initState() {
        TextToSpeechManager.speakNow(textsTrackSelection["TRACK_SELECTION_TUTORIAL"].toString())
        TextToSpeechManager.speakQueue(textsNations["ARGENTINA"].toString())

        screenTexts[trackSelectionData[trackIterator].textValue]?.let {
            optionText.initText(
                R.font.hemi,
                Settings.SCREEN_WIDTH / 2F,
                Settings.SCREEN_HEIGHT / 3F
            )
        }

        idleSpeak.initIdleString(textsNations["IDLE"].toString())
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

        idleSpeak.updateIdleStatus()
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
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(GameModeScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(textsTrackSelection["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
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

        screenTexts[trackSelectionData[trackIterator].textValue]?.let {
            optionText.drawText(
                canvas,
                it
            )
        }
    }
}