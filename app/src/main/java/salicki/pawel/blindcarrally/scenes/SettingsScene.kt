package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.datas.OptionSelectionData
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.enums.LevelTypeEnum
import salicki.pawel.blindcarrally.gameresources.SelectBoxManager
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.GestureManager
import salicki.pawel.blindcarrally.utils.OpenerCSV
import salicki.pawel.blindcarrally.utils.SharedPreferencesManager
import salicki.pawel.blindcarrally.utils.SoundManager

class SettingsScene : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts: HashMap<String, String> = HashMap()
    private var settingsIterator = 0
    private var soundManager: SoundManager =
        SoundManager()
    private var swipe: Boolean = false
    private var settingsSelectionData = arrayListOf<OptionSelectionData>()
    private var selectBoxManager: SelectBoxManager =
        SelectBoxManager()
    private var lastOption = -1
   // private var optionText = TextObject()

    private var idleTime: Int = 0
    private var idleTimeSeconds: Int = 0


    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initSettingsOptions()
        initSelectBoxModel()
        initTextOption()
    }

    private fun initSoundManager(){
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun initSelectBoxModel(){
        selectBoxManager.initSelectBoxModel(6)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(R.raw.settings_tts, Settings.languageTtsEnum))
    }

    private fun initTextOption(){
  //      optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initSettingsOptions() {
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.VIBRATION,
                if(Settings.vibrations) "SETTINGS_VIBRATION_ON" else "SETTINGS_VIBRATION_OFF",
                "Wibracje",
                false
            )
        )
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.DISPLAY,
                if(Settings.display) "SETTINGS_DISPLAY_ON" else "SETTINGS_DISPLAY_OFF",
                "Wyświetlanie ekranu",
                false
            )
        )
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.INTRODUCTION,
                if(Settings.display) "SETTINGS_INTRODUCTION_ON" else "SETTINGS_INTRODUCTION_OFF",
                "Wstępna informacja",
                false
            )
        )
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.VOLUME_TTS,
                "SETTINGS_TTS",
                "Głośność lektora",
                false
            )
        )
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.VOLUME_SOUNDS,
                "SETTINGS_SOUNDS",
                "Głośność dźwięków",
                false
            )
        )
        settingsSelectionData.add(
            OptionSelectionData(
                LevelTypeEnum.MENU,
                "SETTINGS_EXIT",
                "Wyjdź do menu",
                false
            )
        )
    }

    override fun initState() {

        TextToSpeechManager.speakNow(texts["SETTINGS_TUTORIAL"].toString())
        if (Settings.vibrations) {
            TextToSpeechManager.speakQueue(texts["SETTINGS_VIBRATION_ON"].toString())
        } else {
            TextToSpeechManager.speakQueue(texts["SETTINGS_VIBRATION_OFF"].toString())
        }
    }

    override fun updateState() {
        if (settingsSelectionData[settingsIterator].selected && lastOption != settingsIterator) {
            texts[settingsSelectionData[settingsIterator].textKey]?.let {
                TextToSpeechManager.speakNow(
                    it
                )
            }

            lastOption = settingsIterator
        }

        selectBoxManager.updateSelectBoxPosition(settingsIterator)

        if(!TextToSpeechManager.isSpeaking()){
            idleTime++

            if(idleTime % 30 == 0){
                idleTimeSeconds++
            }
        }

        if(idleTimeSeconds > 10){
            TextToSpeechManager.speakNow(texts["SETTINGS_TUTORIAL"].toString())

            idleTimeSeconds = 0
        }
    }

    override fun destroyState() {
        isFocusable = false

        soundManager.destroy()
    }

    override fun respondTouchState(event: MotionEvent) {

        swipe = false

        when(GestureManager.swipeDetect(event))
        {
            GestureTypeEnum.SWIPE_RIGHT -> {
                soundManager.playSound(RawResources.swapSound)
                settingsIterator++

                if (settingsIterator > 5) {
                    settingsIterator = 0
                }

                swipe = true
                idleTimeSeconds = 0
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                settingsIterator--
                if (settingsIterator < 0) {
                    settingsIterator = 5
                }

                swipe = true
                idleTimeSeconds = 0
            }
        }

        when (GestureManager.doubleTapDetect(event)) {
            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)

                when (settingsIterator) {
                    0 -> {
                        Settings.vibrations = !Settings.vibrations

                        var saveOption = "0"
                        if(Settings.display){
                            saveOption = "1"
                        }

                        SharedPreferencesManager.saveConfiguration("vibrations", saveOption)

                        if (Settings.vibrations) {
                            TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_ON"].toString())
                        } else {
                            TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_OFF"].toString())
                        }
                    }
                    1 -> {
                        Settings.display = !Settings.display

                        var saveOption = "0"
                        if(Settings.display){
                            saveOption = "1"
                        }

                        SharedPreferencesManager.saveConfiguration("display", saveOption)

                        if (Settings.display) {
                            TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_ON"].toString())
                        } else {
                            TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_OFF"].toString())
                        }
                    }
                    2->{
                        Settings.introduction = !Settings.introduction

                        var saveOption = "0"
                        if(Settings.introduction){
                            saveOption = "1"
                        }

                        SharedPreferencesManager.saveConfiguration("introduction", saveOption)

                        if (Settings.introduction) {
                            TextToSpeechManager.speakNow(texts["SETTINGS_INTRODUCTION_ON"].toString())
                        } else {
                            TextToSpeechManager.speakNow(texts["SETTINGS_INTRODUCTION_OFF"].toString())
                        }
                    }
                    3 -> {
                        LevelManager.changeLevel(VolumeTtsScene())
                    }
                    4 -> {
                        LevelManager.changeLevel(VolumeSoundsScene())
                    }
                    5 -> {
                        LevelManager.changeLevel(MenuScene())
                    }
                }

                idleTimeSeconds = 0
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 6 -> {
                    settingsIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 6 * 2 -> {
                    settingsIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 6 * 3 -> {
                    settingsIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 6 * 4 -> {
                    settingsIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 6 * 5 -> {
                    settingsIterator = 4
                }
                holdPosition < Settings.SCREEN_WIDTH / 6 * 6 -> {
                    settingsIterator = 5
                }
            }

            idleTimeSeconds = 0
        }

        settingsSelectionData.forEach {
            it.selected = false
        }

        settingsSelectionData[settingsIterator].selected = true
    }

    override fun redrawState(canvas: Canvas) {
        selectBoxManager.drawSelectBox(canvas)
  //      optionText.drawText(canvas, settingsSelectionData[settingsIterator].textValue)

    }
}