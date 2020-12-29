package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.enums.GestureTypeEnum
import salicki.pawel.blindcarrally.gameresources.TextObject
import salicki.pawel.blindcarrally.gameresources.TextToSpeechManager
import salicki.pawel.blindcarrally.information.Settings
import salicki.pawel.blindcarrally.resources.RawResources
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.utils.*

class VolumeSoundsScene : SurfaceView(Settings.CONTEXT), ILevel {
    private var texts: HashMap<String, String> = HashMap()
    private var volume: ArrayList<String> = ArrayList()

    private var optionText: TextObject = TextObject()
    private var idleSpeak: IdleSpeakManager = IdleSpeakManager()
    private var soundManager: SoundManager =
        SoundManager()

    private var volumeIterator = 0
    private var lastOption = 0

    private var swipe: Boolean = false

    init {
        isFocusable = true

        initSoundManager()
        readTTSTextFile()
        initVolumeOptions()

        optionText.initText(R.font.hemi, Settings.SCREEN_WIDTH / 2F, Settings.SCREEN_HEIGHT / 3F)
    }

    private fun initVolumeOptions() {
        volume.add("SETTINGS_VOLUME_10")
        volume.add("SETTINGS_VOLUME_20")
        volume.add("SETTINGS_VOLUME_30")
        volume.add("SETTINGS_VOLUME_40")
        volume.add("SETTINGS_VOLUME_50")
        volume.add("SETTINGS_VOLUME_60")
        volume.add("SETTINGS_VOLUME_70")
        volume.add("SETTINGS_VOLUME_80")
        volume.add("SETTINGS_VOLUME_90")
        volume.add("SETTINGS_VOLUME_100")
    }

    override fun initState() {
        TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

        idleSpeak.initIdleString(texts["IDLE"].toString())
    }

    private fun initSoundManager() {
        soundManager.initSoundManager()

        soundManager.addSound(RawResources.swapSound)
        soundManager.addSound(RawResources.acceptSound)
    }

    private fun readTTSTextFile() {
        texts.putAll(OpenerCSV.readData(RawResources.settings_TTS, Settings.languageTtsEnum))
    }

    override fun updateState() {
        if (volumeIterator != lastOption) {

            TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

            lastOption = volumeIterator
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
                volumeIterator++

                if (volumeIterator >= volume.size) {
                    volumeIterator = 0
                }

                Settings.sounds = volumeIterator + 1

                SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())

                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
            GestureTypeEnum.SWIPE_LEFT -> {
                soundManager.playSound(RawResources.swapSound)
                volumeIterator--
                if (volumeIterator < 0) {
                    volumeIterator = volume.size - 1
                }

                Settings.sounds = volumeIterator + 1

                SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())

                TextToSpeechManager.speakNow(texts["SETTINGS_SOUNDS_VOLUME"].toString() + texts[volume[Settings.sounds - 1]].toString())

                swipe = true
            }
            GestureTypeEnum.SWIPE_UP -> {
                LevelManager.changeLevel(SettingsScene())
                Settings.globalSounds.playSound(RawResources.swapSound)
                swipe = true
            }
            GestureTypeEnum.SWIPE_DOWN -> {
                TextToSpeechManager.speakNow(texts["IDLE"].toString())
                Settings.globalSounds.playSound(RawResources.swapSound)
                idleSpeak.resetIdleTimeSeconds()
                swipe = true
            }
        }

        when (GestureManager.doubleTapDetect(event)) {

            GestureTypeEnum.DOUBLE_TAP -> {
                Settings.globalSounds.playSound(RawResources.acceptSound)

                LevelManager.changeLevel(SettingsScene())
            }
        }

        val holdPosition = GestureManager.holdPositionDetect(event).first
        if (holdPosition > 0 && !swipe) {
            when {
                holdPosition < Settings.SCREEN_WIDTH / 10 -> {
                    volumeIterator = 0
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 2 -> {
                    volumeIterator = 1
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 3 -> {
                    volumeIterator = 2
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 4 -> {
                    volumeIterator = 3
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 5 -> {
                    volumeIterator = 4
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 6 -> {
                    volumeIterator = 5
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 7 -> {
                    volumeIterator = 6
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 8 -> {
                    volumeIterator = 7
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 9 -> {
                    volumeIterator = 8
                }
                holdPosition < Settings.SCREEN_WIDTH / 10 * 10 -> {
                    volumeIterator = 9
                }
            }
            Settings.sounds = volumeIterator + 1

            SharedPreferencesManager.saveConfiguration("sounds", Settings.sounds.toString())
        }
    }

    override fun redrawState(canvas: Canvas) {
        optionText.drawText(canvas, (Settings.sounds * 10).toString())
    }
}