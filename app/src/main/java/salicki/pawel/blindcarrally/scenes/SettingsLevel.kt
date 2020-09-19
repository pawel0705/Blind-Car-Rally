package salicki.pawel.blindcarrally.scenes

import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import salicki.pawel.blindcarrally.*
import salicki.pawel.blindcarrally.scenemanager.ILevel
import salicki.pawel.blindcarrally.scenemanager.LevelManager
import salicki.pawel.blindcarrally.scenemanager.LevelType
import kotlin.math.abs

class SettingsLevel : SurfaceView(Settings.CONTEXT), ILevel {

    private var texts : HashMap<String, String> = HashMap()
    private var settingsIterator = 0

    private var sounds : HashMap<Float, String> = HashMap()
    private var speaker: HashMap<Float, String> = HashMap()

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

        sounds[0.1F]=" 10%."
        sounds[0.2F]=" 20%."
        sounds[0.3F]=" 30%."
        sounds[0.4F]=" 40%."
        sounds[0.5F]=" 50%."
        sounds[0.6F]=" 60%."
        sounds[0.7F]=" 70%."
        sounds[0.8F]=" 80%."
        sounds[0.9F]=" 90%."
        sounds[1.0F]=" 100%."

        speaker[0.1F]=" 10%."
        speaker[0.2F]=" 20%."
        speaker[0.3F]=" 30%."
        speaker[0.4F]=" 40%."
        speaker[0.5F]=" 50%."
        speaker[0.6F]=" 60%."
        speaker[0.7F]=" 70%."
        speaker[0.8F]=" 80%."
        speaker[0.9F]=" 90%."
        speaker[1.0F]=" 100%."

    }


    override fun initState() {
        texts.putAll(OpenerCSV.readData(R.raw.settings_tts, Settings.languageTTS))
        TextToSpeechManager.speakNow(texts["SETTINGS_TUTORIAL"].toString())
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
                        settingsIterator++

                        if(settingsIterator > 3){
                            settingsIterator = 0
                        }
                    } else {
                        settingsIterator--
                        if(settingsIterator < 0) {
                            settingsIterator = 3
                        }
                    }

                    duration = 0

                    when(settingsIterator){
                        0->{
                            if(Settings.vibrations){
                                TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_ON"].toString())
                            } else {
                                TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_OFF"].toString())
                            }
                        }
                        1->{
                            if(Settings.display){
                                TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_ON"].toString())
                            } else {
                                TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_OFF"].toString())
                            }
                        }
                        2->{
                            TextToSpeechManager.speakNow((texts["SETTINGS_TTS"]).toString())
                            TextToSpeechManager.speakQueue((texts["SETTINGS_TTS_VOLUME"]).toString() + speaker[Settings.lector])
                        }
                        3->{
                            TextToSpeechManager.speakNow((texts["SETTINGS_SOUNDS"]).toString())
                            TextToSpeechManager.speakQueue((texts["SETTINGS_SOUNDS_VOLUME"]).toString() + sounds[Settings.sounds])
                        }
                    }
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
                            when(settingsIterator){
                                0->{
                                    Settings.vibrations = !Settings.vibrations

                                    if(Settings.vibrations){
                                        TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_ON"].toString())
                                    } else {
                                        TextToSpeechManager.speakNow(texts["SETTINGS_VIBRATION_OFF"].toString())
                                    }
                                }
                                1->{
                                    Settings.display = !Settings.display

                                    if(Settings.display){
                                        TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_ON"].toString())
                                    } else {
                                        TextToSpeechManager.speakNow(texts["SETTINGS_DISPLAY_OFF"].toString())
                                    }
                                }
                                2->{

                                    Settings.lector+=0.1F

                                    if(Settings.lector > 1.0F){
                                        Settings.lector = 0.1F
                                    }

                                    TextToSpeechManager.speakQueue((texts["SETTINGS_TTS_VOLUME"]).toString() + speaker[Settings.lector])
                                }
                                3->{
                                    Settings.sounds+=0.1F

                                    if(Settings.sounds > 1.0F){
                                        Settings.sounds = 0.1F
                                    }

                                    TextToSpeechManager.speakQueue((texts["SETTINGS_SOUNDS_VOLUME"]).toString() + sounds[Settings.sounds])
                                }
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